package org.korastudios.amazingtitles.extension.alert;

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
		return "Alert";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Strong amber warning pulse for attention-grabbing notices.
		AnimationBuilder warning = new AnimationBuilder(this, AnimationType.REPEATING, false);
		warning.setComponentArguments(ComponentArguments.create("Warning", "SubText is null", BarColor.YELLOW, 10, 8, DisplayType.TITLE));
		warning.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "WARNING" : input;
			double[] levels = new double[] {0.20, 0.45, 0.75, 1.00, 0.72, 0.38};
			for (double level : levels) {
				Color shade = mixColors(Color.decode("#8c5200"), Color.decode("#ffd166"), level);
				boolean bold = level > 0.50;
				frames.add(ColorTranslator.colorize(colorText(visible, shade, bold ? "e" : "6", bold)));
			}
			return frames;
		});
		warning.register("EXTENSION_ALERT_WARNING");

		// Aggressive danger flash with harder red-white hits.
		AnimationBuilder danger = new AnimationBuilder(this, AnimationType.REPEATING, false);
		danger.setComponentArguments(ComponentArguments.create("Danger", "SubText is null", BarColor.RED, 10, 10, DisplayType.TITLE));
		danger.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "DANGER" : input;
			Color[] palette = new Color[] {
				Color.decode("#4a0a0a"),
				Color.decode("#b51c1c"),
				Color.decode("#ff4d4d"),
				Color.decode("#fff0f0"),
				Color.decode("#ff4d4d"),
				Color.decode("#861010")
			};
			for (int i = 0; i < palette.length; i++) {
				boolean bold = i >= 2 && i <= 4;
				String legacy = i == 3 ? "f" : "c";
				frames.add(ColorTranslator.colorize(colorText(visible, palette[i], legacy, bold)));
			}
			return frames;
		});
		danger.register("EXTENSION_ALERT_DANGER");

		// Restart alert ramps through warning tones into urgent red flashes.
		AnimationBuilder restart = new AnimationBuilder(this, AnimationType.REPEATING, false);
		restart.setComponentArguments(ComponentArguments.create("Restart", "SubText is null", BarColor.RED, 10, 10, DisplayType.TITLE));
		restart.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "RESTART" : input;
			frames.add(ColorTranslator.colorize(colorText(">> " + visible + " <<", Color.decode("#ffb347"), "e", true)));
			frames.add(ColorTranslator.colorize(colorText(">> " + visible + " <<", Color.decode("#ff7043"), "6", true)));
			frames.add(ColorTranslator.colorize(colorText(">> " + visible + " <<", Color.decode("#ff3b3b"), "c", true)));
			frames.add(ColorTranslator.colorize(colorText(">> " + visible + " <<", Color.decode("#fff5f5"), "f", true)));
			frames.add(ColorTranslator.colorize(colorText(">> " + visible + " <<", Color.decode("#ff3b3b"), "c", true)));
			return frames;
		});
		restart.register("EXTENSION_ALERT_RESTART");

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
