package xyz.devcmb.cmbminigames.controllers.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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

public class BlockShuffleController implements Minigame {
    public boolean isActive = false;

    private final Map<Player, Material> blocks = new HashMap<>();

    private final List<Player> participants = new ArrayList<>();
    private final List<Player> aliveParticipants = new ArrayList<>();

    private final List<Player> qualifiers = new ArrayList<>();
    private final List<Player> waitingPlayers = new ArrayList<>();

    private final Map<String, Boolean> keepInventoryDefaults = new HashMap<>();

    @Override
    public String getId() {
        return "blockshuffle";
    }

    @Override
    public String getName() {
        return "Block Shuffle";
    }

    @Override
    public Integer minimumPlayers() {
        return 2;
    }

    @Override
    public Component getHowToPlay() {
        return
                Component.text("Welcome to ")
                        .append(Component.text("Block Shuffle!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD))
                        .append(Component.newline())
                        .append(Component.text("The premise is simple, you will be assigned a block and you must retrieve that block from somewhere in the world and")
                                .append(Component.text(" stand on it.").color(NamedTextColor.GREEN)))
                        .append(Component.newline())
                        .append(
                                Component.text("However, you only have")
                                        .append(Component.text(" 5 minutes").color(NamedTextColor.AQUA))
                                        .append(Component.text(" to stand on your assigned block."))
                        )
                        .append(Component.newline())
                        .append(Component.text("If you do not get your block in time, you will be").append(Component.text(" ELIMINATED!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)))
                        .append(Component.newline())
                        .append(Component.text("Have fun!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                ;
    }

    @Override
    public void start() {
        keepInventoryDefaults.clear();

        boolean useKeepInventory = CmbMinigames.getPlugin().getConfig()
                .getBoolean("minigames.blockshuffle.useKeepInventory");

        @SuppressWarnings("unchecked")
        List<String> playWorlds = (List<String>) CmbMinigames.getPlugin().getConfig()
                .getList("minigames.blockshuffle.worlds");

        if(useKeepInventory) {
            assert playWorlds != null;
            playWorlds.forEach(world -> {
                World registeredWorld = Bukkit.getWorld(world);
                if(registeredWorld == null) {
                    CmbMinigames.LOGGER.warning("World " + world + " not found! Error in minigames.blockshuffle.worlds config!");
                    return;
                }

                keepInventoryDefaults.put(world, registeredWorld.getGameRuleValue(GameRule.KEEP_INVENTORY));
                registeredWorld.setGameRule(GameRule.KEEP_INVENTORY, true);
            });
        }

        aliveParticipants.clear();
        participants.clear();
        qualifiers.clear();
        blocks.clear();

        participants.addAll(Bukkit.getOnlinePlayers());
        aliveParticipants.addAll(participants);
        qualifiers.addAll(participants);

        Helpers.Countdown(participants, 10, Component.text("The game is about to begin!"), this::Game, () -> !isActive);
    }

    private void Game() {
        if(!isActive) return;

        blocks.clear();
        qualifiers.forEach(player -> {
            if(!player.isOnline()) return;
            Material block = Helpers.getRandom(Constants.GetBlockShuffleBlocks());
            blocks.put(player, block);

            player.sendMessage(
                    Component.text("Stand on a ")
                            .append(
                                    Component.text(Helpers.materialToDisplayName(block))
                                            .decorate(TextDecoration.BOLD))
                            .color(NamedTextColor.GREEN));
        });

        qualifiers.clear();
        Timer.CreateTimer("blockshuffle_timer", Constants.BlockShuffleTimer, (timeLeft) -> {
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
                        Component.text("No one found their block, so another round will be played!")
                                .color(NamedTextColor.YELLOW)
                );

                qualifiers.addAll(aliveParticipants);
                Game();
                return;
            }

            AtomicBoolean eliminatedPlayers = new AtomicBoolean(false);
            if(!blocks.isEmpty()) {
                blocks.keySet().forEach(player -> {
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
                                    player.getName() + " did not find their block in time! Their block was "
                            ).append(Component.text(Helpers.materialToDisplayName(blocks.get(player))).decorate(TextDecoration.BOLD)).color(NamedTextColor.RED)
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

                    participants.forEach(plr -> plr.setGameMode(GameMode.SURVIVAL));
                    waitingPlayers.forEach(plr -> plr.setGameMode(GameMode.SURVIVAL));
                    MinigameController.stopMinigame();
                }, 5 * 20);

                return;
            }

            if(qualifiers.size() == aliveParticipants.size() && !eliminatedPlayers.get()) {
                Bukkit.broadcast(
                        Component.text("Everyone has found their block in time! Continuing...").color(NamedTextColor.YELLOW)
                );
            }

            Game();
        }, () -> blocks.isEmpty() || !isActive);
    }

    @Override
    public void end() {
        blocks.clear();
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

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!isActive || !blocks.containsKey(player)) return;

        Block belowBlock = player.getLocation().add(0, -1, 0).getBlock();
        if(belowBlock.getType() == blocks.get(player)) {
            blocks.remove(player);
            qualifiers.add(player);

            Bukkit.broadcast(
                    Component.text(
                            player.getName() + " found their block! Their block was "
                    ).append(Component.text(Helpers.materialToDisplayName(belowBlock.getType())).decorate(TextDecoration.BOLD)).color(NamedTextColor.GREEN)
            );
        }
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(!isActive || !blocks.containsKey(player)) return;

        if(player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        blocks.remove(player);
        participants.remove(player);
        aliveParticipants.remove(player);
        qualifiers.remove(player);
        waitingPlayers.remove(player);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!isActive) return;

        player.setGameMode(GameMode.SPECTATOR);
        waitingPlayers.add(player);

        player.sendMessage(
                Component.text("A game of ")
                        .append(Component.text("Block Shuffle").decorate(TextDecoration.BOLD))
                        .append(Component.text(" is currently active. You will exit spectator once the game ends."))
                        .color(NamedTextColor.GREEN)
        );
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }
}
