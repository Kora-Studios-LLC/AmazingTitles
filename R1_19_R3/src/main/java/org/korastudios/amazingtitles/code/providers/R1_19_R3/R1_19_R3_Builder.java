package org.korastudios.amazingtitles.code.providers.R1_19_R3;

import org.korastudios.amazingtitles.code.internal.spi.NmsBuilder;
import org.korastudios.amazingtitles.code.internal.spi.NmsProvider;

public class R1_19_R3_Builder implements NmsBuilder {
	
	
	@Override
	public boolean checked(String version) {
		return version.equals("v1_19_R3");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_19_R3();
	}
	
}
