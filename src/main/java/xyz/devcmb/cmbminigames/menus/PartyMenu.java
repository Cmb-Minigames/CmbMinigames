package xyz.devcmb.cmbminigames.menus;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.devcmb.cmbminigames.misc.Database;
import xyz.devcmb.cmbminigames.misc.ModelDataConstants;

import java.util.List;
import java.util.UUID;

public class PartyMenu {
    public static void initialize(Player player){
        MongoCollection partyCollection = Database.partyCollection;
        boolean inParty = partyCollection.find(Filters.eq("members", player.getUniqueId().toString())).iterator().hasNext();
        if(inParty){
            showPartyMembers(player);
        } else {
            noPartyMenu(player);
        }
    }

    public static void showPartyMembers(Player player) {
        Inventory partyMenu = Bukkit.createInventory(player, 27, ChatColor.WHITE + "Party");
        ItemStack you = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) you.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setItemName("You");
            you.setItemMeta(meta);
        }

        partyMenu.setItem(10, you);

        MongoCollection<Document> partyCollection = Database.partyCollection;
        Document party = partyCollection.find(Filters.eq("members", player.getUniqueId().toString())).first();
        if(party == null) {
            player.sendMessage(ChatColor.RED + "You are not in a party");
            player.closeInventory();
            return;
        }

        List<String> members = party.getList("members", String.class);
        int slot = 11;
        for (String memberUUID : members) {
            if (!memberUUID.equals(player.getUniqueId().toString())) {
                OfflinePlayer member = Bukkit.getPlayer(UUID.fromString(memberUUID));
                ItemStack memberHead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta memberMeta = (SkullMeta) memberHead.getItemMeta();
                if (memberMeta != null) {
                    memberMeta.setOwningPlayer(member);
                    memberMeta.setItemName(member.getName());
                    memberHead.setItemMeta(memberMeta);
                }
                partyMenu.setItem(slot++, memberHead);
                if (slot > 16) break;
            }
        }

        ItemStack fillerItem = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta fillerMeta = fillerItem.getItemMeta();
        fillerMeta.setItemName("Empty Slot");
        fillerMeta.setCustomModelData(ModelDataConstants.EMPTY_PARTY_SLOT);
        fillerItem.setItemMeta(fillerMeta);

        for (int i = 11; i <= 16; i++) {
            if (partyMenu.getItem(i) == null) {
                partyMenu.setItem(i, fillerItem);
            }
        }

        ItemStack leave = new ItemStack(Material.RED_BED);
        ItemMeta leaveMeta = leave.getItemMeta();
        leaveMeta.setItemName("Leave Party");
        leave.setItemMeta(leaveMeta);
        partyMenu.setItem(18, leave);

        player.openInventory(partyMenu);
    }

    public static void noPartyMenu(Player player){
        Inventory noPartyMenu = Bukkit.createInventory(player, 27, ChatColor.WHITE + "No Party");
        ItemStack create = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta createMeta = create.getItemMeta();
        createMeta.setItemName("Create");
        create.setItemMeta(createMeta);
        noPartyMenu.setItem(11, create);

        ItemStack join = new ItemStack(Material.BLUE_BED);
        ItemMeta joinMeta = join.getItemMeta();
        joinMeta.setItemName("Invites");

        join.setItemMeta(joinMeta);
        noPartyMenu.setItem(15, join);

        player.openInventory(noPartyMenu);
    }

    public static void disbandConfirmation(Player player){
        Inventory disbandConfirmation = Bukkit.createInventory(player, 27, ChatColor.WHITE + "Disband");
        ItemStack confirm = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setItemName("Disband");
        confirm.setItemMeta(confirmMeta);
        disbandConfirmation.setItem(11, confirm);

        ItemStack cancel = new ItemStack(Material.RED_CONCRETE);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setItemName("Cancel");
        cancel.setItemMeta(cancelMeta);
        disbandConfirmation.setItem(15, cancel);

        player.openInventory(disbandConfirmation);
    }
}