package xyz.devcmb.cmbminigames.misc;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.controllers.minigames.Minigame;
import java.util.Random;

public class Utilities {
    public static void Countdown(Player player, int totalSeconds){
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

    public static Material GetRandomBlock(){
        Material[] blocks = BlockShuffleBlocks.Blocks;
        Random random = new Random();
        int randomIndex = random.nextInt(blocks.length);
        return blocks[randomIndex];
    }

    public static String getDisplayName(Material material) {
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder displayName = new StringBuilder();
        for (String word : words) {
            displayName.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return displayName.toString().trim();
    }
}
