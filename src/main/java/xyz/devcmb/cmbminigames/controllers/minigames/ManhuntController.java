package xyz.devcmb.cmbminigames.controllers.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.Constants;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.util.Helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO list
 * - Maybe give a compass? (fuck it, you have the locator bar)
 */

public class ManhuntController implements Minigame {
    private boolean isActive = false;
    public boolean isPregame = false;

    private final List<Player> players = new ArrayList<>();
    public final List<Player> hunters = new ArrayList<>();
    public final List<Player> runners = new ArrayList<>();
    private final List<Player> aliveRunners = new ArrayList<>();
    private final List<Player> waitingPlayers = new ArrayList<>();

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
    public boolean getActive() {
        return isActive;
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

        World endWorld = Bukkit.getWorlds().stream()
                .filter(w -> w.getEnvironment() == World.Environment.THE_END)
                .findFirst()
                .orElse(null);

        if(endWorld != null) {
            DragonBattle battle = endWorld.getEnderDragonBattle();
            boolean hasDragon = endWorld.getEntities().stream()
                    .anyMatch(e -> e instanceof EnderDragon);

            if(!hasDragon && battle.hasBeenPreviouslyKilled()) {
                Bukkit.broadcast(Component.text("Could not start manhunt: End dimension does not have an ender dragon.").color(NamedTextColor.RED));
                MinigameController.stopMinigame();
                return;
            }
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

        Helpers.Countdown(hunters, Constants.ManhuntRunnerHeadstart, Component.text("You're about to be released!"), () -> hunters.forEach(plr -> {
            plr.setGameMode(GameMode.SURVIVAL);
            plr.teleport(startWorld.getSpawnLocation());

            if(clearInventories) {
                plr.getInventory().clear();
            }

            Bukkit.broadcast(Component.text("The hunters have been released!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
        }), () -> !isActive || aliveRunners.isEmpty());
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

        hunters.forEach((plr) -> Helpers.GameEndAnnouncement(player, Helpers.GameEndStatus.VICTORY, Component.text("You will be placed into survival in 5 seconds.")));
        runners.forEach((plr) -> Helpers.GameEndAnnouncement(player, Helpers.GameEndStatus.DEFEAT, Component.text("You will be placed into survival in 5 seconds.")));
        players.forEach((plr) -> plr.setGameMode(GameMode.SPECTATOR));

        waitingPlayers.forEach(plr -> Helpers.GameEndAnnouncement(
                plr,
                Helpers.GameEndStatus.NO_PARTICIPATION,
                Component.text("You will be placed into survival in 5 seconds"))
        );

        Bukkit.getScheduler().runTaskLater(CmbMinigames.getPlugin(), () -> {
            // I did a really fun thought experiment when making this, I hope you can tell
            if(!isActive) return;
            MinigameController.stopMinigame();
        }, 5 * 20);
    }

    private void RunnerWin() {
        runners.forEach((player) -> Helpers.GameEndAnnouncement(player, Helpers.GameEndStatus.VICTORY, Component.text("You will be placed into survival in 5 seconds.")));
        hunters.forEach((player) -> Helpers.GameEndAnnouncement(player, Helpers.GameEndStatus.DEFEAT, Component.text("You will be placed into survival in 5 seconds.")));
        players.forEach((plr) -> plr.setGameMode(GameMode.SPECTATOR));

        waitingPlayers.forEach(plr -> Helpers.GameEndAnnouncement(
                plr,
                Helpers.GameEndStatus.NO_PARTICIPATION,
                Component.text("You will be placed into survival in 5 seconds"))
        );

        Bukkit.getScheduler().runTaskLater(CmbMinigames.getPlugin(), () -> {
            // I did a really fun thought experiment when making this, I hope you can tell
            if(!isActive) return;
            MinigameController.stopMinigame();
        }, 5 * 20);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        if(!isActive) return;
        Player player = event.getPlayer();

        if(runners.contains(player)) {
            RunnerDeath(player);
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        if(!isActive) return;
        Player player = event.getPlayer();
        waitingPlayers.add(player);
        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        if(!isActive) return;
        Player player = event.getPlayer();

        players.remove(player);
        runners.remove(player);
        aliveRunners.remove(player);
        hunters.remove(player);
        waitingPlayers.remove(player);

        if(player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void dragonDeathEvent(EntityDeathEvent event) {
        if(!isActive) return;
        if(event.getEntity() instanceof EnderDragon) {
            RunnerWin();
        }
    }

    @Override
    public void end() {
        isPregame = false;

        players.forEach(plr -> plr.setGameMode(GameMode.SURVIVAL));
        waitingPlayers.forEach(plr -> plr.setGameMode(GameMode.SURVIVAL));

        players.clear();
        hunters.clear();
        runners.clear();
        waitingPlayers.clear();
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    public void Runner(Player player) {
        if(!isPregame) return;
        hunters.remove(player);
        runners.add(player);
        Bukkit.broadcast(
                Component.text(player.getName())
                        .append(Component.text(" » "))
                        .append(
                                Component.text("RUNNER").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD)
                        )
        );
    }

    public void Hunter(Player player) {
        if(!isPregame) return;
        hunters.add(player);
        runners.remove(player);
        Bukkit.broadcast(
                Component.text(player.getName())
                        .append(Component.text(" » "))
                        .append(
                                Component.text("HUNTER").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
                        )
        );
    }
}
