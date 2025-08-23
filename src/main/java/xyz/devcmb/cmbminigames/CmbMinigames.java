package xyz.devcmb.cmbminigames;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class CmbMinigames extends JavaPlugin {

    public static Logger LOGGER;
    private static CmbMinigames plugin;

    public static CmbMinigames getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        LOGGER = getLogger();
        plugin = this;

        LOGGER.info("Cmb Minigames has awoken");
    }

    @Override
    public void onDisable() {
        LOGGER.info("Cmb Minigames has been murdered");
    }
}
