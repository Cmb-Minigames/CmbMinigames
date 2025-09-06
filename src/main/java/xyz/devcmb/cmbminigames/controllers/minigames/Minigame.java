package xyz.devcmb.cmbminigames.controllers.minigames;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Listener;

public interface Minigame extends Listener {
    String getId();
    String getName();
    Integer minimumPlayers();
    Component getHowToPlay();


    default void load(){}
    default void unload(){}

    void start();
    void end();

    void setActive(boolean active);
    boolean getActive();
}
