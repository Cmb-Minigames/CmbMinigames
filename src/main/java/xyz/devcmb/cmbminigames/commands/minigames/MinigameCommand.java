package xyz.devcmb.cmbminigames.commands.minigames;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.controllers.minigames.Minigame;
import xyz.devcmb.cmbminigames.util.Format;

import java.util.List;

public class MinigameCommand implements CommandExecutor {
    public static List<String> subcommands = List.of("start", "end", "list");

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(args.length == 0 || !subcommands.contains(args[0].toLowerCase())) {
            commandSender.sendMessage(Format.format("Invalid usage: /minigame <" + String.join(" | ", subcommands) + ">", Format.FormatType.INVALID));
            return true;
        }

        switch(args[0]) {
            case "start":
                if(MinigameController.getActiveMinigame() != null) {
                    commandSender.sendMessage(Format.format("You cannot start a minigame while one is currently active.", Format.FormatType.INVALID));
                    return true;
                }

                if(args.length < 2) {
                    commandSender.sendMessage(Format.format("Invalid usage: /minigame start <" + String.join(" | ", MinigameController.getMinigames().keySet()) + ">", Format.FormatType.INVALID));
                    return true;
                }

                String minigameName = args[1];
                Minigame minigame = MinigameController.getMinigames().get(minigameName);

                if(minigame == null) {
                    commandSender.sendMessage(Format.format("Minigame with the name " + minigameName + " could not be found.", Format.FormatType.INVALID));
                    return true;
                }

                if(!commandSender.hasPermission("cmbminigames.minigame." + minigame.getId()) && !commandSender.hasPermission("cmbminigames.manager")) {
                    commandSender.sendMessage(Format.format("You do not have permission to run this minigame!", Format.FormatType.ERROR));
                    return true;
                }

                commandSender.sendMessage(Format.format("Sent signal for minigame start!", Format.FormatType.SUCCESS));
                MinigameController.startMinigame(commandSender, minigameName);
                return true;
            case "end":
                Minigame activeMinigame = MinigameController.getActiveMinigame();
                if(activeMinigame == null) {
                    commandSender.sendMessage(Format.format("There is no active minigame!", Format.FormatType.INVALID));
                    return true;
                }

                if(!commandSender.hasPermission("cmbminigames.minigame." + activeMinigame.getId()) && !commandSender.hasPermission("cmbminigames.manager")) {
                    commandSender.sendMessage(Format.format("You do not have permission to run this minigame!", Format.FormatType.ERROR));
                    return true;
                }

                commandSender.sendMessage(Format.format("Sent signal for minigame end!", Format.FormatType.SUCCESS));
                MinigameController.stopMinigame();
                return true;
            case "list":
                // TODO
            default:
                commandSender.sendMessage(Format.format("Subcommand " + args[0] + " not implemented. Contact the developer.", Format.FormatType.ERROR));
        }

        return true;
    }
}
