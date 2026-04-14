package org.korastudios.amazingtitles.extension.timer;

import org.bukkit.boss.BarColor;
import org.korastudios.amazingtitles.code.api.builders.AnimationBuilder;
import org.korastudios.amazingtitles.code.api.enums.AnimationType;
import org.korastudios.amazingtitles.code.api.enums.DisplayType;
import org.korastudios.amazingtitles.code.api.interfaces.AmazingExtension;
import org.korastudios.amazingtitles.code.internal.components.ComponentArguments;
import org.korastudios.amazingtitles.code.internal.utils.ColorTranslator;

import java.awt.Color;
import java.util.LinkedList;

public class Main implements AmazingExtension {

	@Override
	public String extension_name() {
		return "Timer";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Configurable countdown that changes palette as it approaches the final seconds.
		AnimationBuilder countdown = new AnimationBuilder(this, AnimationType.REPEATING, false, "startNumber", "normalHex", "warningHex", "dangerHex");
		countdown.setComponentArguments(ComponentArguments.create("Timer", "SubText is null", BarColor.WHITE, 10, 8, DisplayType.TITLE));
		countdown.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String label = clean(arguments.getMainText());
			int start = Math.max(0, Math.min(60, safeParseInt(arg(args, 0), 10)));
			Color normal = Color.decode(safeHex(arg(args, 1), "#5fd5ff"));
			Color warning = Color.decode(safeHex(arg(args, 2), "#ffca57"));
			Color danger = Color.decode(safeHex(arg(args, 3), "#ff5a5a"));
			for (int value = start; value >= 0; value--) {
				Color base = value > 5 ? normal : (value >= 3 ? warning : danger);
				String legacy = value > 5 ? "b" : (value >= 3 ? "e" : "c");
				String text = label.isEmpty() ? String.valueOf(value) : label + " " + value;
				boolean emphasis = value <= 2;
				frames.add(ColorTranslator.colorize(colorText(text, base, legacy, emphasis)));
				if (emphasis) {
					Color flash = mixColors(base, Color.decode("#fff2f2"), 0.65);
					frames.add(ColorTranslator.colorize(colorText(text, flash, "f", true)));
				}
			}
			return frames;
		});
		countdown.register("EXTENSION_TIMER_COUNTDOWN");

		// Title-friendly progress bar that visually drains as the timer advances.
		AnimationBuilder progress_bar = new AnimationBuilder(this, AnimationType.REPEATING, false, "totalSteps(optional)");
		progress_bar.setComponentArguments(ComponentArguments.create("Timer", "SubText is null", BarColor.GREEN, 10, 10, DisplayType.TITLE));
		progress_bar.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String label = clean(arguments.getMainText());
			String visible = label.isEmpty() ? "TIME" : label;
			int total = Math.max(4, Math.min(30, safeParseInt(arg(args, 0), 10)));
			int barSize = 12;
			for (int remaining = total; remaining >= 0; remaining--) {
				double progress = total == 0 ? 0.0 : remaining / (double) total;
				int filled = (int) Math.round(progress * barSize);
				Color fill = mixColors(Color.decode("#ff5959"), Color.decode("#62e594"), progress);
				String frame = colorText(visible + " ", Color.decode("#d9f7ff"), "f", true)
					+ renderBar(filled, barSize, fill, Color.decode("#335a40"), "a", "8")
					+ colorText(" " + remaining + "s", fill, progress > 0.45 ? "a" : "e", true);
				frames.add(ColorTranslator.colorize(frame));
			}
			return frames;
		});
		progress_bar.register("EXTENSION_TIMER_PROGRESS_BAR");

		// Dramatic final-seconds sequence with strong pulse and blink accents for 3, 2, 1, and 0.
		AnimationBuilder last_seconds = new AnimationBuilder(this, AnimationType.REPEATING, false);
		last_seconds.setComponentArguments(ComponentArguments.create("Final", "SubText is null", BarColor.RED, 10, 10, DisplayType.TITLE));
		last_seconds.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String label = clean(arguments.getMainText());
			String visible = label.isEmpty() ? "FINAL" : label;
			for (int value = 3; value >= 0; value--) {
				Color base = mixColors(Color.decode("#ffcf6a"), Color.decode("#ff4f4f"), (4 - value) / 4.0);
				String text = visible + " " + value;
				frames.add(ColorTranslator.colorize(colorText(text, base, value > 1 ? "e" : "c", true)));
				frames.add(ColorTranslator.colorize(colorText(text, Color.decode("#fff5f5"), "f", true)));
				if (value > 0) {
					frames.add(ColorTranslator.colorize(colorText(text, mixColors(base, Color.decode("#350808"), 0.55), "8", false)));
				}
			}
			return frames;
		});
		last_seconds.register("EXTENSION_TIMER_LAST_SECONDS");

	}

	private String renderBar(int filled, int total, Color filledColor, Color emptyColor, String filledLegacy, String emptyLegacy) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < total; i++) {
			boolean active = i < filled;
			builder.append(colorText(active ? "=" : "-", active ? filledColor : emptyColor, active ? filledLegacy : emptyLegacy, active));
		}
		return builder.toString();
	}

	private String colorText(String text, Color color, String legacyCode, boolean bold) {
		if (text == null || text.isEmpty()) {
			return "";
		}
		String prefix = bold ? "&l" : "";
		if (ColorTranslator.isHexSupport()) {
			String hex = toHex(color);
			return "<" + hex + ">" + prefix + text + "</" + hex + ">";
		}
		return prefix + "&" + legacyCode + text;
	}

	private String clean(String raw) {
		if (raw == null) {
			return "";
		}
		String colored = ColorTranslator.colorize(raw);
		String stripped = ColorTranslator.removeColors(colored);
		return stripped == null ? "" : stripped;
	}

	private String arg(String[] args, int index) {
		if (args == null || index < 0 || index >= args.length || args[index] == null) {
			return "";
		}
		return args[index];
	}

	private int safeParseInt(String raw, int fallback) {
		try {
			return Integer.parseInt(raw);
		} catch (Exception ignored) {
			return fallback;
		}
	}

	private String safeHex(String raw, String fallback) {
		if (raw == null) {
			return fallback;
		}
		String value = raw.trim();
		if (value.matches("#?[0-9a-fA-F]{6}")) {
			return value.startsWith("#") ? value : "#" + value;
		}
		return fallback;
	}

	private Color mixColors(Color from, Color to, double ratio) {
		double fixed = Math.max(0.0, Math.min(1.0, ratio));
		int r = (int) Math.round(from.getRed() + (to.getRed() - from.getRed()) * fixed);
		int g = (int) Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * fixed);
		int b = (int) Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * fixed);
		return new Color(r, g, b);
	}

	private String toHex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

}
