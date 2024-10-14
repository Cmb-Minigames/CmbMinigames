package xyz.devcmb.cmbminigames.controllers;

import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.devcmb.cmbminigames.menus.PartyMenu;

import java.util.List;
import java.util.UUID;
import static xyz.devcmb.cmbminigames.misc.Database.partyCollection;

public class PartyController {
    public static void createParty(String partyName, String leader){

    }

    public static void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Document party = (Document) partyCollection.find(Filters.in("members", player.getUniqueId().toString())).first();

        if (party != null) {
            String owner = party.getString("creator");
            if (owner.equals(player.getUniqueId().toString())) {
                disbandParty(party, owner);
            } else {
                partyCollection.updateOne(Filters.eq("creator", owner), new Document("$pull", new Document("members", player.getUniqueId().toString())));
            }
        }
    }

    public static void disbandParty(Document party, String owner) {
        party.getList("members", String.class).forEach(member -> {
            if (!member.equals(owner)) {
                Player target = Bukkit.getPlayer(UUID.fromString(member));
                if (target != null) {
                    target.sendMessage(ChatColor.RED + "The party has been disbanded by the owner.");
                }
            }
        });
        partyCollection.deleteOne(Filters.eq("creator", owner));
    }

    public static boolean isInParty(Player player) {
        boolean inParty = partyCollection.find(Filters.in("members", player.getUniqueId().toString())).iterator().hasNext();
        if (inParty) {
            player.sendMessage(ChatColor.RED + "You are already in a party");
            player.closeInventory();
            return true;
        }
        return false;
    }

    public static void kickMember(Player player, String owner, Player target) {
        partyCollection.updateOne(Filters.eq("creator", owner), new Document("$pull", new Document("members", target.getUniqueId().toString())));
        player.closeInventory();
        PartyMenu.showPartyMembers(player);
    }

    public static void LeaveParty(Player player, String owner){
        partyCollection.updateOne(Filters.eq("creator", owner), new Document("$pull", new Document("members", player.getUniqueId().toString())));
        player.closeInventory();
        PartyMenu.noPartyMenu(player);
    }

    public static void CreateParty(Player player){
        partyCollection.insertOne(new Document("creator", player.getUniqueId().toString()).append("members", List.of(player.getUniqueId().toString())).append("invites", List.of()));
        player.closeInventory();
        PartyMenu.showPartyMembers(player);
    }

    public static void JoinParty(Player player, String playerName){
        Player user = Bukkit.getPlayer(playerName);
        if(user == null){
            player.sendMessage(ChatColor.RED + "This party no longer exists");
            player.closeInventory();
            PartyMenu.invitesMenu(player, true);
            return;
        }

        Document party = (Document)partyCollection.find(Filters.eq("creator", user.getUniqueId().toString())).first();
        if(party == null){
            player.sendMessage(ChatColor.RED + "This party no longer exists");
            player.closeInventory();
            PartyMenu.invitesMenu(player, true);
            return;
        }

        partyCollection.updateOne(Filters.eq("creator", user.getUniqueId().toString()), new Document("$pull", new Document("invites", player.getUniqueId().toString())).append("$push", new Document("members", player.getUniqueId().toString())));
    }

    public static void InviteToParty(Player player, String playerName){
        Player target = Bukkit.getPlayer(playerName);
        if(target == null){
            player.sendMessage(ChatColor.RED + "This player is no longer online");
            player.closeInventory();
            PartyMenu.inviteMemberMenu(player, true);
            return;
        }

        Document party = (Document)partyCollection.find(Filters.in("members", player.getUniqueId().toString())).first();
        if(party == null){
            player.sendMessage(ChatColor.RED + "You are not in a party");
            player.closeInventory();
            return;
        }

        if(party.getList("members", String.class).size() >= 6){
            player.sendMessage(ChatColor.RED + "Your party is full");
            player.closeInventory();
            PartyMenu.showPartyMembers(player);
            return;
        }

        partyCollection.updateOne(Filters.eq("creator", party.getString("creator")), new Document("$push", new Document("invites", target.getUniqueId().toString())));
        player.sendMessage(ChatColor.GREEN + "Invite sent to " + target.getName());
        player.closeInventory();
        PartyMenu.inviteMemberMenu(player, true);
    }
}
