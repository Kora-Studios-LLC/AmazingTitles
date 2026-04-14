package org.korastudios.amazingtitles.extension.winter;

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
		return "Winter";
	}

	@Override
	public void unload() {

	}

	@Override
	public void load() {

		// Falling snow with icy gradient text
		AnimationBuilder snowfall = new AnimationBuilder(this, AnimationType.REPEATING, false);
		snowfall.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.BLUE, 10, 1, DisplayType.TITLE));
		snowfall.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			if (input.isEmpty()) {
				frames.add(ColorTranslator.colorize("<#e8f8ff>*</#b6ddff>"));
				return frames;
			}
			int totalFrames = input.length() + 12;
			for (int offset = 0; offset < totalFrames; offset++) {
				StringBuilder builder = new StringBuilder("&l");
				for (int i = 0; i < input.length(); i++) {
					int marker = (i + offset) % 6;
					if (marker == 0) builder.append("<#e8f8ff>*</#b6ddff>");
					else if (marker == 3) builder.append("<#d7efff>.</#b6ddff>");
					else builder.append(" ");
					builder.append("<#8fcaff>").append(input.charAt(i)).append("</#d7f3ff>");
				}
				frames.add(ColorTranslator.colorize(builder.toString()));
			}
			return frames;
		});
		snowfall.register("EXTENSION_WINTER_SNOWFALL");

		// Frost reveal gradient
		AnimationBuilder frost_reveal = new AnimationBuilder(this, AnimationType.REPEATING, false);
		frost_reveal.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.BLUE, 10, 1, DisplayType.TITLE));
		frost_reveal.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			if (input.isEmpty()) {
				frames.add(ColorTranslator.colorize("<#a7dbff>*</#e4f5ff>"));
				return frames;
			}
			int length = input.length();
			for (int step = 0; step <= length; step++) {
				String frozen = input.substring(0, step);
				String soft = input.substring(step);
				String frame = "&l<#74c5ff>" + frozen + "</#e4f5ff><#cdeaff>" + soft + "</#cdeaff>";
				frames.add(ColorTranslator.colorize(frame));
			}
			for (int step = length - 1; step >= 0; step--) {
				String frozen = input.substring(0, step);
				String soft = input.substring(step);
				String frame = "&l<#74c5ff>" + frozen + "</#e4f5ff><#cdeaff>" + soft + "</#cdeaff>";
				frames.add(ColorTranslator.colorize(frame));
			}
			return frames;
		});
		frost_reveal.register("EXTENSION_WINTER_FROST_REVEAL");

		// Plain icy gradient
		AnimationBuilder frost_plain = new AnimationBuilder(this, AnimationType.REPEATING, false);
		frost_plain.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.BLUE, 10, 1, DisplayType.TITLE));
		frost_plain.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			frames.add(ColorTranslator.colorize("&l<#74c5ff>" + input + "</#cdeaff>"));
			return frames;
		});
		frost_plain.register("EXTENSION_WINTER_PLAIN");

		// Glacier gradient (bold optional)
		AnimationBuilder glacier_gradient = new AnimationBuilder(this, AnimationType.REPEATING, false, "0/1(1=bold,0=normal)");
		glacier_gradient.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.BLUE, 10, 1, DisplayType.TITLE));
		glacier_gradient.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			int code = safeParseInt(arg(args, 0), 1);
			String bold = (code == 1) ? "&l" : "";
			String frame = "<#9ddfff>" + bold + input + "</#4ac3ff>";
			frames.add(ColorTranslator.colorize(frame));
			return frames;
		});
		glacier_gradient.register("EXTENSION_WINTER_GLACIER");

		// Aurora sweep wave
		AnimationBuilder aurora_sweep = new AnimationBuilder(this, AnimationType.REPEATING, false);
		aurora_sweep.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.BLUE, 10, 1, DisplayType.TITLE));
		aurora_sweep.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			if (input.isEmpty()) {
				frames.add(ColorTranslator.colorize("<#9ddfff>*</#e7faff>"));
				return frames;
			}
			Color deep = Color.decode("#43c0ff");
			Color light = Color.decode("#e7faff");
			int totalFrames = input.length() + 16;
			for (int shift = 0; shift < totalFrames; shift++) {
				StringBuilder builder = new StringBuilder("&l");
				for (int i = 0; i < input.length(); i++) {
					double wave = 0.5 + 0.5 * Math.sin((i + shift) / 2.0);
					Color shade = mixColors(deep, light, wave);
					String hex = toHex(shade);
					builder.append("<").append(hex).append(">").append(input.charAt(i)).append("</").append(hex).append(">");
				}
				frames.add(ColorTranslator.colorize(builder.toString()));
			}
			return frames;
		});
		aurora_sweep.register("EXTENSION_WINTER_AURORA_SWEEP");

		// Ice shards pulse
		AnimationBuilder ice_shards = new AnimationBuilder(this, AnimationType.REPEATING, false);
		ice_shards.setComponentArguments(ComponentArguments.create("Text is null", "SubText is null", BarColor.BLUE, 10, 1, DisplayType.TITLE));
		ice_shards.setFramesBuilder((arguments, args) -> {
			LinkedList<String> frames = new LinkedList<>();
			String input = clean(arguments.getMainText());
			if (input.isEmpty()) {
				frames.add(ColorTranslator.colorize("<#c6e9ff>*</#c6e9ff>"));
				return frames;
			}
			int length = input.length();
			int centerLeft = (length - 1) / 2;
			int centerRight = length / 2;
			int maxExpand = Math.max(centerLeft, length - centerRight - 1);
			for (int expand = 0; expand <= maxExpand; expand++) {
				int start = Math.max(centerLeft - expand, 0);
				int end = Math.min(centerRight + expand + 1, length);
				String left = input.substring(0, start);
				String shard = input.substring(start, end);
				String right = input.substring(end);
				String frame = "<#c6e9ff>" + left + "</#c6e9ff><#5bbcff>&l" + shard + "</#e4f8ff><#c6e9ff>" + right + "</#c6e9ff>";
				frames.add(ColorTranslator.colorize(frame));
			}
			for (int expand = maxExpand - 1; expand >= 0; expand--) {
				int start = Math.max(centerLeft - expand, 0);
				int end = Math.min(centerRight + expand + 1, length);
				String left = input.substring(0, start);
				String shard = input.substring(start, end);
				String right = input.substring(end);
				String frame = "<#c6e9ff>" + left + "</#c6e9ff><#5bbcff>&l" + shard + "</#e4f8ff><#c6e9ff>" + right + "</#c6e9ff>";
				frames.add(ColorTranslator.colorize(frame));
			}
			return frames;
		});
		ice_shards.register("EXTENSION_WINTER_ICE_SHARDS");

	}

	private Color mixColors(Color from, Color to, double ratio) {
		double fixed = Math.max(0, Math.min(1, ratio));
		int r = (int) Math.round(from.getRed() + (to.getRed() - from.getRed()) * fixed);
		int g = (int) Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * fixed);
		int b = (int) Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * fixed);
		return new Color(r, g, b);
	}

	private String toHex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private String clean(String raw) {
		if (raw == null) return "";
		String colored = ColorTranslator.colorize(raw);
		return ColorTranslator.removeColors(colored);
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

}
