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
			for (int offset = 0; offset < visible.length(); offset++) {
				frames.add(ColorTranslator.colorize(renderDust(visible, offset)));
			}
			return frames;
		});
		cosmic_dust.register("EXTENSION_COSMIC_DUST");

	}

	private String renderSwirl(String input, int offset) {
		StringBuilder builder = new StringBuilder();
		// Smooth looping palette: cyan → violet → pink → violet → (back to cyan)
		Color[] palette = new Color[] {
			Color.decode("#56d7ff"),
			Color.decode("#8e7dff"),
			Color.decode("#ff88d1"),
			Color.decode("#8e7dff")
		};
		String[] legacy = new String[] {"b", "9", "d", "9"};
		int cycleWidth = palette.length * 3; // chars per full color cycle
		for (int i = 0; i < input.length(); i++) {
			double phase = ((double)(i + offset) / cycleWidth) * palette.length;
			phase = phase % palette.length;
			int from = (int) phase;
			int to = (from + 1) % palette.length;
			double t = phase - from;
			Color c = mixColors(palette[from], palette[to], t);
			builder.append(colorText(String.valueOf(input.charAt(i)), c, legacy[from], false));
		}
		return builder.toString();
	}

	private String renderNova(String input, double radius) {
		StringBuilder builder = new StringBuilder();
		double center = (input.length() - 1) / 2.0;
		Color edge = Color.decode("#4a52e0");   // deep indigo base
		Color burst = Color.decode("#d8f0ff");  // cool bright white-blue core
		for (int i = 0; i < input.length(); i++) {
			double distance = Math.abs(i - center);
			// Glow: characters near the expanding radius ring brighten
			double proximity = Math.abs(distance - radius);
			double glow = Math.max(0.0, 1.0 - proximity / 1.2);
			glow = Math.pow(glow, 2.0); // sharper falloff for cleaner ring
			Color shade = mixColors(edge, burst, glow);
			String legacy = glow > 0.4 ? "f" : "9";
			builder.append(colorText(String.valueOf(input.charAt(i)), shade, legacy, false));
		}
		return builder.toString();
	}

	private String renderDust(String input, int offset) {
		StringBuilder builder = new StringBuilder();
		Color base = Color.decode("#7a70e8");    // muted indigo base
		Color near = Color.decode("#bdb0ff");    // soft lavender halo
		Color glow = Color.decode("#eee8ff");    // near-white sparkle
		int len = input.length();
		int sparkle = len > 0 ? offset % len : 0;
		for (int i = 0; i < len; i++) {
			int dist = Math.abs(i - sparkle);
			dist = Math.min(dist, len - dist); // wrap-around distance
			Color c;
			String legacy;
			if (dist == 0) { c = glow;  legacy = "f"; }
			else if (dist == 1) { c = near;  legacy = "d"; }
			else { c = base;  legacy = "9"; }
			builder.append(colorText(String.valueOf(input.charAt(i)), c, legacy, false));
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
