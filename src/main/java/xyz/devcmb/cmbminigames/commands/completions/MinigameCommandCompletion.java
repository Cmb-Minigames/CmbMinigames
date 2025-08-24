package xyz.devcmb.cmbminigames.commands.completions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.devcmb.cmbminigames.commands.minigames.MinigameCommand;
import xyz.devcmb.cmbminigames.controllers.MinigameController;

import java.util.List;

public class MinigameCommandCompletion implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(args.length == 1) {
            return MinigameCommand.subcommands;
        } else if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if(subcommand.equals("start")) {
                return MinigameController.getMinigames().keySet().stream().toList();
            }
        }

        return List.of();
    }
}
