package org.korastudios.amazingtitles.code.providers.R1_20_R2;

import org.korastudios.amazingtitles.code.internal.spi.NmsBuilder;
import org.korastudios.amazingtitles.code.internal.spi.NmsProvider;

public class R1_20_R2_Builder implements NmsBuilder {
	
	
	@Override
	public boolean checked(String version) {
		return version.equals("v1_20_R2");
	}
	
	@Override
	public NmsProvider build() {
		return new R1_20_R2();
	}
	
}
