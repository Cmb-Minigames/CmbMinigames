package xyz.devcmb.cmbminigames.listeners;

import xyz.devcmb.cmbminigames.CmbMinigames;

public class RegisterListeners {
    public static void register(){
        CmbMinigames.getPlugin().getServer().getPluginManager().registerEvents(new DeathListener(), CmbMinigames.getPlugin());
    }
}
