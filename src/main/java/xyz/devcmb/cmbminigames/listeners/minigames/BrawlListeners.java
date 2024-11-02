package xyz.devcmb.cmbminigames.listeners.minigames;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.controllers.minigames.BrawlController;

public class BrawlListeners implements Listener {
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!MinigameController.isMinigameBeingPlayed("brawl")) return;
        BrawlController bc = (BrawlController) MinigameController.getById("brawl");

        if(!(event.getDamager() instanceof Player)) return;
        if(!(event.getEntity() instanceof Player)) return;

        if(!bc.PVPEnabled){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        if (!MinigameController.isMinigameBeingPlayed("brawl")) return;
        Player player = event.getEntity();

        BrawlController bc = (BrawlController) MinigameController.getById("brawl");
        bc.AlivePlayers.remove(player);

        if(bc.AlivePlayers.size() <= 1){
            bc.winner = bc.AlivePlayers.getFirst();
            MinigameController.endMinigame(null, "brawl", true);
            MinigameController.resetMinigame(null, "brawl", true);
        }
    }
}
