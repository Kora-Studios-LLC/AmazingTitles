package org.korastudios.amazingtitles.extension.fire;

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
		return "Fire";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Ember sparks flicker through the text with hot accent flashes.
		AnimationBuilder embers = new AnimationBuilder(this, AnimationType.REPEATING, false);
		embers.setComponentArguments(ComponentArguments.create("Fire", "SubText is null", BarColor.RED, 10, 10, DisplayType.TITLE));
		embers.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "FIRE" : input;
			for (int offset = 0; offset < visible.length() + 6; offset++) {
				frames.add(ColorTranslator.colorize(renderEmbers(visible, offset)));
			}
			return frames;
		});
		embers.register("EXTENSION_FIRE_EMBERS");

		// A hot blaze wave travels across the text with a bright yellow leading edge.
		AnimationBuilder blaze_wave = new AnimationBuilder(this, AnimationType.REPEATING, false);
		blaze_wave.setComponentArguments(ComponentArguments.create("Fire", "SubText is null", BarColor.RED, 10, 10, DisplayType.TITLE));
		blaze_wave.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "FIRE" : input;
			for (int frame = 0; frame < visible.length() + 5; frame++) {
				frames.add(ColorTranslator.colorize(renderSweep(visible, -1.0 + frame, Color.decode("#b82218"), Color.decode("#ffd966"), "c", "e", true)));
			}
			return frames;
		});
		blaze_wave.register("EXTENSION_FIRE_BLAZE_WAVE");

		// Fiery heat pulse intensifies and cools with a molten rhythm.
		AnimationBuilder heat_pulse = new AnimationBuilder(this, AnimationType.REPEATING, false);
		heat_pulse.setComponentArguments(ComponentArguments.create("Fire", "SubText is null", BarColor.RED, 10, 8, DisplayType.TITLE));
		heat_pulse.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "FIRE" : input;
			double[] levels = new double[] {0.15, 0.35, 0.62, 0.88, 1.00, 0.70, 0.38};
			for (double level : levels) {
				Color shade = mixColors(Color.decode("#8b130b"), Color.decode("#ffb347"), level);
				boolean bold = level > 0.55;
				String legacy = level > 0.50 ? "e" : "c";
				frames.add(ColorTranslator.colorize(colorText(visible, shade, legacy, bold)));
			}
			return frames;
		});
		heat_pulse.register("EXTENSION_FIRE_HEAT_PULSE");

	}

	private String renderEmbers(String input, int offset) {
		if (input.isEmpty()) {
			return colorText("*", Color.decode("#ffd966"), "e", true);
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			int marker = (i + offset) % 5;
			Color shade;
			String legacy;
			boolean bold;
			if (marker == 0) {
				shade = Color.decode("#ffd966");
				legacy = "e";
				bold = true;
			} else if (marker == 2) {
				shade = Color.decode("#ff8a3d");
				legacy = "6";
				bold = true;
			} else {
				shade = Color.decode("#c92b1c");
				legacy = "c";
				bold = false;
			}
			builder.append(colorText(String.valueOf(input.charAt(i)), shade, legacy, bold));
		}
		return builder.toString();
	}

	private String renderSweep(String input, double center, Color base, Color accent, String baseLegacy, String accentLegacy, boolean boldAccent) {
		StringBuilder builder = new StringBuilder();
		double radius = Math.max(1.05, input.length() / 3.0);
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			double ratio = Math.max(0.0, 1.0 - (distance / radius));
			Color shade = mixColors(base, accent, ratio);
			boolean bold = boldAccent && ratio > 0.45;
			String legacy = ratio > 0.30 ? accentLegacy : baseLegacy;
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
