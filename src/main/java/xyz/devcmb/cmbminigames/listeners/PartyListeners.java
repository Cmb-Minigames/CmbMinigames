package xyz.devcmb.cmbminigames.listeners;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.devcmb.cmbminigames.controllers.PartyController;
import xyz.devcmb.cmbminigames.menus.PartyMenu;

import static xyz.devcmb.cmbminigames.misc.Database.partyCollection;

public class PartyListeners implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        if (
            title.equals(ChatColor.WHITE + "Party")
            || title.equals(ChatColor.WHITE + "No Party")
            || title.equals(ChatColor.WHITE + "Disband")
            || title.equals(ChatColor.WHITE + "Invites")
            || title.equals(ChatColor.WHITE + "Invite Member")
        ) {
            event.setCancelled(true);
        }

        if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;

        if (title.equals(ChatColor.WHITE + "Party")) {
            Document party = (Document)partyCollection.find(Filters.in("members", player.getUniqueId().toString())).first();
            if (party == null) {
                player.sendMessage(ChatColor.RED + "You are not in a party");
                player.closeInventory();
                return;
            }

            if (event.getCurrentItem().getType().name().equals("PLAYER_HEAD")) {
                if (event.getCurrentItem().getItemMeta().getItemName().equals("You")) return;

                String owner = party.getString("creator");
                if (owner.equals(player.getUniqueId().toString())) {
                    String playerName = event.getCurrentItem().getItemMeta().getItemName();
                    Player target = Bukkit.getPlayer(playerName);
                    PartyController.kickMember(player, owner, target);
                }
            } else if(event.getCurrentItem().getItemMeta().getItemName().equals("Leave Party")){
                String owner = party.getString("creator");
                if (owner.equals(player.getUniqueId().toString())){
                    PartyMenu.disbandConfirmation(player);
                } else {
                    PartyController.LeaveParty(player, owner);
                }
            } else if(event.getCurrentItem().getItemMeta().getItemName().equals("Empty Slot")) {
                player.closeInventory();
                PartyMenu.inviteMemberMenu(player, true);
            }
        } else if (title.equals(ChatColor.WHITE + "No Party")) {
            if (event.getCurrentItem().getItemMeta().getItemName().equals("Create")) {
                if (PartyController.isInParty(player)) return;
                PartyController.CreateParty(player);
            } else if(event.getCurrentItem().getItemMeta().getItemName().equals("Invites")){
                if (PartyController.isInParty(player)) return;
                player.closeInventory();
                PartyMenu.invitesMenu(player, true);
            }
        } else if(title.equals(ChatColor.WHITE + "Disband")){
            if(event.getCurrentItem().getItemMeta().getItemName().equals("Disband")){
                Document party = (Document)partyCollection.find(Filters.in("members", player.getUniqueId().toString())).first();
                if(party == null){
                    player.sendMessage(ChatColor.RED + "You are not in a party");
                    player.closeInventory();
                    return;
                }

                String owner = party.getString("creator");
                if(owner.equals(player.getUniqueId().toString())){
                    PartyController.disbandParty(party, owner);
                    player.closeInventory();
                    PartyMenu.noPartyMenu(player);
                } else {
                    player.sendMessage(ChatColor.RED + "You are not the party owner");
                    player.closeInventory();
                    PartyMenu.showPartyMembers(player);
                }
            } else if(event.getCurrentItem().getItemMeta().getItemName().equals("Cancel")){
                player.closeInventory();
                PartyMenu.showPartyMembers(player);
            }
        } else if(title.equals(ChatColor.WHITE + "Invites")){
            if(event.getCurrentItem().getItemMeta().getItemName().equals("Close")){
                player.closeInventory();
                PartyMenu.noPartyMenu(player);
            } else if (event.getCurrentItem().getItemMeta().getItemName().equals("Next Page")) {
                player.closeInventory();
                PartyMenu.nextInvitePage(player);
            } else if (event.getCurrentItem().getItemMeta().getItemName().equals("Previous Page")) {
                player.closeInventory();
                PartyMenu.previousInvitePage(player);
            } else if(event.getCurrentItem().getItemMeta().getItemName().endsWith("'s Party")) {
                String playerName = event.getCurrentItem().getItemMeta().getItemName().split("'")[0];
                PartyController.JoinParty(player, playerName);
                player.closeInventory();
                PartyMenu.showPartyMembers(player);
            }
        } else if(title.equals(ChatColor.WHITE + "Invite Member")){
            if(event.getCurrentItem().getItemMeta().getItemName().equals("Close")){
                player.closeInventory();
                PartyMenu.showPartyMembers(player);
            } else if(event.getCurrentItem().getItemMeta().getItemName().equals("Next Page")){
                player.closeInventory();
                PartyMenu.nextInviteMemberPage(player);
            } else if(event.getCurrentItem().getItemMeta().getItemName().equals("Previous Page")){
                player.closeInventory();
                PartyMenu.previousInviteMemberPage(player);
            } else {
                String playerName = event.getCurrentItem().getItemMeta().getItemName();
                PartyController.InviteToParty(player, playerName);
                player.closeInventory();
                PartyMenu.showPartyMembers(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PartyController.onLeave(event);
    }
}