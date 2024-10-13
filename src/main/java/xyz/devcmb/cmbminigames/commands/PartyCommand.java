package xyz.devcmb.cmbminigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.devcmb.cmbminigames.menus.PartyMenu;

public class PartyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(sender instanceof Player player) {
            PartyMenu.initialize(player);
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
        }
        return true;
    }
}
