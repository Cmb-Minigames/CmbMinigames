package xyz.devcmb.cmbminigames;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.devcmb.cmbminigames.commands.RegisterCommands;
import xyz.devcmb.cmbminigames.listeners.RegisterListeners;
import xyz.devcmb.cmbminigames.misc.Database;

import java.util.logging.Logger;

public final class CmbMinigames extends JavaPlugin {

    public static Logger LOGGER;
    public static String VERSION;
    private static CmbMinigames plugin;

    public static CmbMinigames getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        VERSION = getDescription().getVersion();
        plugin = this;

        Database.connect();
        RegisterCommands.register();
        RegisterListeners.register();

        LOGGER.info("Cmb Minigames has awoken");
    }

    @Override
    public void onDisable() {
        LOGGER.info("Cmb Minigames has been murdered");
    }
}
