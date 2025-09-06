package xyz.devcmb.cmbminigames.commands.minigames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.controllers.minigames.ManhuntController;
import xyz.devcmb.cmbminigames.util.Format;

public class RunnerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        ManhuntController manhuntController = (ManhuntController) MinigameController.getMinigame("manhunt");
        assert manhuntController != null;

        if(!manhuntController.getActive()) {
            sender.sendMessage(Format.format("You cannot select a manhunt role while manhunt is inactive!", Format.FormatType.INVALID));
            return true;
        }

        if(!manhuntController.isPregame) {
            sender.sendMessage(Format.format("You can only select a role during pregame!", Format.FormatType.INVALID));
            return true;
        }

        if(!(sender instanceof Player p)) {
            sender.sendMessage(Format.format("Only players can use this command!", Format.FormatType.INVALID));
        } else {
            manhuntController.Runner(p);
        }

        return true;
    }
}
