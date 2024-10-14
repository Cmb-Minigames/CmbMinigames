package xyz.devcmb.cmbminigames.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import xyz.devcmb.cmbminigames.CmbMinigames;

public class GlobalListeners implements Listener {
    // Disable off-handing
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.SWAP_OFFHAND) {
            event.setCancelled(true);
            return;
        }
        if (event.getSlot() == 40 || (event.getSlotType() == InventoryType.SlotType.QUICKBAR && event.getSlot() == 40)) {
            event.setCancelled(true);
            return;
        }
    }
}
