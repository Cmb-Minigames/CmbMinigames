package xyz.devcmb.cmbminigames.listeners;

import org.bukkit.plugin.PluginManager;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.listeners.minigames.BlockBingoListeners;
import xyz.devcmb.cmbminigames.listeners.minigames.BlockShuffleListeners;
import xyz.devcmb.cmbminigames.listeners.minigames.ManhuntListeners;

public class RegisterListeners {
    public static void register(){
        CmbMinigames plugin = CmbMinigames.getPlugin();
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new GlobalListeners(), CmbMinigames.getPlugin());
        pluginManager.registerEvents(new DeathEffects(), CmbMinigames.getPlugin());
        pluginManager.registerEvents(new PartyListeners(), CmbMinigames.getPlugin());

        // Minigame listeners
        pluginManager.registerEvents(new ManhuntListeners(), CmbMinigames.getPlugin());
        pluginManager.registerEvents(new BlockShuffleListeners(), CmbMinigames.getPlugin());
        pluginManager.registerEvents(new BlockBingoListeners(), CmbMinigames.getPlugin());
    }
}
