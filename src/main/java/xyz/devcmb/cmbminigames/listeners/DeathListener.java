package xyz.devcmb.cmbminigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.Random;

public class DeathListener implements Listener {
    private static final String[] deathMessages = new String[]{
        "{player} tripped",
        "{player} fell and couldn't get up",
        "{player} forgot this wasn't roblox",
        "{player} didn't try hard enough",
        "{player}'s dad wasn't proud",
        "{player} thought they could speed bridge",
        "{player} got roasted on the internet",
        "Cmb personally doesn't like {player}"
    };

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        String deathMessage = deathMessages[new Random().nextInt(deathMessages.length)];
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + deathMessage.replace("{player}", e.getEntity().getName()));
    }
}
