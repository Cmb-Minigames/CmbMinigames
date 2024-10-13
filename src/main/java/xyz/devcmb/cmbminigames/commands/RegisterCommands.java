package xyz.devcmb.cmbminigames.commands;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.devcmb.cmbminigames.CmbMinigames;

public class RegisterCommands {
    public static void register(){
        CmbMinigames plugin = CmbMinigames.getPlugin();
        plugin.getCommand("cm").setExecutor(new CMCommand());
    }
}
