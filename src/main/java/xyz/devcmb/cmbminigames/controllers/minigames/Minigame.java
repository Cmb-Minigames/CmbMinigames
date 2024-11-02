package xyz.devcmb.cmbminigames.controllers.minigames;
import org.bukkit.entity.Player;

public interface Minigame {
    default String getId(){ return "minigame"; }
    default String getName(){ return "Minigame"; }
    default String getDescription(){ return "This is where we make the minigame"; }
    default String getVersion(){ return "0.0"; }

    void activateGame(Player player, Boolean automaticTrigger);
    void deactivateGame(Player player, Boolean automaticTrigger);
    void startGame(Player player, Boolean automaticTrigger);
    void endGame(Boolean automaticTrigger);
    void resetGame(Boolean automaticTrigger);
}