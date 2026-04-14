package org.korastudios.amazingtitles.extension.bounce;

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
		return "Bounce";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Elastic center-out reveal with a small overshoot, rebound, and stable finish.
		AnimationBuilder center_reveal = new AnimationBuilder(this, AnimationType.REPEATING, false);
		center_reveal.setComponentArguments(ComponentArguments.create("Bounce", "SubText is null", BarColor.BLUE, 10, 12, DisplayType.TITLE));
		center_reveal.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "BOUNCE" : input;
			double maxRadius = Math.max(1.0, visible.length() / 2.0);
			double[] radii = new double[] {
				0.10,
				maxRadius * 0.30,
				maxRadius * 0.55,
				maxRadius * 0.78,
				maxRadius,
				maxRadius + 0.65,
				maxRadius - 0.18,
				maxRadius + 0.10,
				maxRadius
			};
			for (double radius : radii) {
				frames.add(ColorTranslator.colorize(renderCenterReveal(visible, radius)));
			}
			frames.add(ColorTranslator.colorize(renderSweep(visible, (visible.length() - 1) / 2.0, Color.decode("#3db8ff"), Color.decode("#fff2a3"), "b", "e", true)));
			return frames;
		});
		center_reveal.register("EXTENSION_BOUNCE_CENTER_REVEAL");

		// Energetic highlight wave that pushes forward and rebounds slightly back.
		AnimationBuilder elastic_wave = new AnimationBuilder(this, AnimationType.REPEATING, false);
		elastic_wave.setComponentArguments(ComponentArguments.create("Bounce", "SubText is null", BarColor.BLUE, 10, 12, DisplayType.TITLE));
		elastic_wave.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "BOUNCE" : input;
			for (int frame = 0; frame < visible.length() + 4; frame++) {
				frames.add(ColorTranslator.colorize(renderSweep(visible, -1.0 + frame, Color.decode("#3fbcff"), Color.decode("#fff0a0"), "b", "e", true)));
			}
			for (int rebound = Math.max(visible.length() - 2, 0); rebound >= Math.max(visible.length() - 5, 0); rebound--) {
				frames.add(ColorTranslator.colorize(renderSweep(visible, rebound, Color.decode("#3fbcff"), Color.decode("#ffd37a"), "b", "6", true)));
			}
			return frames;
		});
		elastic_wave.register("EXTENSION_BOUNCE_ELASTIC_WAVE");

		// Brightness pulse with a playful center-weighted bounce feel for titles.
		AnimationBuilder bounce_pulse = new AnimationBuilder(this, AnimationType.REPEATING, false);
		bounce_pulse.setComponentArguments(ComponentArguments.create("Bounce", "SubText is null", BarColor.BLUE, 10, 10, DisplayType.TITLE));
		bounce_pulse.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "BOUNCE" : input;
			double[] strengths = new double[] {0.15, 0.35, 0.60, 0.90, 0.65, 0.82, 0.45};
			for (double strength : strengths) {
				frames.add(ColorTranslator.colorize(renderPulse(visible, strength)));
			}
			return frames;
		});
		bounce_pulse.register("EXTENSION_BOUNCE_PULSE");

	}

	private String renderCenterReveal(String input, double radius) {
		if (input.isEmpty()) {
			return colorText("*", Color.decode("#fff0a0"), "e", true);
		}
		StringBuilder builder = new StringBuilder();
		double center = (input.length() - 1) / 2.0;
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			if (distance > radius + 0.35) {
				builder.append(" ");
				continue;
			}
			double ratio = 1.0 - Math.min(1.0, distance / Math.max(radius + 0.20, 0.35));
			Color edge = Color.decode("#46c2ff");
			Color core = radius > input.length() / 2.0 ? Color.decode("#ffd884") : Color.decode("#fff3b8");
			Color shade = mixColors(edge, core, ratio);
			boolean bold = ratio > 0.40 || radius >= input.length() / 2.0;
			String legacy = ratio > 0.55 ? "e" : "b";
			builder.append(colorText(String.valueOf(input.charAt(i)), shade, legacy, bold));
		}
		return builder.toString();
	}

	private String renderSweep(String input, double center, Color base, Color accent, String baseLegacy, String accentLegacy, boolean boldAccent) {
		if (input.isEmpty()) {
			return colorText("*", accent, accentLegacy, true);
		}
		StringBuilder builder = new StringBuilder();
		double radius = Math.max(1.10, input.length() / 3.0);
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

	private String renderPulse(String input, double strength) {
		if (input.isEmpty()) {
			return colorText("*", Color.decode("#fff0a0"), "e", true);
		}
		StringBuilder builder = new StringBuilder();
		double center = (input.length() - 1) / 2.0;
		double radius = Math.max(1.0, input.length() / 2.4);
		Color dim = Color.decode("#3997d8");
		Color bright = Color.decode("#fff3ad");
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			double focus = Math.max(0.0, 1.0 - (distance / radius));
			double ratio = Math.min(1.0, 0.18 + strength * (0.50 + focus * 0.50));
			Color shade = mixColors(dim, bright, ratio);
			boolean bold = ratio > 0.68;
			String legacy = ratio > 0.52 ? "e" : "b";
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
