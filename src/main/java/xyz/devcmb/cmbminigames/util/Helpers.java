package xyz.devcmb.cmbminigames.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;

import java.time.Duration;
import java.util.List;

public class Helpers {
    public static void Countdown(List<Player> players, int length, Component subtitle, Runnable callback) {
        new BukkitRunnable() {
            int timer = length;

            @Override
            public void run() {
                if (timer == 0) {
                    callback.run();
                    this.cancel();
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
}
