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
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        ManhuntController mhc = (ManhuntController) MinigameController.getById("manhunt");
        if(sender instanceof Player p){
            if(MinigameController.isMinigameActive("manhunt")){
                Boolean success = mhc.setRunner(p);
                if(success) Bukkit.broadcastMessage(p.getDisplayName() + " -> " + ChatColor.BOLD + ChatColor.AQUA + "Runner");
            } else {
                sender.sendMessage(ChatColor.RED + "Manhunt is not currently active");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command");
        }

        return true;
    }
}
