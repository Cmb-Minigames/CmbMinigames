package xyz.devcmb.cmbminigames.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.devcmb.cmbminigames.misc.ModelDataConstants;

public class PartyMenu {
    public static void initialize(Player player){
        Inventory partyMenu = Bukkit.createInventory(player, 27, ChatColor.WHITE + "Party");
        ItemStack you = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta meta = (SkullMeta) you.getItemMeta();
        if(meta != null){
            meta.setOwningPlayer(player);
            meta.setItemName("You");
            you.setItemMeta(meta);
        }

        partyMenu.setItem(11, you);

        for (int i = 12; i < 16; i++) {
            ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD);
            ItemMeta shardMeta = shard.getItemMeta();
            shardMeta.setItemName("Empty Slot");
            shardMeta.setCustomModelData(ModelDataConstants.EMPTY_PARTY_SLOT);
            shard.setItemMeta(shardMeta);
            partyMenu.setItem(i, shard);
        }

        player.openInventory(partyMenu);
    }
}
