package xyz.devcmb.cmbminigames.controllers;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.controllers.minigames.BlockShuffleController;
import xyz.devcmb.cmbminigames.controllers.minigames.Minigame;

import java.util.HashMap;
import java.util.Map;

public class MinigameController {
    private static final Map<String, Minigame> minigames = new HashMap<>();
    private static Minigame activeMinigame = null;

    public static Minigame getActiveMinigame() {
        return activeMinigame;
    }

    public static Map<String, Minigame> getMinigames() {
        return minigames;
    }

    public static void RegisterAllMinigames() {
        registerMinigame(new BlockShuffleController());
    }

    public static void startMinigame(String id){
        if(activeMinigame != null) return;

        Minigame minigame = minigames.get(id);
        if(minigame == null) return;

        minigame.setActive(true);
        minigame.start();
        activeMinigame = minigame;
    }

    public static void stopMinigame(){
        if(activeMinigame == null) return;

        activeMinigame.setActive(false);
        activeMinigame.end();
    }

    private static void registerMinigame(Minigame minigame) {
        minigames.put(minigame.getId(), minigame);

        Permission permission = new Permission("cmbminigames.minigame." + minigame.getId(), PermissionDefault.OP);

        PluginManager pm = Bukkit.getPluginManager();
        pm.addPermission(permission);
        pm.registerEvents(minigame, CmbMinigames.getPlugin());
    }
}
