package xyz.devcmb.cmbminigames.controllers.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.misc.Utilities;

import java.util.ArrayList;
import java.util.List;

public class BrawlController implements Minigame {
    public Boolean PVPEnabled = false;
    public List<Player> Players = new ArrayList<>();
    public List<Player> AlivePlayers = new ArrayList<>();
    public Player winner = null;

    @Override
    public String getId() {
        return "brawl";
    }

    @Override
    public String getName() {
        return "Brawl";
    }

    @Override
    public String getDescription() {
        return "Be the last one standing with a closing world border!";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void activateGame(Player player, Boolean automaticTrigger) {
        Utilities.AnnounceMinigame(this);

        World overworld = Bukkit.getWorld("world");
        World nether = Bukkit.getWorld("world_nether");

        if(overworld != null){
            overworld.getWorldBorder().setSize(2000 * 2);
        }

        if(nether != null){
            nether.getWorldBorder().setSize(400 * 2);
        }
    }

    @Override
    public void deactivateGame(Player player, Boolean automaticTrigger) {
        PVPEnabled = false;
    }

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
        Players.addAll(Bukkit.getOnlinePlayers());
        for(Player p : Players){
            p.setGameMode(GameMode.SPECTATOR);
            Utilities.Countdown(p, 10);
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player p : Players){
                    if(!p.isOnline()) {
                        Players.remove(p);
                        continue;
                    }

                    p.teleport(p.getWorld().getSpawnLocation());
                    p.setGameMode(GameMode.SURVIVAL);
                }
                Bukkit.broadcastMessage(ChatColor.GOLD + "PVP and the shrinking world border will be enabled in 2 minutes!");
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "PVP and the shrinking world border have been enabled! Last one standing wins!");

                        PVPEnabled = true;
                        World overworld = Bukkit.getWorld("world");
                        World nether = Bukkit.getWorld("world_nether");

                        if(overworld != null){
                            overworld.getWorldBorder().setSize(20 * 2, 10 * 60);
                        }

                        if(nether != null){
                            nether.getWorldBorder().setSize(10 * 2, 10 * 60);
                        }
                    }
                }.runTaskLater(CmbMinigames.getPlugin(), 2 * 60 * 20);
            }
        }.runTaskLater(CmbMinigames.getPlugin(), 10 * 20);
    }

    @Override
    public void endGame(Boolean automaticTrigger) {
        if(winner == null){
            Bukkit.broadcastMessage(ChatColor.RED + "The game has been ended early or the winner has left the game.");
            return;
        }

        for(Player p : Players){
            p.setGameMode(GameMode.SPECTATOR);
            p.getInventory().clear();
            if(p == winner){
                p.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", null, 5, 60, 5);
            } else {
                p.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", null, 5, 60, 5);
            }
        }
    }

    @Override
    public void resetGame(Boolean automaticTrigger) {
        AlivePlayers.clear();
        Players.clear();
        winner = null;

        World overworld = Bukkit.getWorld("world");
        World nether = Bukkit.getWorld("world_nether");

        if (overworld != null) {
            overworld.getWorldBorder().setSize(60000000);
        }

        if (nether != null) {
            nether.getWorldBorder().setSize(60000000);
        }
    }
}
