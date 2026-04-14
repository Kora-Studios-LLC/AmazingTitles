package org.korastudios.amazingtitles.extension.glow;

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
		return "Glow";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Bright highlight sweeps across the text like a polished light reflection.
		AnimationBuilder glow_sweep = new AnimationBuilder(this, AnimationType.REPEATING, false);
		glow_sweep.setComponentArguments(ComponentArguments.create("Glow", "SubText is null", BarColor.WHITE, 10, 10, DisplayType.TITLE));
		glow_sweep.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "GLOW" : input;
			for (int frame = 0; frame < visible.length() + 6; frame++) {
				frames.add(ColorTranslator.colorize(renderSweep(visible, -1.5 + frame, Color.decode("#69d6ff"), Color.decode("#ffffff"), "b", "f", true)));
			}
			return frames;
		});
		glow_sweep.register("EXTENSION_GLOW_SWEEP");

		// Gentle glow breathing between soft light and stronger luminous emphasis.
		AnimationBuilder glow_pulse = new AnimationBuilder(this, AnimationType.REPEATING, false);
		glow_pulse.setComponentArguments(ComponentArguments.create("Glow", "SubText is null", BarColor.WHITE, 10, 8, DisplayType.TITLE));
		glow_pulse.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "GLOW" : input;
			double[] levels = new double[] {0.18, 0.35, 0.58, 0.82, 1.00, 0.70, 0.40};
			for (double level : levels) {
				frames.add(ColorTranslator.colorize(renderPulse(visible, level, Color.decode("#6ed8ff"), Color.decode("#ffffff"), "b", "f")));
			}
			return frames;
		});
		glow_pulse.register("EXTENSION_GLOW_PULSE");

		// Premium core glow with a stable bright center and softer outer edges.
		AnimationBuilder glow_core = new AnimationBuilder(this, AnimationType.REPEATING, false);
		glow_core.setComponentArguments(ComponentArguments.create("Glow", "SubText is null", BarColor.WHITE, 10, 8, DisplayType.TITLE));
		glow_core.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "GLOW" : input;
			double[] focuses = new double[] {0.55, 0.72, 0.88, 1.00, 0.84, 0.66};
			for (double focus : focuses) {
				frames.add(ColorTranslator.colorize(renderCore(visible, focus)));
			}
			return frames;
		});
		glow_core.register("EXTENSION_GLOW_CORE");

	}

	private String renderSweep(String input, double center, Color base, Color accent, String baseLegacy, String accentLegacy, boolean boldAccent) {
		if (input.isEmpty()) {
			return colorText("*", accent, accentLegacy, true);
		}
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

	private String renderPulse(String input, double level, Color dim, Color bright, String dimLegacy, String brightLegacy) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			Color shade = mixColors(dim, bright, level);
			boolean bold = level > 0.58;
			String legacy = level > 0.45 ? brightLegacy : dimLegacy;
			builder.append(colorText(String.valueOf(input.charAt(i)), shade, legacy, bold));
		}
		return builder.toString();
	}

	private String renderCore(String input, double focus) {
		if (input.isEmpty()) {
			return colorText("*", Color.decode("#ffffff"), "f", true);
		}
		StringBuilder builder = new StringBuilder();
		double center = (input.length() - 1) / 2.0;
		double radius = Math.max(1.0, input.length() / 2.5);
		Color rim = Color.decode("#7edcff");
		Color core = mixColors(Color.decode("#d8f8ff"), Color.decode("#ffffff"), focus);
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			double ratio = Math.max(0.0, 1.0 - (distance / radius));
			Color shade = mixColors(rim, core, ratio * focus);
			boolean bold = ratio > 0.55;
			String legacy = ratio > 0.48 ? "f" : "b";
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
