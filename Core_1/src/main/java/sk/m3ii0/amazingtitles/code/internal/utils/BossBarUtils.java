package sk.m3ii0.amazingtitles.code.internal.utils;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import sk.m3ii0.amazingtitles.code.api.enums.DisplayType;

import java.lang.reflect.Method;

public class BossBarUtils {
	
	private static final Method CREATE_BOSS_BAR = resolveCreateBossBar();
	
	private BossBarUtils() {}
	
	public static boolean isSupported() {
		return CREATE_BOSS_BAR != null;
	}
	
	public static DisplayType resolveDisplayType(DisplayType displayType) {
		if (displayType == DisplayType.BOSS_BAR && !isSupported()) {
			return DisplayType.ACTION_BAR;
		}
		return displayType;
	}
	
	public static BossBar createBossBar(String title, BarColor color) {
		if (!isSupported()) {
			return null;
		}
		try {
			return (BossBar) CREATE_BOSS_BAR.invoke(null, title, color, BarStyle.SOLID);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to create boss bar", e);
		}
	}
	
	private static Method resolveCreateBossBar() {
		try {
			return Bukkit.class.getMethod("createBossBar", String.class, BarColor.class, BarStyle.class);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	
}
