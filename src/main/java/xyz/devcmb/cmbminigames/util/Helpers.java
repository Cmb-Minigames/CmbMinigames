package xyz.devcmb.cmbminigames.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class Helpers {
    public static void Countdown(List<Player> players, int length, Component subtitle, Runnable callback, BooleanSupplier cancel) {
        new BukkitRunnable() {
            int timer = length;

            @Override
            public void run() {
                if (timer == 0) {
                    callback.run();
                    this.cancel();
                    return;
                }

                if(cancel.getAsBoolean()) {
                    this.cancel();
                    return;
                }

                players.forEach(player -> {
                    if (timer > 3) {
                        Title title = Title.title(
                                Component.text(">    ")
                                        .append(Component.text(timer).decorate(TextDecoration.BOLD))
                                        .append(Component.text("    <")),
                                subtitle,
                                Title.Times.times(Duration.ZERO, Duration.ofMillis(1000), Duration.ZERO)
                        );

                        player.showTitle(title);
                    } else {
                        switch (timer) {
                            case 1:
                                Title title1 = Title.title(
                                        Component.text("> ")
                                                .append(Component.text("1").decorate(TextDecoration.BOLD))
                                                .append(Component.text(" <")).color(NamedTextColor.RED),
                                        subtitle,
                                        Title.Times.times(Duration.ZERO, Duration.ofMillis(1000), Duration.ZERO)
                                );

                                player.showTitle(title1);
                                break;
                            case 2:
                                Title title2 = Title.title(
                                        Component.text(">  ")
                                                .append(Component.text("2").decorate(TextDecoration.BOLD))
                                                .append(Component.text("  <")).color(TextColor.fromHexString("#fc8403")),
                                        subtitle,
                                        Title.Times.times(Duration.ZERO, Duration.ofMillis(1000), Duration.ZERO)
                                );

                                player.showTitle(title2);
                                break;
                            case 3:
                                Title title3 = Title.title(
                                        Component.text(">   ")
                                                .append(Component.text("3").decorate(TextDecoration.BOLD))
                                                .append(Component.text("   <")).color(NamedTextColor.YELLOW),
                                        subtitle,
                                        Title.Times.times(Duration.ZERO, Duration.ofMillis(1000), Duration.ZERO)
                                );

                                player.showTitle(title3);
                                break;
                        }
                    }
                });

                timer--;
            }
        }.runTaskTimer(CmbMinigames.getPlugin(), 0, 20);
    }

    public static <T> T getRandom(List<T> list) throws IllegalArgumentException {
        if(list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Cannot find a random value of an empty or nonexistent list.");
        }

        return list.get(new Random().nextInt(list.size()));
    }

    public static String materialToDisplayName(Material material) {
        String[] parts = material.name().split("_");
        StringBuilder displayName = new StringBuilder();
        for (String part : parts) {
            if (!displayName.isEmpty()) displayName.append(" ");
            displayName.append(part.substring(0, 1).toUpperCase())
                    .append(part.substring(1).toLowerCase());
        }
        return displayName.toString();
    }

    public static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public static void GameEndAnnouncement(Player player, GameEndStatus status, Component subtitle) {
        Title title = Title.title(status.Title, subtitle);
        player.showTitle(title);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

    public enum GameEndStatus {
        VICTORY(Component.text("VICTORY").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD)),
        DEFEAT(Component.text("DEFEAT").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)),
        NO_PARTICIPATION(Component.text("GAME OVER").decorate(TextDecoration.BOLD).color(NamedTextColor.AQUA));

        final Component Title;
        GameEndStatus(Component title) {
            Title = title;
        }
    }
}
