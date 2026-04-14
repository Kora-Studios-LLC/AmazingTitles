package org.korastudios.amazingtitles.extension.progress;

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
		return "Progress";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Progress bar filling from left to right.
		AnimationBuilder fill = new AnimationBuilder(this, AnimationType.REPEATING, false);
		fill.setComponentArguments(ComponentArguments.create("Progress", "SubText is null", BarColor.GREEN, 10, 10, DisplayType.TITLE));
		fill.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String label = clean(arguments.getMainText());
			String visible = label.isEmpty() ? "PROGRESS" : label;
			for (int filled = 0; filled <= 12; filled++) {
				String frame = colorText(visible + " ", Color.decode("#d9fff0"), "f", true)
					+ renderBar(filled, 12, Color.decode("#5ad48f"), Color.decode("#274d38"), "a", "8", -1);
				frames.add(ColorTranslator.colorize(frame));
			}
			return frames;
		});
		fill.register("EXTENSION_PROGRESS_FILL");

		// Progress bar draining down with a cooler fade.
		AnimationBuilder drain = new AnimationBuilder(this, AnimationType.REPEATING, false);
		drain.setComponentArguments(ComponentArguments.create("Progress", "SubText is null", BarColor.GREEN, 10, 10, DisplayType.TITLE));
		drain.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String label = clean(arguments.getMainText());
			String visible = label.isEmpty() ? "PROGRESS" : label;
			for (int filled = 12; filled >= 0; filled--) {
				String frame = colorText(visible + " ", Color.decode("#d9fff0"), "f", true)
					+ renderBar(filled, 12, Color.decode("#52c7ff"), Color.decode("#203847"), "b", "8", -1);
				frames.add(ColorTranslator.colorize(frame));
			}
			return frames;
		});
		drain.register("EXTENSION_PROGRESS_DRAIN");

		// Fully active bar with one brighter segment pulsing through it.
		AnimationBuilder pulse = new AnimationBuilder(this, AnimationType.REPEATING, false);
		pulse.setComponentArguments(ComponentArguments.create("Progress", "SubText is null", BarColor.GREEN, 10, 10, DisplayType.TITLE));
		pulse.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String label = clean(arguments.getMainText());
			String visible = label.isEmpty() ? "PROGRESS" : label;
			for (int active = 0; active < 12; active++) {
				String frame = colorText(visible + " ", Color.decode("#d9fff0"), "f", true)
					+ renderBar(12, 12, Color.decode("#5ad48f"), Color.decode("#274d38"), "a", "8", active);
				frames.add(ColorTranslator.colorize(frame));
			}
			return frames;
		});
		pulse.register("EXTENSION_PROGRESS_PULSE");

	}

	private String renderBar(int filled, int total, Color fillColor, Color emptyColor, String fillLegacy, String emptyLegacy, int highlightIndex) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < total; i++) {
			boolean active = i < filled;
			boolean highlight = highlightIndex == i;
			Color color = active ? (highlight ? mixColors(fillColor, Color.decode("#ffffff"), 0.65) : fillColor) : emptyColor;
			String legacy = active ? (highlight ? "f" : fillLegacy) : emptyLegacy;
			builder.append(colorText(active ? "=" : "-", color, legacy, active));
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
