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
import xyz.devcmb.cmbminigames.misc.Database;
import xyz.devcmb.cmbminigames.misc.ModelDataConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static xyz.devcmb.cmbminigames.misc.Database.partyCollection;

public class PartyMenu {
    public static void initialize(Player player){
        MongoCollection partyCollection = Database.partyCollection;
        boolean inParty = partyCollection.find(Filters.in("members", player.getUniqueId().toString())).iterator().hasNext();
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
        Document party = partyCollection.find(Filters.in("members", player.getUniqueId().toString())).first();
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

    private static int invitesPage = 0;

    public static void nextInvitePage(Player player){
        invitesPage++;
        invitesMenu(player, false);
    }

    public static void previousInvitePage(Player player){
        invitesPage--;
        invitesMenu(player, false);
    }

   public static void invitesMenu(Player player, boolean resetPage) {
        if (resetPage) invitesPage = 0;
        Inventory invitesMenu = Bukkit.createInventory(player, 27, ChatColor.WHITE + "Invites");

        List<Document> parties = (List<Document>)partyCollection.find(Filters.in("invites", player.getUniqueId().toString())).into(new ArrayList<>());
        int totalPages = (int) Math.ceil((double) parties.size() / 18);

        if (invitesPage < 0) invitesPage = 0;
        if (invitesPage >= totalPages) invitesPage = Math.max(totalPages - 1, 0);

        int startIndex = invitesPage * 18;
        int endIndex = Math.min(startIndex + 18, parties.size());

        for (int i = startIndex; i < endIndex; i++) {
            Document party = parties.get(i);
            ItemStack partyItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta partyMeta = (SkullMeta) partyItem.getItemMeta();
            if (partyMeta != null) {
                partyMeta.setOwningPlayer(Bukkit.getPlayer(UUID.fromString(party.getString("creator"))));
                partyMeta.setItemName(Bukkit.getPlayer(UUID.fromString(party.getString("creator"))).getName() + "'s Party");
                partyItem.setItemMeta(partyMeta);
            }
            invitesMenu.setItem(i - startIndex, partyItem);
        }

        if (invitesPage > 0) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta previousMeta = previousPage.getItemMeta();
            previousMeta.setItemName(ChatColor.GREEN + "Previous Page");
            previousPage.setItemMeta(previousMeta);
            invitesMenu.setItem(18, previousPage);
        }

        if (invitesPage < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setItemName(ChatColor.GREEN + "Next Page");
            nextPage.setItemMeta(nextMeta);
            invitesMenu.setItem(26, nextPage);
        }

        ItemStack closeMenu = new ItemStack(Material.RED_BED);
        ItemMeta closeMeta = closeMenu.getItemMeta();
        closeMeta.setItemName("Close");
        closeMenu.setItemMeta(closeMeta);
        invitesMenu.setItem(22, closeMenu);

        player.openInventory(invitesMenu);
    }

    private static int invitePage = 0;
    public static void inviteMemberMenu(Player player, boolean resetPage) {
        if (resetPage) invitePage = 0;
        Inventory inviteMemberMenu = Bukkit.createInventory(player, 27, ChatColor.WHITE + "Invite Member");
        Document party = (Document) partyCollection.find(Filters.in("members", player.getUniqueId().toString())).first();
        if (party == null) {
            player.sendMessage(ChatColor.RED + "You are not in a party");
            player.closeInventory();
            return;
        }

        if (party.getString("creator").equals(player.getUniqueId().toString())) {
            List<Player> availablePlayers = new ArrayList<>();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                boolean inParty = partyCollection.find(Filters.in("members", onlinePlayer.getUniqueId().toString())).iterator().hasNext();
                if (!inParty) {
                    availablePlayers.add(onlinePlayer);
                }
            }

            int totalPages = (int) Math.ceil((double) availablePlayers.size() / 18);
            if (totalPages == 0) totalPages = 1;

            if (invitePage < 0) invitePage = 0;
            if (invitePage >= totalPages) invitePage = Math.max(totalPages - 1, 0);

            int startIndex = invitePage * 18;
            int endIndex = Math.min(startIndex + 18, availablePlayers.size());

            for (int i = startIndex; i < endIndex; i++) {
                Player onlinePlayer = availablePlayers.get(i);
                ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta playerMeta = (SkullMeta) playerHead.getItemMeta();
                if (playerMeta != null) {
                    playerMeta.setOwningPlayer(onlinePlayer);
                    playerMeta.setItemName(onlinePlayer.getName());
                    playerHead.setItemMeta(playerMeta);
                }
                inviteMemberMenu.addItem(playerHead);
            }

            if (invitePage > 0) {
                ItemStack previousPage = new ItemStack(Material.ARROW);
                ItemMeta previousMeta = previousPage.getItemMeta();
                previousMeta.setItemName(ChatColor.GREEN + "Previous Page");
                previousPage.setItemMeta(previousMeta);
                inviteMemberMenu.setItem(18, previousPage);
            }

            if (invitePage < totalPages - 1) {
                ItemStack nextPage = new ItemStack(Material.ARROW);
                ItemMeta nextMeta = nextPage.getItemMeta();
                nextMeta.setItemName(ChatColor.GREEN + "Next Page");
                nextPage.setItemMeta(nextMeta);
                inviteMemberMenu.setItem(26, nextPage);
            }

            ItemStack closeMenu = new ItemStack(Material.RED_BED);
            ItemMeta closeMeta = closeMenu.getItemMeta();
            closeMeta.setItemName("Close");
            closeMenu.setItemMeta(closeMeta);
            inviteMemberMenu.setItem(22, closeMenu);
        } else {
            player.sendMessage(ChatColor.RED + "You are not the party owner");
            player.closeInventory();
            showPartyMembers(player);
        }

        player.openInventory(inviteMemberMenu);
    }

    public static void nextInviteMemberPage(Player player) {
        invitePage++;
        inviteMemberMenu(player, false);
    }

    public static void previousInviteMemberPage(Player player) {
        invitePage--;
        inviteMemberMenu(player, false);
    }
}