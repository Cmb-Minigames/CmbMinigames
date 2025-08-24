package xyz.devcmb.cmbminigames.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.commands.completions.MinigameCommandCompletion;
import xyz.devcmb.cmbminigames.commands.minigames.MinigameCommand;

import java.util.Objects;

public class RegisterCommands {
    public static void RegisterAllCommands() {
        // Commands
        registerSingleCommand("minigame", new MinigameCommand());

        // Completions
        registerSingleTabCompletion("minigame", new MinigameCommandCompletion());
    }

    /**
     * Registers a single command
     * @param command The command name
     * @param executor The command executor class
     */
    public static void registerSingleCommand(String command, CommandExecutor executor){
        CmbMinigames plugin = CmbMinigames.getPlugin();
        Objects.requireNonNull(plugin.getCommand(command)).setExecutor(executor);
    }

    /**
     * Registers a single tab completion
     * @param command The command name
     * @param completer The tab completer class
     */
    public static void registerSingleTabCompletion(String command, TabCompleter completer){
        CmbMinigames plugin = CmbMinigames.getPlugin();
        Objects.requireNonNull(plugin.getCommand(command)).setTabCompleter(completer);
    }
}
