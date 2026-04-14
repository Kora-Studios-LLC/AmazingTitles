package org.korastudios.amazingtitles.code.providers.R1_21_R1;

import org.korastudios.amazingtitles.code.internal.spi.NmsBuilder;
import org.korastudios.amazingtitles.code.internal.spi.NmsProvider;

import java.util.Locale;

public class R1_21_R1_Builder implements NmsBuilder {
	
	
	@Override
	public boolean checked(String version) {
		String normalized = version.toLowerCase(Locale.ROOT);
		return normalized.startsWith("v1_21_r1") || normalized.startsWith("1.21");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_21_R1();
	}
	
}
