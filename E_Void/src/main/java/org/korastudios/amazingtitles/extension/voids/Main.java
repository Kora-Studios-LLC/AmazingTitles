package org.korastudios.amazingtitles.extension.voids;

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
		return "Void";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Dark rift opens through the center, exposing brighter void edges.
		AnimationBuilder rift = new AnimationBuilder(this, AnimationType.REPEATING, false);
		rift.setComponentArguments(ComponentArguments.create("Void", "SubText is null", BarColor.PURPLE, 10, 10, DisplayType.TITLE));
		rift.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "VOID" : input;
			double maxRadius = Math.max(1.0, visible.length() / 2.0);
			for (double radius = 0.0; radius <= maxRadius; radius += 0.55) {
				frames.add(ColorTranslator.colorize(renderRift(visible, radius)));
			}
			for (double radius = maxRadius - 0.55; radius >= 0.25; radius -= 0.55) {
				frames.add(ColorTranslator.colorize(renderRift(visible, radius)));
			}
			return frames;
		});
		rift.register("EXTENSION_VOID_RIFT");

		// Controlled glitch flicker with slight offsets and unstable highlight slices.
		AnimationBuilder glitch = new AnimationBuilder(this, AnimationType.REPEATING, false);
		glitch.setComponentArguments(ComponentArguments.create("Void", "SubText is null", BarColor.PURPLE, 10, 10, DisplayType.TITLE));
		glitch.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "VOID" : input;
			frames.add(ColorTranslator.colorize(colorText(visible, Color.decode("#8a5dff"), "5", true)));
			frames.add(ColorTranslator.colorize(" " + renderGlitch(visible, 1)));
			frames.add(ColorTranslator.colorize(renderGlitch(visible, 2)));
			frames.add(ColorTranslator.colorize("  " + renderGlitch(visible, 3)));
			frames.add(ColorTranslator.colorize(colorText(visible, Color.decode("#c6b8ff"), "d", true)));
			return frames;
		});
		glitch.register("EXTENSION_VOID_GLITCH");

		// Ominous pulse of dark energy that brightens the center and sinks back into shadow.
		AnimationBuilder pulse = new AnimationBuilder(this, AnimationType.REPEATING, false);
		pulse.setComponentArguments(ComponentArguments.create("Void", "SubText is null", BarColor.PURPLE, 10, 8, DisplayType.TITLE));
		pulse.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "VOID" : input;
			double[] levels = new double[] {0.12, 0.28, 0.50, 0.82, 1.00, 0.70, 0.35};
			for (double level : levels) {
				frames.add(ColorTranslator.colorize(renderPulse(visible, level)));
			}
			return frames;
		});
		pulse.register("EXTENSION_VOID_PULSE");

	}

	private String renderRift(String input, double radius) {
		StringBuilder builder = new StringBuilder();
		double center = (input.length() - 1) / 2.0;
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			if (distance < radius * 0.65) {
				builder.append(" ");
				continue;
			}
			double edgeRatio = Math.max(0.0, 1.0 - (Math.abs(distance - radius) / Math.max(0.65, radius)));
			Color shade = mixColors(Color.decode("#28113c"), Color.decode("#ad8dff"), edgeRatio);
			String legacy = edgeRatio > 0.42 ? "d" : "5";
			builder.append(colorText(String.valueOf(input.charAt(i)), shade, legacy, edgeRatio > 0.55));
		}
		return builder.toString();
	}

	private String renderGlitch(String input, int phase) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			int marker = (i + phase) % 4;
			Color shade = marker == 0 ? Color.decode("#cbbcff") : (marker == 1 ? Color.decode("#8256ff") : Color.decode("#40205e"));
			String legacy = marker == 0 ? "d" : (marker == 1 ? "5" : "8");
			boolean bold = marker != 2;
			builder.append(colorText(String.valueOf(input.charAt(i)), shade, legacy, bold));
		}
		return builder.toString();
	}

	private String renderPulse(String input, double level) {
		StringBuilder builder = new StringBuilder();
		double center = (input.length() - 1) / 2.0;
		double radius = Math.max(1.0, input.length() / 2.4);
		Color dim = Color.decode("#261034");
		Color bright = Color.decode("#b694ff");
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			double focus = Math.max(0.0, 1.0 - (distance / radius));
			double ratio = Math.min(1.0, 0.10 + level * (0.50 + focus * 0.50));
			Color shade = mixColors(dim, bright, ratio);
			String legacy = ratio > 0.50 ? "d" : "5";
			builder.append(colorText(String.valueOf(input.charAt(i)), shade, legacy, ratio > 0.62));
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
