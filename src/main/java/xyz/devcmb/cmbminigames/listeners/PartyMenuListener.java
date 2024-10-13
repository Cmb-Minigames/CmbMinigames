package xyz.devcmb.cmbminigames.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PartyMenuListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getView().getTitle().equals(ChatColor.WHITE + "Party")){
            event.setCancelled(true);
        }
    }
}
