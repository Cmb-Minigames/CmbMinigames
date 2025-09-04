package xyz.devcmb.cmbminigames.controllers.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.Constants;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.util.Helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO list
 * - Allow role assignment through a command (/runner and /hunter)
 * - Make the game end when any runner makes it to the end fountain
 * - Give a better team indication
 * - Handle midgame join
 * - TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST
 * - Fix the 10k bugs I find during testing
 */

public class ManhuntController implements Minigame {
    private boolean isActive = false;
    private boolean isPregame = false;

    private static List<Player> players = new ArrayList<>();
    private static List<Player> hunters = new ArrayList<>();
    private static List<Player> runners = new ArrayList<>();
    private static List<Player> aliveRunners = new ArrayList<>();

    @Override
    public String getId() {
        return "manhunt";
    }

    @Override
    public String getName() {
        return "Manhunt";
    }

    @Override
    public Integer minimumPlayers() {
        return 2;
    }

    @Override
    public Component getHowToPlay() {
        return
                Component.text("Welcome to ")
                        .append(Component.text("Manhunt!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                        .append(Component.newline())
                        .append(Component.text("The premise is simple. Before the game begins, you can assign yourself as a ")
                                .append(Component.text("HUNTER").decorate(TextDecoration.BOLD).color(NamedTextColor.RED))
                                .append(Component.text(" or a "))
                                .append(Component.text("RUNNER").decorate(TextDecoration.BOLD).color(NamedTextColor.AQUA)))
                        .append(Component.newline())
                        .append(
                                Component.text("The ")
                                        .append(Component.text("RUNNERS").decorate(TextDecoration.BOLD).color(NamedTextColor.AQUA))
                                        .append(Component.text(" need to beat the game before the "))
                                        .append(Component.text("HUNTERS").decorate(TextDecoration.BOLD).color(NamedTextColor.RED))
                                        .append(Component.text(" can kill them"))
                        )
                        .append(Component.newline())
                        .append(Component.text("Hunters get unlimited lives, but if a runner dies, they will be").append(Component.text(" ELIMINATED!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)))
                        .append(Component.newline())
                        .append(Component.text("Have fun!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                ;
    }

    @Override
    public void start() {
        String worldName = CmbMinigames.getPlugin().getConfig()
                .getString("minigames.manhunt.play_world");

        if(worldName == null) {
            Bukkit.broadcast(Component.text("Could not start manhunt: minigames.manhunt.play_world is not provided").color(NamedTextColor.RED));
            MinigameController.stopMinigame();
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if(world == null) {
            Bukkit.broadcast(Component.text("Could not start manhunt: minigames.manhunt.play_world is not a valid world name").color(NamedTextColor.RED));
            MinigameController.stopMinigame();
            return;
        }

        Bukkit.broadcast(Component.text("Choose a role using the /runner or /hunter command").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        isPregame = true;


        List<Player> activePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        activePlayers.forEach(plr -> plr.setGameMode(GameMode.SPECTATOR));

        Helpers.Countdown(activePlayers, 30, Component.text("Choose a role with /runner or /hunter"), () -> {
            if(!isActive) return;
            isPregame = false;

            if(hunters.size() < Constants.MinimumManhuntHunters) {
                Bukkit.broadcast(
                        Component.text("Not enough ")
                                .append(Component.text("hunters! ").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                                .append(Component.text("Cannot continue!")).color(NamedTextColor.RED)
                );

                MinigameController.stopMinigame();
                return;
            }

            if(runners.size() < Constants.MinimumManhuntRunners) {
                Bukkit.broadcast(
                        Component.text("Not enough ")
                                .append(Component.text("runners! ").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD))
                                .append(Component.text("Cannot continue!")).color(NamedTextColor.RED)
                );

                MinigameController.stopMinigame();
                return;
            }

            if(hunters.size() > Constants.MaximumManhuntHunters) {
                Bukkit.broadcast(Component.text("An error has occurred trying to play this minigame: hunters.size() exceeds maximum.").color(NamedTextColor.RED));
                MinigameController.stopMinigame();
                return;
            }

            if(runners.size() > Constants.MaximumManhuntRunners) {
                Bukkit.broadcast(Component.text("An error has occurred trying to play this minigame: runners.size() exceeds maximum.").color(NamedTextColor.RED));
                MinigameController.stopMinigame();
                return;
            }

            Game(world);
        }, () -> !isActive);
    }

    private void Game(World startWorld) {
        if(!isActive) return;
        players.addAll(hunters);
        players.addAll(runners);
        aliveRunners.addAll(runners);

        boolean clearInventories = CmbMinigames.getPlugin().getConfig()
                .getBoolean("minigames.manhunt.clear_start");

        aliveRunners.forEach(plr -> {
            plr.setGameMode(GameMode.SURVIVAL);
            plr.teleport(startWorld.getSpawnLocation());

            if(clearInventories) {
                plr.getInventory().clear();
            }
        });

        Helpers.Countdown(hunters, Constants.ManhuntRunnerHeadstart, Component.text("You're about to be released!"), () -> {
            hunters.forEach(plr -> {
                plr.setGameMode(GameMode.SURVIVAL);
                plr.teleport(startWorld.getSpawnLocation());

                if(clearInventories) {
                    plr.getInventory().clear();
                }
            });
        }, () -> !isActive || aliveRunners.isEmpty());
    }

    private void RunnerDeath(Player player) {
        if(!aliveRunners.contains(player) || !isActive) return;
        aliveRunners.remove(player);

        if(!aliveRunners.isEmpty()){
            Bukkit.broadcast(
                    Component.text(player.getName())
                            .append(Component.text(" has died! ").append(Component.text(aliveRunners.size() + " runners remain!")).color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
            );

            return;
        }

        hunters.forEach((plr) -> Helpers.GameEndAnnouncement(player, Helpers.GameEndStatus.VICTORY, Component.text("")));
        runners.forEach((plr) -> Helpers.GameEndAnnouncement(player, Helpers.GameEndStatus.DEFEAT, Component.text("")));
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        if(!isActive) return;
        Player player = event.getPlayer();

        if(runners.contains(player)) {
            RunnerDeath(player);
        }
    }

    @Override
    public void end() {
        isPregame = false;
        players.clear();
        hunters.clear();
        runners.clear();
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }
}
