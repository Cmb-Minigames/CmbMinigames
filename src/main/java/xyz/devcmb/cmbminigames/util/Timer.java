package xyz.devcmb.cmbminigames.util;

import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class Timer {
    public static Map<String, Integer> timers = new HashMap<>();

    public static void CreateTimer(String id, int length, IntConsumer tick, Consumer<Boolean> complete, BooleanSupplier cancel) {
        if (timers.containsKey(id)) {
            throw new IllegalArgumentException("Timer with the id " + id + " already exists");
        }

        timers.put(id, length);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cancel.getAsBoolean()) {
                    this.cancel();
                    TimerEnd(id, complete, true);
                    return;
                }

                Integer timeLeft = timers.get(id);
                if (timeLeft == 0) {
                    this.cancel();
                    TimerEnd(id, complete, false);
                    return;
                }

                timers.put(id, timeLeft - 1);
                tick.accept(timeLeft - 1);
            }
        }.runTaskTimer(CmbMinigames.getPlugin(), 20, 20);
    }

    private static void TimerEnd(String id, Consumer<Boolean> completion, boolean endedEarly) {
        if (!timers.containsKey(id)) {
            throw new IllegalArgumentException("Timer with the id " + id + " does not exist");
        }

        timers.remove(id);
        completion.accept(endedEarly);
    }
}