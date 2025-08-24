package xyz.devcmb.cmbminigames.controllers.minigames;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class BlockShuffleController implements Minigame {
    public boolean isActive = false;

    @Override
    public String getId() {
        return "blockshuffle";
    }

    @Override
    public void start() {

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
