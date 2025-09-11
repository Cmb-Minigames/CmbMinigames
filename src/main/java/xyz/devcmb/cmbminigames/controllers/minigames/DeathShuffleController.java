package xyz.devcmb.cmbminigames.controllers.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.Constants;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.util.Helpers;
import xyz.devcmb.cmbminigames.util.Timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeathShuffleController implements Minigame {
    private boolean isActive = false;

    private final Map<Player, EntityDamageEvent.DamageCause> deathSources = new HashMap<>();
    private final List<Player> participants = new ArrayList<>();
    private final List<Player> aliveParticipants = new ArrayList<>();

    private final List<Player> qualifiers = new ArrayList<>();
    private final List<Player> waitingPlayers = new ArrayList<>();

    private final Map<String, Boolean> keepInventoryDefaults = new HashMap<>();

    @Override
    public String getId() {
        return "deathshuffle";
    }

    @Override
    public String getName() {
        return "Death Shuffle";
    }

    @Override
    public Integer minimumPlayers() {
        return 2;
    }

    @Override
    public Component getHowToPlay() {
        return
                Component.text("Welcome to ")
                        .append(Component.text("Death Shuffle!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD))
                        .append(Component.newline())
                        .append(Component.text("The premise is simple, you will be assigned a death source, and you will need to ")
                                .append(Component.text("die by it.").color(NamedTextColor.RED)))
                        .append(Component.newline())
                        .append(
                                Component.text("However, you only have ")
                                        .append(Component.text(Math.round(Constants.DeathShuffleTimer / 60f)).append(Component.text(" minutes")).color(NamedTextColor.AQUA))
                                        .append(Component.text(" to die by your assigned source."))
                        )
                        .append(Component.newline())
                        .append(Component.text("If you do not properly die in time, you will be").append(Component.text(" ELIMINATED!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)))
                        .append(Component.newline())
                        .append(Component.text("Have fun!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                ;
    }

    @Override
    public void start() {
        keepInventoryDefaults.clear();

        boolean useKeepInventory = CmbMinigames.getPlugin().getConfig()
                .getBoolean("minigames.deathshuffle.useKeepInventory");

        @SuppressWarnings("unchecked")
        List<String> playWorlds = (List<String>) CmbMinigames.getPlugin().getConfig()
                .getList("minigames.deathshuffle.worlds");

        if(useKeepInventory) {
            assert playWorlds != null;
            playWorlds.forEach(world -> {
                World registeredWorld = Bukkit.getWorld(world);
                if(registeredWorld == null) {
                    CmbMinigames.LOGGER.warning("World " + world + " not found! Error in minigames.deathshuffle.worlds config!");
                    return;
                }

                keepInventoryDefaults.put(world, registeredWorld.getGameRuleValue(GameRule.KEEP_INVENTORY));
                registeredWorld.setGameRule(GameRule.KEEP_INVENTORY, true);
            });
        }

        participants.addAll(Bukkit.getOnlinePlayers());
        aliveParticipants.addAll(participants);
        qualifiers.addAll(participants);

        Helpers.Countdown(participants, 10, Component.text("The game is about to begin!"), this::Game, () -> !isActive);
    }

    private void Game() {
        if(!isActive) return;

        deathSources.clear();
        qualifiers.forEach(player -> {
            if(!player.isOnline()) return;
            EntityDamageEvent.DamageCause cause = Helpers.getRandom(Constants.GetDeathShuffleDeaths());
            deathSources.put(player, cause);

            player.sendMessage(
                    Component.text("Die by ")
                            .append(Component.text(cause.name())
                                            .decorate(TextDecoration.BOLD))
                            .color(NamedTextColor.GREEN));
        });

        qualifiers.clear();
        Timer.CreateTimer("deathshuffle_timer", Constants.DeathShuffleTimer, (timeLeft) -> {
            participants.removeIf(plr -> !plr.isOnline());
            aliveParticipants.removeIf(plr -> !plr.isOnline());
            qualifiers.removeIf(plr -> !plr.isOnline());
            waitingPlayers.removeIf(plr -> !plr.isOnline());

            if(participants.isEmpty()) {
                MinigameController.stopMinigame();
                return;
            }

            participants.forEach(player -> player.sendActionBar(
                    Component.text(Helpers.formatTime(timeLeft))
                            .color(qualifiers.contains(player) ? NamedTextColor.GREEN : NamedTextColor.YELLOW)
                            .decorate(TextDecoration.BOLD)
            ));
        }, (early) -> {
            if(!isActive) return;

            if(qualifiers.isEmpty()) {
                Bukkit.broadcast(
                        Component.text("No one died by their death source, so another round will be played!")
                                .color(NamedTextColor.YELLOW)
                );

                qualifiers.addAll(aliveParticipants);
                Game();
                return;
            }

            AtomicBoolean eliminatedPlayers = new AtomicBoolean(false);
            if(!deathSources.isEmpty()) {
                deathSources.keySet().forEach(player -> {
                    aliveParticipants.remove(player);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(
                            Component.text("You have been ")
                                    .append(Component.text("ELIMINATED").decorate(TextDecoration.BOLD))
                                    .color(NamedTextColor.RED)
                    );

                    eliminatedPlayers.set(true);

                    Bukkit.broadcast(
                            Component.text(
                                    player.getName() + " did not properly die in time! Their source was "
                            ).append(Component.text(deathSources.get(player).name()).decorate(TextDecoration.BOLD)).color(NamedTextColor.RED)
                    );
                });
            }

            if(qualifiers.size() == 1 && !Constants.IsDevelopment) {
                participants.forEach(player -> Helpers.GameEndAnnouncement(
                        player,
                        player.equals(qualifiers.getFirst()) ? Helpers.GameEndStatus.VICTORY : Helpers.GameEndStatus.DEFEAT,
                        Component.text("You will be placed into survival in 5 seconds")
                ));

                waitingPlayers.forEach(player -> Helpers.GameEndAnnouncement(
                        player,
                        Helpers.GameEndStatus.NO_PARTICIPATION,
                        Component.text("You will be placed into survival in 5 seconds"))
                );

                Bukkit.getScheduler().runTaskLater(CmbMinigames.getPlugin(), () -> {
                    // I did a really fun thought experiment when making this, I hope you can tell
                    if(!isActive) return;
                    MinigameController.stopMinigame();
                }, 5 * 20);

                return;
            }

            if(qualifiers.size() == aliveParticipants.size() && !eliminatedPlayers.get()) {
                Bukkit.broadcast(
                        Component.text("Everyone died in time! Continuing...").color(NamedTextColor.YELLOW)
                );
            }

            Game();
        }, () -> deathSources.isEmpty() || !isActive);
    }

    @Override
    public void end() {
        participants.forEach(plr -> plr.setGameMode(GameMode.SURVIVAL));
        waitingPlayers.forEach(plr -> plr.setGameMode(GameMode.SURVIVAL));

        deathSources.clear();
        qualifiers.clear();
        participants.clear();
        aliveParticipants.clear();

        keepInventoryDefaults.forEach((world, def) -> {
            World registeredWorld = Bukkit.getWorld(world);
            if(registeredWorld != null) {
                registeredWorld.setGameRule(GameRule.KEEP_INVENTORY, def);
            }
        });

        keepInventoryDefaults.clear();
        waitingPlayers.clear();
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        if(!isActive || !deathSources.containsKey(player)) return;

        EntityDamageEvent lastDamage = player.getLastDamageCause();
        if (lastDamage != null) {
            EntityDamageEvent.DamageCause cause = lastDamage.getCause();
            EntityDamageEvent.DamageCause playerCause = deathSources.get(player);

            if(playerCause == cause) {
                qualifiers.add(player);
                deathSources.remove(player);

                Bukkit.broadcast(
                        Component.text(
                                player.getName() + " died! Their source was "
                        ).append(Component.text(playerCause.name()).decorate(TextDecoration.BOLD)).color(NamedTextColor.GREEN)
                );
            }
        }
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(!isActive || !deathSources.containsKey(player)) return;

        if(player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        deathSources.remove(player);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!isActive) return;

        player.setGameMode(GameMode.SPECTATOR);
        waitingPlayers.add(player);

        player.sendMessage(
                Component.text("A game of ")
                        .append(Component.text("Death Shuffle").decorate(TextDecoration.BOLD))
                        .append(Component.text(" is currently active. You will exit spectator once the game ends."))
                        .color(NamedTextColor.GREEN)
        );
    }

    @Override
    public boolean getActive() {
        return isActive;
    }
}
