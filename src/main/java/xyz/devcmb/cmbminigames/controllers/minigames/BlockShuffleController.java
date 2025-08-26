package xyz.devcmb.cmbminigames.controllers.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.devcmb.cmbminigames.Constants;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.util.Helpers;
import xyz.devcmb.cmbminigames.util.Timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockShuffleController implements Minigame {
    public boolean isActive = false;

    private final Map<Player, Material> blocks = new HashMap<>();
    private final List<Player> players = new ArrayList<>();
    private final List<Player> qualifiers = new ArrayList<>();

    @Override
    public String getId() {
        return "blockshuffle";
    }

    @Override
    public String getName() {
        return "Block Shuffle";
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
        players.clear();
        qualifiers.clear();

        players.addAll(Bukkit.getOnlinePlayers());
        qualifiers.addAll(players);

        Helpers.Countdown(players, 10, Component.text("The game is about to begin!"), this::Game);
    }

    private void Game() {
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
        Timer.CreateTimer("blockshuffle_timer", 60 * 5, (timeLeft) -> {
            blocks.keySet().removeIf(player -> !player.isOnline());
            players.removeIf(player -> !player.isOnline());
            qualifiers.removeIf(player -> !player.isOnline());

            if(players.isEmpty()) {
                MinigameController.stopMinigame();
                return;
            }

            players.forEach(player -> player.sendActionBar(
                    Component.text(Helpers.formatTime(timeLeft))
                            .color(qualifiers.contains(player) ? NamedTextColor.GREEN : NamedTextColor.YELLOW)
                            .decorate(TextDecoration.BOLD)
            ));
        }, (early) -> {
            if(!isActive) return;

            if(qualifiers.isEmpty()) {
                // TODO: Test this
                Bukkit.broadcast(
                        Component.text("No one found their block, so another round will be played!")
                                .color(NamedTextColor.YELLOW)
                );

                Game();
                return;
            }

            if(!blocks.isEmpty()) {
                // TODO: Test this
                blocks.keySet().forEach(player -> {
                    players.remove(player);
                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(
                            Component.text("You have been ")
                                    .append(Component.text("ELIMINATED").decorate(TextDecoration.BOLD))
                                    .color(NamedTextColor.RED)
                    );

                    Bukkit.broadcast(
                            Component.text(
                                    player.getName() + " did not find their block in time! Their block was "
                            ).append(Component.text(Helpers.materialToDisplayName(blocks.get(player))).decorate(TextDecoration.BOLD)).color(NamedTextColor.RED)
                    );
                });
            }

            if(qualifiers.size() == 1 && !Constants.IsDevelopment) {
                // TODO
                MinigameController.stopMinigame();
                return;
            }

            if(qualifiers.size() == players.size()) {
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
        players.clear();
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


    @Override
    public void setActive(boolean active) {
        isActive = active;
    }
}
