package xyz.devcmb.cmbminigames.commands;

import xyz.devcmb.cmbminigames.CmbMinigames;

public class RegisterCommands {
    public static void register(){
        CmbMinigames plugin = CmbMinigames.getPlugin();
        plugin.getCommand("cm").setExecutor(new CMCommand());
        plugin.getCommand("party").setExecutor(new PartyCommand());
    }
}
