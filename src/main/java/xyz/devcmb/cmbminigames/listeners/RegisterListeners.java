package xyz.devcmb.cmbminigames.listeners;

import org.bukkit.plugin.PluginManager;
import xyz.devcmb.cmbminigames.CmbMinigames;

public class RegisterListeners {
    public static void register(){
        CmbMinigames plugin = CmbMinigames.getPlugin();
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new GlobalListeners(), CmbMinigames.getPlugin());
        pluginManager.registerEvents(new DeathEffects(), CmbMinigames.getPlugin());
        pluginManager.registerEvents(new PartyListeners(), CmbMinigames.getPlugin());
    }
}
