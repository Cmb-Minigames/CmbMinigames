package xyz.devcmb.cmbminigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.devcmb.cmbminigames.CmbMinigames;

public class CMCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        commandSender.sendMessage(ChatColor.GREEN + "Your server is currently running Cmb Minigames v" + CmbMinigames.VERSION);
        return true;
    }
}
