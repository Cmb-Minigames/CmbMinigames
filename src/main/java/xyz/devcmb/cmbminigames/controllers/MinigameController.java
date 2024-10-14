package xyz.devcmb.cmbminigames.controllers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.controllers.minigames.ManhuntController;
import xyz.devcmb.cmbminigames.controllers.minigames.Minigame;

import java.util.ArrayList;
import java.util.List;

public class MinigameController {
    public static final List<Minigame> MINIGAMES = new ArrayList<>();

    public static Minigame ActiveMinigame = null;
    public static Minigame PlayingMinigame = null;

    public static Minigame getById(String id){
        return MINIGAMES.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }

    public static boolean isMinigameActive(String MinigameID) {
        return (ActiveMinigame != null) && (ActiveMinigame.getId().equals(MinigameID));
    }

    public static boolean isMinigameBeingPlayed(String MinigameID) {
        return (PlayingMinigame != null) && (PlayingMinigame.getId().equals(MinigameID));
    }

    private static void registerMinigame(Minigame minigame){
        MINIGAMES.add(minigame);
        Permission minigamePermission = new Permission("cmbminigames.minigame." + minigame.getId(), PermissionDefault.OP);
        CmbMinigames.getPlugin().getServer().getPluginManager().addPermission(minigamePermission);
    }

    public static void registerAllMinigames(){
        registerMinigame(new ManhuntController());
    }

    public static void activateMinigame(Player executor, String id, Boolean automaticTrigger){
        Minigame minigame = MINIGAMES.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        if(minigame == null) return;

        if(ActiveMinigame == minigame){
            if(!automaticTrigger) executor.sendMessage(ChatColor.RED + "This minigame is already active!");
            return;
        }

        if(PlayingMinigame != null){
            executor.sendMessage(ChatColor.RED + "A minigame is already being played!");
            return;
        }

        if(ActiveMinigame != null){
            ActiveMinigame.resetGame();
        }

        ActiveMinigame = minigame;
        minigame.activateGame(executor);
    }

    public static void deactivateMinigame(Player executor, String id, Boolean automaticTrigger){
        Minigame minigame = MINIGAMES.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        if(minigame == null) return;

        if(ActiveMinigame != minigame){
            if(!automaticTrigger) executor.sendMessage(ChatColor.RED + "This minigame is not active!");
            return;
        }

        if(PlayingMinigame != null){
            if(!automaticTrigger) executor.sendMessage(ChatColor.RED + "A minigame is already being played!");
            return;
        }

        ActiveMinigame = null;

        minigame.deactivateGame(executor);
    }

    public static void startMinigame(Player executor, String id, Boolean automaticTrigger){
        Minigame minigame = MINIGAMES.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        if(minigame == null) return;

        if(ActiveMinigame != minigame){
            if(!automaticTrigger) executor.sendMessage(ChatColor.RED + "You must activate the minigame before starting it");
            return;
        }

        PlayingMinigame = minigame;
        minigame.startGame(executor);
    }

    public static void endMinigame(Player executor, String id, Boolean automaticTrigger){
        Minigame minigame = MINIGAMES.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        if(minigame == null) return;

        if(PlayingMinigame != minigame){
            if(!automaticTrigger) executor.sendMessage(ChatColor.RED + "This minigame is not being played!");
            return;
        }

        PlayingMinigame = null;
        minigame.endGame(executor);
    }

    public static void resetMinigame(Player executor, String id, Boolean automaticTrigger){
        Minigame minigame = MINIGAMES.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
        if(minigame == null) return;

        if(ActiveMinigame != minigame){
            if(!automaticTrigger) executor.sendMessage(ChatColor.RED + "This minigame is not active!");
            return;
        }

        minigame.resetGame();
    }
}
