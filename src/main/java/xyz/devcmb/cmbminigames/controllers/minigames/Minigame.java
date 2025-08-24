package xyz.devcmb.cmbminigames.controllers.minigames;

import org.bukkit.event.Listener;

public interface Minigame extends Listener {
    String getId();
    void start();
    void end();

    void setActive(boolean active);
}
