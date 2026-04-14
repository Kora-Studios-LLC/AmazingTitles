package org.korastudios.amazingtitles.extension.cosmic;

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
		return "Cosmic";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Multicolor cosmic sweep that spirals through cyan, violet, and pink.
		AnimationBuilder cosmic_swirl = new AnimationBuilder(this, AnimationType.REPEATING, false);
		cosmic_swirl.setComponentArguments(ComponentArguments.create("Cosmic", "SubText is null", BarColor.PURPLE, 10, 10, DisplayType.TITLE));
		cosmic_swirl.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "COSMIC" : input;
			for (int offset = 0; offset < visible.length() + 8; offset++) {
				frames.add(ColorTranslator.colorize(renderSwirl(visible, offset)));
			}
			return frames;
		});
		cosmic_swirl.register("EXTENSION_COSMIC_SWIRL");

		// Center burst expands outward with a bright stellar core.
		AnimationBuilder cosmic_nova = new AnimationBuilder(this, AnimationType.REPEATING, false);
		cosmic_nova.setComponentArguments(ComponentArguments.create("Cosmic", "SubText is null", BarColor.PURPLE, 10, 10, DisplayType.TITLE));
		cosmic_nova.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "COSMIC" : input;
			double maxRadius = Math.max(1.0, visible.length() / 2.0);
			for (double radius = 0.0; radius <= maxRadius; radius += 0.55) {
				frames.add(ColorTranslator.colorize(renderNova(visible, radius)));
			}
			for (double radius = maxRadius - 0.55; radius >= 0.35; radius -= 0.55) {
				frames.add(ColorTranslator.colorize(renderNova(visible, radius)));
			}
			return frames;
		});
		cosmic_nova.register("EXTENSION_COSMIC_NOVA");

		// Subtle stardust shimmer with a premium slow-glow finish.
		AnimationBuilder cosmic_dust = new AnimationBuilder(this, AnimationType.REPEATING, false);
		cosmic_dust.setComponentArguments(ComponentArguments.create("Cosmic", "SubText is null", BarColor.PURPLE, 10, 8, DisplayType.TITLE));
		cosmic_dust.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			String visible = input.isEmpty() ? "COSMIC" : input;
			for (int offset = 0; offset < visible.length() + 6; offset++) {
				frames.add(ColorTranslator.colorize(renderDust(visible, offset)));
			}
			return frames;
		});
		cosmic_dust.register("EXTENSION_COSMIC_DUST");

	}

	private String renderSwirl(String input, int offset) {
		StringBuilder builder = new StringBuilder();
		Color[] palette = new Color[] {
			Color.decode("#56d7ff"),
			Color.decode("#8e7dff"),
			Color.decode("#ff88d1")
		};
		String[] legacy = new String[] {"b", "9", "d"};
		for (int i = 0; i < input.length(); i++) {
			int slot = Math.abs(i + offset) % palette.length;
			boolean bold = (i + offset) % 2 == 0;
			builder.append(colorText(String.valueOf(input.charAt(i)), palette[slot], legacy[slot], bold));
		}
		return builder.toString();
	}

	private String renderNova(String input, double radius) {
		StringBuilder builder = new StringBuilder();
		double center = (input.length() - 1) / 2.0;
		Color edge = Color.decode("#5d63ff");
		Color burst = Color.decode("#fff4ff");
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			double ratio = Math.max(0.0, 1.0 - (Math.abs(distance - radius) / Math.max(0.85, radius + 0.25)));
			Color shade = mixColors(edge, burst, ratio);
			String legacy = ratio > 0.48 ? "f" : "9";
			builder.append(colorText(String.valueOf(input.charAt(i)), shade, legacy, ratio > 0.55));
		}
		return builder.toString();
	}

	private String renderDust(String input, int offset) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			int marker = (i + offset) % 5;
			Color shade;
			String legacy;
			boolean bold = false;
			if (marker == 0) {
				shade = Color.decode("#fff4ff");
				legacy = "f";
				bold = true;
			} else if (marker == 2) {
				shade = Color.decode("#9a86ff");
				legacy = "d";
			} else {
				shade = Color.decode("#59cfff");
				legacy = "b";
			}
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
