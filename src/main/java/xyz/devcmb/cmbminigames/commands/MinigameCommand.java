package xyz.devcmb.cmbminigames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.controllers.minigames.Minigame;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MinigameCommand implements CommandExecutor {

    private static final List<String> actions = Arrays.asList("activate", "deactivate", "start", "end", "reset");

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(!(commandSender instanceof Player)){
            commandSender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }

        if(args.length != 2){
            commandSender.sendMessage(ChatColor.RED + "Invalid arguments. Usage: /minigame <game> <action>");
            return true;
        }

        if(!actions.contains(args[1].toLowerCase())){
            commandSender.sendMessage(ChatColor.RED + "Invalid action. Valid actions are activate, deactivate, start, end, and reset. You said " + args[1].toLowerCase());
            return true;
        }


        if(commandSender.hasPermission("cmbminigames.minigame." + args[0].toLowerCase()) || commandSender.hasPermission("cmbminigames.manager")){
            boolean found = false;
            for(Minigame minigame : MinigameController.MINIGAMES){
                if(minigame.getId().equals(args[0].toLowerCase())){
                    found = true;
                }
            }

            if(!found){
                commandSender.sendMessage(ChatColor.RED + "Invalid minigame");
                return true;
            }

            switch(args[1].toLowerCase()) {
                case "activate":
                    MinigameController.activateMinigame((Player) commandSender, args[0].toLowerCase(), false);
                    break;
                case "deactivate":
                    MinigameController.deactivateMinigame((Player) commandSender, args[0].toLowerCase(),false);
                    break;
                case "start":
                    MinigameController.startMinigame((Player) commandSender, args[0].toLowerCase(), false);
                    break;
                case "end":
                    MinigameController.endMinigame((Player) commandSender, args[0].toLowerCase(), false);
                    break;
                case "reset":
                    MinigameController.resetMinigame((Player) commandSender, args[0].toLowerCase(), false);
                    break;
                default:
                    commandSender.sendMessage(ChatColor.RED + "Invalid action. Valid actions are activate, deactivate, start, end, and reset");
            }
        }

        return true;
    }
}
