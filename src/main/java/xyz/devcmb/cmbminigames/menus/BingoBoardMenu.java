package xyz.devcmb.cmbminigames.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devcmb.cmbminigames.misc.Utilities;

import java.util.List;
import java.util.Map;

public class BingoBoardMenu {
    public static void initialize(Player p, Map<Material, Boolean> board){
        Inventory inv = Bukkit.createInventory(p, 45, ChatColor.WHITE + "Board");

        int[] positions = {
            2, 3, 4, 5, 6,
            11, 12, 13, 14, 15,
            20, 21, 22, 23, 24,
            29, 30, 31, 32, 33,
            38, 39, 40, 41, 42
        };

        int index = 0;
        for (Map.Entry<Material, Boolean> entry : board.entrySet()) {
            if (index >= positions.length) break;
            ItemStack item;

            if (!entry.getValue()) {
                item = new ItemStack(entry.getKey());
            } else {
                item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta meta = item.getItemMeta();
                if(meta != null){
                    meta.setItemName(Utilities.getDisplayName(entry.getKey()));
                    meta.setLore(List.of(ChatColor.GREEN + "Completed"));
                    item.setItemMeta(meta);
                }
            }
            inv.setItem(positions[index], item);
            index++;
        }

        p.openInventory(inv);
    }
}
