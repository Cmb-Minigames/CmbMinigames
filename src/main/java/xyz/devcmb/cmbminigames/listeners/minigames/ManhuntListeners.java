package xyz.devcmb.cmbminigames.listeners.minigames;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemFlag;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.controllers.minigames.ManhuntController;

public class ManhuntListeners implements Listener {
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        if(!MinigameController.isMinigameBeingPlayed("manhunt")) return;
        if(ManhuntController.Hunters.contains(player)){
            ItemStack compass = new ItemStack(Material.COMPASS);
            ItemMeta meta = compass.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                compass.setItemMeta(meta);
            }
            player.getInventory().addItem(compass);
        } else if(ManhuntController.Runners.contains(player)) {
            player.setGameMode(GameMode.SPECTATOR);
            ManhuntController.Runners.remove(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(!MinigameController.isMinigameBeingPlayed("manhunt")) return;
        if(ManhuntController.Hunters.contains(player) || ManhuntController.Runners.contains(player)){
            player.getInventory().clear();
        }
        ManhuntController.Hunters.remove(player);
        ManhuntController.Runners.remove(player);
    }
}