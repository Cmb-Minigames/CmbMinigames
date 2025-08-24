package xyz.devcmb.cmbminigames.controllers.minigames;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;

public interface Minigame extends Listener {
    String getId();
    String getName();
    Component getHowToPlay();


    void start();
    void end();

    void setActive(boolean active);
}
