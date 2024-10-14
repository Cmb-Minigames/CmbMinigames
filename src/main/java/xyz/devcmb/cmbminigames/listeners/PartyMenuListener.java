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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import xyz.devcmb.cmbminigames.menus.PartyMenu;
import xyz.devcmb.cmbminigames.misc.Database;

import java.util.List;
import java.util.UUID;

public class PartyMenuListener implements Listener {

    private boolean isPartyWindowOpen(Player player) {
        String title = player.getOpenInventory().getTitle();
        return title.equals(ChatColor.WHITE + "Party") || title.equals(ChatColor.WHITE + "No Party");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        if (title.equals(ChatColor.WHITE + "Party") || title.equals(ChatColor.WHITE + "No Party")) {
            event.setCancelled(true);
        }

        if (title.equals(ChatColor.WHITE + "Party")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;

            MongoCollection partyCollection = Database.partyCollection;
            Document party = (Document)partyCollection.find(Filters.eq("members", player.getUniqueId().toString())).first();
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
                    partyCollection.updateOne(Filters.eq("creator", owner), new Document("$pull", new Document("members", target.getUniqueId().toString())));
                    player.closeInventory();
                    PartyMenu.showPartyMembers(player);
                }
            } else if(event.getCurrentItem().getItemMeta().getItemName().equals("Leave Party")){
                String owner = party.getString("creator");
                if (owner.equals(player.getUniqueId().toString())){
                    PartyMenu.disbandConfirmation(player);
                } else {
                    partyCollection.updateOne(Filters.eq("creator", owner), new Document("$pull", new Document("members", player.getUniqueId().toString())));
                    player.closeInventory();
                    PartyMenu.noPartyMenu(player);
                }
            } else if(event.getCurrentItem().getItemMeta().getItemName().equals("Empty Slot")) {
                // TODO - Invite screen
            }
        } else if (title.equals(ChatColor.WHITE + "No Party")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
            if (event.getCurrentItem().getItemMeta().getItemName().equals("Create")) {
                MongoCollection<Document> partyCollection = Database.partyCollection;
                boolean inParty = partyCollection.find(Filters.eq("members", player.getUniqueId().toString())).iterator().hasNext();
                if (inParty) {
                    player.sendMessage(ChatColor.RED + "You are already in a party");
                    player.closeInventory();
                    return;
                }

                partyCollection.insertOne(new Document("creator", player.getUniqueId().toString()).append("members", List.of(player.getUniqueId().toString())));
                player.closeInventory();
                PartyMenu.showPartyMembers(player);
            }
        } else if(title.equals(ChatColor.WHITE + "Disband")){
            if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) return;
            if(event.getCurrentItem().getItemMeta().getItemName().equals("Disband")){
                MongoCollection<Document> partyCollection = Database.partyCollection;
                Document party = (Document)partyCollection.find(Filters.eq("members", player.getUniqueId().toString())).first();
                if(party == null){
                    player.sendMessage(ChatColor.RED + "You are not in a party");
                    player.closeInventory();
                    return;
                }

                String owner = party.getString("creator");
                if(owner.equals(player.getUniqueId().toString())){
                    party.getList("members", String.class).forEach(member -> {
                        if (!member.equals(owner)) {
                            Player target = Bukkit.getPlayer(UUID.fromString(member));
                            if (target != null) {
                                target.sendMessage(ChatColor.RED + "The party has been disbanded by the owner.");
                            }
                        }
                    });

                    partyCollection.deleteOne(Filters.eq("creator", owner));
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
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (isPartyWindowOpen(player)) {
            event.setCancelled(true);
        }
    }
}