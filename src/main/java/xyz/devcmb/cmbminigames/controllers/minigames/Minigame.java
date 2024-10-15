package xyz.devcmb.cmbminigames.controllers.minigames;
import org.bukkit.entity.Player;

public interface Minigame {
    String getId();
    String getName();
    String getDescription();
    String getVersion();

    void activateGame(Player player, Boolean automaticTrigger);
    void deactivateGame(Player player, Boolean automaticTrigger);
    void startGame(Player player, Boolean automaticTrigger);
    void endGame(Boolean automaticTrigger);
    void resetGame(Boolean automaticTrigger);
}