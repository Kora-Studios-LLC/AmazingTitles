package sk.m3ii0.amazingtitles.code.providers.R1_21_R1;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import sk.m3ii0.amazingtitles.code.internal.spi.NmsProvider;

import java.time.Duration;

public class R1_21_R1 implements NmsProvider {
	
	private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
	
	@Override
	public Object createActionbarPacket(String text) {
		return deserialize(text);
	}
	
	@Override
	public Object[] createTitlePacket(String title, String subtitle, int in, int keep, int out) {
		Title.Times times = Title.Times.times(
				Duration.ofMillis(in * 50L),
				Duration.ofMillis(keep * 50L),
				Duration.ofMillis(out * 50L)
		);
		Title paperTitle = Title.title(deserialize(title), deserialize(subtitle), times);
		return new Object[] {paperTitle};
	}
	
	@Override
	public void sendTitles(Player player, Object... packets) {
		if (packets.length == 0) return;
		player.showTitle((Title) packets[0]);
	}
	
	@Override
	public void sendActionbar(Player player, Object packet) {
		player.sendActionBar((Component) packet);
	}
	
	private Component deserialize(String text) {
		if (text == null || text.isEmpty()) {
			text = " ";
		}
		return LEGACY_SERIALIZER.deserialize(text);
	}
	
}
