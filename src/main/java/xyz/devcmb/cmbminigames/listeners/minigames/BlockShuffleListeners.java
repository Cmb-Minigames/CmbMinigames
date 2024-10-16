package xyz.devcmb.cmbminigames.listeners.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.controllers.minigames.BlockShuffleController;

public class BlockShuffleListeners implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!MinigameController.isMinigameBeingPlayed("blockshuffle")) return;

        Block block;
        Block belowBlock = player.getLocation().add(0, -1, 0).getBlock();
        Block onBlock = player.getLocation().getBlock();
        BlockShuffleController bsc = (BlockShuffleController) MinigameController.getById("blockshuffle");

        if ((belowBlock.getType() == bsc.PlayerBlocks.get(player) || onBlock.getType() == bsc.PlayerBlocks.get(player)) && !bsc.SuccessfulPlayers.contains(player)) {
            if(belowBlock.getType() == bsc.PlayerBlocks.get(player)){
                block = belowBlock;
            } else {
                block = onBlock;
            }

            bsc.SuccessfulPlayers.add(player);
            Bukkit.broadcastMessage(player.getDisplayName() + " has successfully found their block! It was " + ChatColor.GREEN + ChatColor.BOLD + block.getType().name().replace("_", " "));
            if (bsc.SuccessfulPlayers.size() >= bsc.AlivePlayers.size()) {
                Bukkit.broadcastMessage(ChatColor.GREEN + "All players have found their block! Round will continue at the next minute.");
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        if(!MinigameController.isMinigameBeingPlayed("blockshuffle")) return;
        BlockShuffleController bsc = (BlockShuffleController) MinigameController.getById("blockshuffle");

        bsc.AlivePlayers.remove(event.getPlayer());
        bsc.SuccessfulPlayers.remove(event.getPlayer());
        if(bsc.AlivePlayers.isEmpty()) MinigameController.endMinigame(null, "blockshuffle", true);
    }
}
