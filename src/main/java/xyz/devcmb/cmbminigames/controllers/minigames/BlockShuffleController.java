package xyz.devcmb.cmbminigames.controllers.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.misc.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockShuffleController implements Minigame {
    public List<Player> AlivePlayers = new ArrayList<>();
    public List<Player> SuccessfulPlayers = new ArrayList<>();
    public Map<Player, Material> PlayerBlocks = new HashMap<>();
    public int Round = 1;

    @Override
    public String getId() {
        return "blockshuffle";
    }

    @Override
    public String getName() {
        return "Block Shuffle";
    }

    @Override
    public String getDescription() {
        return "Stand on a random block every 5 minutes!";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void activateGame(Player player, Boolean automaticTrigger) {
        Utilities.AnnounceMinigame(this);
    }

    @Override
    public void deactivateGame(Player player, Boolean automaticTrigger) {}

    @Override
    public void startGame(Player player, Boolean automaticTrigger) {
        if(
            Bukkit.getOnlinePlayers().size() < 2
            && !CmbMinigames.DeveloperModeEnabled
        ){
            if(!automaticTrigger) player.sendMessage(ChatColor.RED + "There are not enough players to start the game");
            return;
        }

        AlivePlayers.addAll(Bukkit.getOnlinePlayers());
        for(Player p : AlivePlayers){
            Utilities.Countdown(p, 10);
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player p : AlivePlayers){
                    p.teleport(p.getWorld().getSpawnLocation());
                    p.setGameMode(GameMode.SURVIVAL);
                    p.getInventory().clear();
                    AssignBlocks(p);
                }

                new BukkitRunnable(){
                    int minutesRemaining = 6;
                    @Override
                    public void run() {
                        if(
                            (AlivePlayers.size() <= 1
                            || !MinigameController.isMinigameBeingPlayed(getId()))
                            && !CmbMinigames.DeveloperModeEnabled
                        ){
                            this.cancel();

                            MinigameController.endMinigame(null, getId(), true);
                            MinigameController.resetMinigame(null, getId(), true);
                            return;
                        }

                        minutesRemaining--;
                        if(minutesRemaining == 0 || SuccessfulPlayers.size() == AlivePlayers.size()){
                            if(SuccessfulPlayers.isEmpty()){
                                Bukkit.broadcastMessage(ChatColor.GREEN + "No one got their block, so no one will be eliminated");
                            } else {
                                for (Player plr : AlivePlayers) {
                                    if(!SuccessfulPlayers.contains(plr)){
                                        AlivePlayers.remove(plr);
                                        plr.sendMessage(ChatColor.RED + "You have failed to stand on the correct block, and have been eliminated!");
                                        plr.setGameMode(GameMode.SPECTATOR);
                                        Bukkit.broadcastMessage(plr.getName() + " has been eliminated! Their block was " + ChatColor.RED + ChatColor.BOLD + PlayerBlocks.get(plr).name().replace("_", " "));
                                    }
                                }

                                SuccessfulPlayers.clear();
                            }

                            PlayerBlocks.clear();

                            if(AlivePlayers.size() <= 1 && !CmbMinigames.DeveloperModeEnabled){
                                this.cancel();

                                MinigameController.endMinigame(null, getId(), true);
                                MinigameController.resetMinigame(null, getId(), true);
                                return;
                            }

                            Round++;

                            for(Player plr : AlivePlayers){
                                plr.sendTitle("Round " + Round, null, 5, 60, 5);
                                AssignBlocks(plr);
                            }

                            minutesRemaining = 5;
                        } else if(minutesRemaining != 5) {
                            for (Player p : AlivePlayers){
                                p.sendTitle(ChatColor.RED.toString() + minutesRemaining + " minutes remaining", null, 5, 60, 5);
                            }
                        }
                    }

                    public void AssignBlocks(Player plr) {
                        Material block = Utilities.GetRandomBlock();
                        PlayerBlocks.put(plr, block);
                        plr.sendMessage(ChatColor.GREEN + "You have been given the block " + ChatColor.BOLD + block.name().replace("_", " "));
                    }
                }.runTaskTimer(CmbMinigames.getPlugin(), 0, 60 * 20);
            }

            public void AssignBlocks(Player plr) {
                Material block = Utilities.GetRandomBlock();
                PlayerBlocks.put(plr, block);
                plr.sendMessage(ChatColor.GREEN + "You have been given the block " + ChatColor.BOLD + block.name().replace("_", " "));
            }
        }.runTaskLater(CmbMinigames.getPlugin(), 10 * 20);
    }

    @Override
    public void endGame(Boolean automaticTrigger) {
        if(AlivePlayers.isEmpty()){
            Bukkit.broadcastMessage(ChatColor.RED + "All active players have left the game, so there is no winner");
        } else if(AlivePlayers.size() == 1){
            Player winner = AlivePlayers.getFirst();
            winner.sendTitle(ChatColor.GOLD + "VICTORY", null, 5, 60, 5);
            Bukkit.broadcastMessage(ChatColor.GREEN + winner.getName() + " has won the game!");
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "The game has been ended early, and there are no winners :(");
        }
    }

    @Override
    public void resetGame(Boolean automaticTrigger) {
        AlivePlayers.clear();
        SuccessfulPlayers.clear();
        PlayerBlocks.clear();
    }
}
