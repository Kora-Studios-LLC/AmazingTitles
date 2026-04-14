package org.korastudios.amazingtitles.code.internal.announcements;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker implements Listener {

	private static final Pattern TAG_NAME_PATTERN = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");

	private final JavaPlugin plugin;
	private final String latestVersionUrl;
	private final String currentVersion;
	private final String permission;
	private final String link;
	private final String pluginName;

	public UpdateChecker(JavaPlugin plugin, String pluginName, String link, String permission, String currentVersion, String latestVersionUrl) {
		this.plugin = plugin;
		this.latestVersionUrl = latestVersionUrl;
		this.currentVersion = currentVersion;
		this.permission = permission;
		this.link = link;
		this.pluginName = pluginName;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private void getVersion(final Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
			HttpURLConnection connection = null;
			try {
				connection = (HttpURLConnection) new URL(this.latestVersionUrl).openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				connection.setRequestProperty("Accept", "application/vnd.github+json");
				connection.setRequestProperty("User-Agent", this.plugin.getName() + "-update-checker");
				try (InputStream inputStream = connection.getInputStream();
					 Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\A")) {
					if (scanner.hasNext()) {
						String response = scanner.next();
						Matcher matcher = TAG_NAME_PATTERN.matcher(response);
						if (matcher.find()) {
							consumer.accept(matcher.group(1));
						}
					}
				}
			} catch (IOException ignore) {
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		});
	}

	private boolean isUpToDate(String latestVersion) {
		return normalizeVersion(currentVersion).equals(normalizeVersion(latestVersion));
	}

	private String normalizeVersion(String version) {
		if (version == null) {
			return "";
		}
		String normalized = version.trim();
		if (normalized.startsWith("v") || normalized.startsWith("V")) {
			normalized = normalized.substring(1);
		}
		normalized = normalized.replace("-SNAPSHOT", "");
		normalized = normalized.replace("-snapshot", "");
		return normalized.trim();
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission(permission)) {
			return;
		}
		getVersion((latest) -> Bukkit.getScheduler().runTask(this.plugin, () -> {
			if (!player.isOnline()) {
				return;
			}
			if (isUpToDate(latest)) {
				player.sendMessage("\u00A7a\u00A7l\u2713 \u00A7a" + pluginName + " \u00A77\u00BB \u00A7fYou're using latest version!");
				return;
			}
			player.spigot().sendMessage(
				new ComponentBuilder("\u00A7c\u00A7l\u2718 \u00A7c" + pluginName + " \u00A77\u00BB \u00A7fYou're using outdated version! Click to open GitHub Releases and update plugin...")
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("\u00A7fClick to open!")))
					.event(new ClickEvent(ClickEvent.Action.OPEN_URL, link))
					.create()
			);
		}));
	}

}
