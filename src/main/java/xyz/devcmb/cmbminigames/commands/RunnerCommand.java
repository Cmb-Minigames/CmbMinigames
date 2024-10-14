package xyz.devcmb.cmbminigames.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.controllers.minigames.ManhuntController;

public class RunnerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        ManhuntController mhc = (ManhuntController) MinigameController.getById("manhunt");
        if(commandSender instanceof Player p){
            if(MinigameController.isMinigameActive("manhunt")){
                mhc.setRunner(p);
                Bukkit.broadcastMessage(p.getDisplayName() + " -> " + ChatColor.BOLD + ChatColor.RED + "Hunter");
            } else {
                commandSender.sendMessage(ChatColor.RED + "Manhunt is not currently active");
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "Only players can execute this command");
        }

        return true;
    }
}
