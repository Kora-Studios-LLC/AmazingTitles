package sk.m3ii0.amazingtitles.code.providers.R1_8_R3;

import org.bukkit.entity.Player;
import sk.m3ii0.amazingtitles.code.internal.spi.NmsProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class R1_8_R3 implements NmsProvider {
	
	private final Method getHandle;
	private final Field playerConnection;
	private final Method sendPacket;
	private final Method chatSerializer;
	private final Constructor<?> actionbarPacketConstructor;
	private final Constructor<?> titleTimesPacketConstructor;
	private final Constructor<?> titleTextPacketConstructor;
	private final Constructor<?> subtitleTextPacketConstructor;
	private final Object titleAction;
	private final Object subtitleAction;
	
	public R1_8_R3() {
		try {
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer");
			Class<?> entityPlayer = Class.forName("net.minecraft.server.v1_8_R3.EntityPlayer");
			Class<?> playerConnectionClass = Class.forName("net.minecraft.server.v1_8_R3.PlayerConnection");
			Class<?> packetClass = Class.forName("net.minecraft.server.v1_8_R3.Packet");
			Class<?> chatComponentClass = Class.forName("net.minecraft.server.v1_8_R3.IChatBaseComponent");
			Class<?> chatSerializerClass = Class.forName("net.minecraft.server.v1_8_R3.IChatBaseComponent$ChatSerializer");
			Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server.v1_8_R3.PacketPlayOutChat");
			Class<?> packetPlayOutTitleClass = Class.forName("net.minecraft.server.v1_8_R3.PacketPlayOutTitle");
			Class<?> titleActionClass = Class.forName("net.minecraft.server.v1_8_R3.PacketPlayOutTitle$EnumTitleAction");
			
			this.getHandle = craftPlayer.getMethod("getHandle");
			this.playerConnection = entityPlayer.getDeclaredField("playerConnection");
			this.playerConnection.setAccessible(true);
			this.sendPacket = playerConnectionClass.getMethod("sendPacket", packetClass);
			this.chatSerializer = chatSerializerClass.getMethod("a", String.class);
			this.actionbarPacketConstructor = packetPlayOutChatClass.getConstructor(chatComponentClass, byte.class);
			this.titleTimesPacketConstructor = packetPlayOutTitleClass.getConstructor(int.class, int.class, int.class);
			this.titleTextPacketConstructor = packetPlayOutTitleClass.getConstructor(titleActionClass, chatComponentClass);
			this.titleAction = readEnumConstant(titleActionClass, "TITLE");
			this.subtitleAction = readEnumConstant(titleActionClass, "SUBTITLE");
			this.subtitleTextPacketConstructor = packetPlayOutTitleClass.getConstructor(titleActionClass, chatComponentClass);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to initialize R1_8_R3 provider", e);
		}
	}
	
	@Override
	public Object createActionbarPacket(String text) {
		try {
			return actionbarPacketConstructor.newInstance(parseText(text), (byte) 2);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to create 1.8.8 action bar packet", e);
		}
	}
	
	@Override
	public Object[] createTitlePacket(String title, String subtitle, int in, int keep, int out) {
		try {
			Object animation = titleTimesPacketConstructor.newInstance(in, keep, out);
			Object text = titleTextPacketConstructor.newInstance(titleAction, parseText(title));
			Object subtext = subtitleTextPacketConstructor.newInstance(subtitleAction, parseText(subtitle));
			return new Object[] {animation, text, subtext};
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to create 1.8.8 title packet", e);
		}
	}
	
	@Override
	public void sendTitles(Player player, Object... packets) {
		Object connection = getPlayerConnection(player);
		for (Object packet : packets) {
			sendPacket(connection, packet);
		}
	}
	
	@Override
	public void sendActionbar(Player player, Object packet) {
		sendPacket(getPlayerConnection(player), packet);
	}
	
	private Object getPlayerConnection(Player player) {
		try {
			Object handle = getHandle.invoke(player);
			return playerConnection.get(handle);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to access 1.8.8 player connection", e);
		}
	}
	
	private void sendPacket(Object connection, Object packet) {
		try {
			sendPacket.invoke(connection, packet);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to send 1.8.8 packet", e);
		}
	}
	
	private Object parseText(String text) throws ReflectiveOperationException {
		String normalized = (text == null || text.isEmpty()) ? " " : text;
		return chatSerializer.invoke(null, "{\"text\":\"" + escapeJson(normalized) + "\"}");
	}
	
	private Object readEnumConstant(Class<?> enumClass, String name) {
		@SuppressWarnings({"rawtypes", "unchecked"})
		Enum<?> constant = Enum.valueOf((Class) enumClass.asSubclass(Enum.class), name);
		return constant;
	}
	
	private String escapeJson(String text) {
		return text
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}
	
}
