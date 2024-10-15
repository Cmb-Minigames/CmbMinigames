package xyz.devcmb.cmbminigames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Random;

public class DeathEffects implements Listener {
    private static final String[] deathMessages = new String[]{
        "{player} tripped",
        "{player} fell and couldn't get up",
        "{player} forgot this wasn't roblox",
        "{player} didn't try hard enough",
        "{player}'s dad wasn't proud",
        "{player} thought they could speed bridge",
        "{player} got roasted on the internet",
        "Cmb personally doesn't like {player}",
        "{player} had a skill issue",
        "{player} became a pork chop",
        "{player} went kaboom",
        "{player} was caught playing fortnite",
        "{player}'s brother did not tell them how to chase the bag",
        "{player} should get their eyes checked",
        "{player} should buy a better pc",
        "{player} didn't check out kaboom kompetition",
        "{player} forgot that no one jumps for the beef",
        "{player} watched skibidi toilet",
        "{player} got banned from discord",
        "{player} got rejected",
        "{player} forgot to touch grass"
    };

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity().getPlayer();
        String deathMessage = deathMessages[new Random().nextInt(deathMessages.length)];
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "💀 " + deathMessage.replace("{player}", player.getName()));
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 10, 1);
    }
}
