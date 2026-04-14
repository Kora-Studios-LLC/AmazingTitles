package sk.m3ii0.amazingtitles.code.providers.R1_8_R3;

import sk.m3ii0.amazingtitles.code.internal.spi.NmsBuilder;
import sk.m3ii0.amazingtitles.code.internal.spi.NmsProvider;

import java.util.Locale;

public class R1_8_R3_Builder implements NmsBuilder {
	
	@Override
	public boolean checked(String version) {
		String normalized = version.toLowerCase(Locale.ROOT);
		return normalized.startsWith("v1_8_r3") || normalized.startsWith("1.8.8");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_8_R3();
	}
	
}
