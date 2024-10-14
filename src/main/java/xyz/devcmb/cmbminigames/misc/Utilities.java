package xyz.devcmb.cmbminigames.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.controllers.minigames.Minigame;

public class Utilities {
    public static void doCountdown(Player player, int totalSeconds){
        new BukkitRunnable(){
            int seconds = totalSeconds;
            @Override
            public void run() {
                if(seconds == 0){
                    this.cancel();
                    return;
                }

                ChatColor color = ChatColor.WHITE;

                switch(seconds){
                    case 3:
                        color = ChatColor.GREEN;
                        break;
                    case 2:
                        color = ChatColor.YELLOW;
                        break;
                    case 1:
                        color = ChatColor.RED;
                        break;
                    default:
                        break;
                }

                player.sendTitle(color.toString() + ChatColor.BOLD + "> " + seconds + " <", "The game will begin shortly", 5, 20, 5);
                seconds--;
            }
        }.runTaskTimer(CmbMinigames.getPlugin(), 0, 20);
    }

    public static void AnnounceMinigame(Minigame minigame){
        for(Player plr : Bukkit.getOnlinePlayers()){
            plr.sendTitle(ChatColor.BOLD + minigame.getName(), ChatColor.YELLOW + minigame.getDescription(), 5, 120, 5);
        }
    }
}
