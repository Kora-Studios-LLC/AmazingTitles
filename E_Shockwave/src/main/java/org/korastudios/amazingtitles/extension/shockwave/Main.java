package org.korastudios.amazingtitles.extension.shockwave;

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
		return "Shockwave";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Impact ignites at the center and expands outward as a single strong wave.
		AnimationBuilder shockwave_center = new AnimationBuilder(this, AnimationType.REPEATING, false);
		shockwave_center.setComponentArguments(ComponentArguments.create("Shockwave", "SubText is null", BarColor.WHITE, 10, 10, DisplayType.TITLE));
		shockwave_center.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "SHOCKWAVE" : input;
			double maxRadius = Math.max(1.0, visible.length() / 2.0 + 0.5);
			for (double radius = 0.0; radius <= maxRadius; radius += 0.55) {
				frames.add(ColorTranslator.colorize(renderWave(visible, radius, -99.0)));
			}
			return frames;
		});
		shockwave_center.register("EXTENSION_SHOCKWAVE_CENTER");

		// Two consecutive expanding rings create a layered impact effect.
		AnimationBuilder shockwave_double = new AnimationBuilder(this, AnimationType.REPEATING, false);
		shockwave_double.setComponentArguments(ComponentArguments.create("Shockwave", "SubText is null", BarColor.WHITE, 10, 10, DisplayType.TITLE));
		shockwave_double.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "SHOCKWAVE" : input;
			double maxRadius = Math.max(1.0, visible.length() / 2.0 + 1.0);
			for (double radius = 0.0; radius <= maxRadius; radius += 0.55) {
				frames.add(ColorTranslator.colorize(renderWave(visible, radius, radius - 1.45)));
			}
			return frames;
		});
		shockwave_double.register("EXTENSION_SHOCKWAVE_DOUBLE");

		// Expansion travels out, then a softer reverse pull returns toward the center.
		AnimationBuilder shockwave_rebound = new AnimationBuilder(this, AnimationType.REPEATING, false);
		shockwave_rebound.setComponentArguments(ComponentArguments.create("Shockwave", "SubText is null", BarColor.WHITE, 10, 10, DisplayType.TITLE));
		shockwave_rebound.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "SHOCKWAVE" : input;
			double maxRadius = Math.max(1.0, visible.length() / 2.0 + 0.75);
			for (double radius = 0.0; radius <= maxRadius; radius += 0.65) {
				frames.add(ColorTranslator.colorize(renderWave(visible, radius, -99.0)));
			}
			for (double radius = maxRadius - 0.65; radius >= 0.35; radius -= 0.65) {
				frames.add(ColorTranslator.colorize(renderWave(visible, radius, -99.0)));
			}
			return frames;
		});
		shockwave_rebound.register("EXTENSION_SHOCKWAVE_REBOUND");

	}

	private String renderWave(String input, double primaryRadius, double secondaryRadius) {
		if (input.isEmpty()) {
			return colorText("*", Color.decode("#effcff"), "f", true);
		}
		StringBuilder builder = new StringBuilder();
		double center = (input.length() - 1) / 2.0;
		double bandWidth = 0.85;
		Color idle = Color.decode("#4db8e8");
		Color ring = Color.decode("#ffffff");
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			double primary = Math.max(0.0, 1.0 - (Math.abs(distance - primaryRadius) / bandWidth));
			double secondary = secondaryRadius < 0 ? 0.0 : Math.max(0.0, 1.0 - (Math.abs(distance - secondaryRadius) / bandWidth));
			double ratio = Math.max(primary, secondary * 0.75);
			Color shade = mixColors(idle, ring, ratio);
			boolean bold = ratio > 0.55;
			String legacy = ratio > 0.35 ? "f" : "b";
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
