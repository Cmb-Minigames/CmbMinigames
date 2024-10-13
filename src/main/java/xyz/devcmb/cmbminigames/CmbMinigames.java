package xyz.devcmb.cmbminigames;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.devcmb.cmbminigames.commands.RegisterCommands;
import xyz.devcmb.cmbminigames.listeners.RegisterListeners;

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

        LOGGER.info("Cmb Minigames has awoken");
        RegisterCommands.register();
        RegisterListeners.register();
    }

    @Override
    public void onDisable() {
        LOGGER.info("Cmb Minigames has been murdered");
    }
}
