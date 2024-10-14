package xyz.devcmb.cmbminigames;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.devcmb.cmbminigames.commands.RegisterCommands;
import xyz.devcmb.cmbminigames.controllers.PartyController;
import xyz.devcmb.cmbminigames.listeners.PartyListeners;
import xyz.devcmb.cmbminigames.listeners.RegisterListeners;
import xyz.devcmb.cmbminigames.misc.Database;

import java.util.logging.Logger;

public final class CmbMinigames extends JavaPlugin {

    public static Logger LOGGER;
    public static String VERSION;
    public static boolean PluginDisabled = false;
    private static CmbMinigames plugin;

    public static CmbMinigames getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        VERSION = getDescription().getVersion();
        plugin = this;

        saveDefaultConfig();

        Database.connect();

        if(PluginDisabled){
            LOGGER.severe("Cmb Minigames has been disabled due to an error");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        RegisterCommands.register();
        RegisterListeners.register();

        LOGGER.info("Cmb Minigames has awoken");
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()){
            PartyController.onLeave(new PlayerQuitEvent(player, "Server closed"));
        }
        LOGGER.info("Cmb Minigames has been murdered");
    }
}
