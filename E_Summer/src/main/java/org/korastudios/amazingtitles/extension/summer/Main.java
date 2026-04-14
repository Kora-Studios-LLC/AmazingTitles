package org.korastudios.amazingtitles.extension.summer;

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
		return "Summer";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Warm sun wraps the title in a simple two-step pulse.
		AnimationBuilder wrapped_sun = new AnimationBuilder(this, AnimationType.REPEATING, false);
		wrapped_sun.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.YELLOW, 10, 1, DisplayType.TITLE));
		wrapped_sun.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "SUMMER" : input;
			frames.add(ColorTranslator.colorize(colorText(visible, Color.decode("#ffcf4d"), "e", true)));
			frames.add(ColorTranslator.colorize("&l&6☀ &r" + colorText(visible, Color.decode("#fff0a8"), "e", true) + "&r &6☀"));
			return frames;
		});
		wrapped_sun.register("EXTENSION_SUMMER_WRAPPED_SUN");

		// Same sun wrap but with a configurable highlight color.
		AnimationBuilder wrapped_colored_sun = new AnimationBuilder(this, AnimationType.REPEATING, false, "Hex(Sun-Color)");
		wrapped_colored_sun.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.YELLOW, 10, 1, DisplayType.TITLE));
		wrapped_colored_sun.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "SUMMER" : input;
			Color sunColor = Color.decode(safeHex(arg(args, 0), "#ffd54f"));
			frames.add(ColorTranslator.colorize(colorText(visible, Color.decode("#ffcf4d"), "e", true)));
			frames.add(ColorTranslator.colorize(colorText("☀ ", sunColor, "6", true) + colorText(visible, mixColors(sunColor, Color.decode("#fff8c2"), 0.45), "e", true) + colorText(" ☀", sunColor, "6", true)));
			return frames;
		});
		wrapped_colored_sun.register("EXTENSION_SUMMER_WRAPPED_COLORED_SUN");

		// Stable summer gradient with optional bold styling.
		AnimationBuilder summer_gradient = new AnimationBuilder(this, AnimationType.REPEATING, false, "0/1(1=bold,0=normal)");
		summer_gradient.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.YELLOW, 10, 1, DisplayType.TITLE));
		summer_gradient.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			boolean bold = safeParseInt(arg(args, 0), 1) == 1;
			frames.add(ColorTranslator.colorize(renderSweep(input.isEmpty() ? "SUMMER" : input, 0.0, Color.decode("#ffad33"), Color.decode("#fff2a6"), "6", "e", bold)));
			return frames;
		});
		summer_gradient.register("EXTENSION_SUMMER_GRADIENT");

		// Bright summer wave that sweeps warm light across the text.
		AnimationBuilder summer_waves = new AnimationBuilder(this, AnimationType.REPEATING, false);
		summer_waves.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.YELLOW, 10, 10, DisplayType.TITLE));
		summer_waves.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "SUMMER" : input;
			for (int frame = 0; frame < visible.length() + 8; frame++) {
				double center = -2.0 + frame;
				frames.add(ColorTranslator.colorize(renderSweep(visible, center, Color.decode("#ff8a1f"), Color.decode("#fff3b0"), "6", "e", true)));
			}
			return frames;
		});
		summer_waves.register("EXTENSION_SUMMER_WAVES");

		// Elastic highlight expands through the text and rebounds gently.
		AnimationBuilder summer_bounce = new AnimationBuilder(this, AnimationType.REPEATING, false);
		summer_bounce.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.YELLOW, 10, 10, DisplayType.TITLE));
		summer_bounce.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "SUMMER" : input;
			int length = visible.length();
			for (int frame = 0; frame < length + 3; frame++) {
				double center = Math.min(length - 1, frame * 0.9);
				frames.add(ColorTranslator.colorize(renderSweep(visible, center, Color.decode("#ff9c2a"), Color.decode("#fff5bf"), "6", "e", true)));
			}
			for (int rebound = Math.max(length - 2, 0); rebound >= Math.max(length - 5, 0); rebound--) {
				frames.add(ColorTranslator.colorize(renderSweep(visible, rebound, Color.decode("#ff9c2a"), Color.decode("#fff8cf"), "6", "e", true)));
			}
			return frames;
		});
		summer_bounce.register("EXTENSION_SUMMER_BOUNCE");

		// Soft summer pulse between sunset orange and bright sunlight.
		AnimationBuilder summer_pulsing = new AnimationBuilder(this, AnimationType.REPEATING, false);
		summer_pulsing.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.YELLOW, 10, 8, DisplayType.TITLE));
		summer_pulsing.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "SUMMER" : input;
			Color dim = Color.decode("#ff9830");
			Color bright = Color.decode("#fff6bf");
			double[] ratios = new double[] {0.10, 0.30, 0.55, 0.80, 1.00, 0.70, 0.40, 0.20};
			for (double ratio : ratios) {
				Color frameColor = mixColors(dim, bright, ratio);
				boolean bold = ratio >= 0.55;
				frames.add(ColorTranslator.colorize(colorText(visible, frameColor, bold ? "e" : "6", bold)));
			}
			return frames;
		});
		summer_pulsing.register("EXTENSION_SUMMER_PULSING");

	}

	private String renderSweep(String input, double center, Color base, Color accent, String baseLegacy, String accentLegacy, boolean boldAccent) {
		if (input.isEmpty()) {
			return colorText("*", accent, accentLegacy, true);
		}
		StringBuilder builder = new StringBuilder();
		double radius = Math.max(1.15, input.length() / 3.25);
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			double ratio = Math.max(0.0, 1.0 - (distance / radius));
			Color shade = mixColors(base, accent, ratio);
			boolean bold = boldAccent && ratio > 0.45;
			String legacy = ratio > 0.35 ? accentLegacy : baseLegacy;
			builder.append(colorText(String.valueOf(input.charAt(i)), shade, legacy, bold));
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

	private String clean(String raw) {
		if (raw == null) {
			return "";
		}
		String colored = ColorTranslator.colorize(raw);
		String stripped = ColorTranslator.removeColors(colored);
		return stripped == null ? "" : stripped;
	}

}
