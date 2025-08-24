package xyz.devcmb.cmbminigames.controllers.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import xyz.devcmb.cmbminigames.util.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockShuffleController implements Minigame {
    public boolean isActive = false;

    private Map<Player, Material> blocks = new HashMap<>();
    private List<Player> qualifiers = new ArrayList<>();

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
        qualifiers.addAll(Bukkit.getOnlinePlayers());
        Helpers.Countdown(qualifiers, 10, Component.text("The game is about to begin!"), () -> {
            Bukkit.broadcast(Component.text("hey sorry this minigame is still in dev....sorry :("));
        });
    }

    @Override
    public void end() {

    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if(!isActive) return;

        // TODO
    }


    @Override
    public void setActive(boolean active) {
        isActive = active;
    }
}
