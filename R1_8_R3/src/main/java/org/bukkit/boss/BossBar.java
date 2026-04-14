package org.bukkit.boss;

import org.bukkit.entity.Player;

public interface BossBar {
	
	void addPlayer(Player player);
	
	void removeAll();
	
	void setVisible(boolean visible);
	
	void setTitle(String title);
	
}
