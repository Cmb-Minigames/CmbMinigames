package xyz.devcmb.cmbminigames.controllers.minigames;

import org.bukkit.entity.Player;

import java.util.List;

public interface Minigame {
    String getId();
    String getName();
    String getDescription();
    String getVersion();

    void activateGame(Player player);
    void deactivateGame(Player player);
    void startGame(Player player);
    void endGame(Player player);
    void resetGame();
}