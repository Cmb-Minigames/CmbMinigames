package xyz.devcmb.cmbminigames.listeners.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.controllers.minigames.BlockBingoController;
import xyz.devcmb.cmbminigames.menus.BingoBoardMenu;
import xyz.devcmb.cmbminigames.misc.Utilities;

import java.util.Map;
import java.util.Objects;

public class BlockBingoListeners implements Listener {
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event){
        if (!MinigameController.isMinigameBeingPlayed("blockbingo")) return;
        Player player = event.getPlayer();

        BlockBingoController blockBingoController = (BlockBingoController) MinigameController.getById("blockbingo");
        Map<Player, Map<Material, Boolean>> boards = blockBingoController.Boards;
        Map<Material, Boolean> board = boards.get(player);
        if(board == null) {
            CmbMinigames.LOGGER.warning("Player " + player.getName() + " has no board");
            return;
        }

        Material block = event.getItem().getItemStack().getType();
        if(board.containsKey(block)){
            board.put(block, true);
            player.sendMessage(ChatColor.YELLOW + "You have found a " + ChatColor.BOLD + Utilities.getDisplayName(block) + "!");

            if(checkBingo(board)){
                Bukkit.broadcastMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + player.getName() + ChatColor.RESET + ChatColor.BLUE + " has won Block Bingo!");
                blockBingoController.winner = player;
                MinigameController.endMinigame(null, "blockbingo", true);
                MinigameController.resetMinigame(null, "blockbingo", true);
            }
        } else {
            CmbMinigames.LOGGER.warning("Player " + player.getName() + " has found a block that isn't in their bingo board " + block);
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        if (!MinigameController.isMinigameActive("blockbingo") || MinigameController.isMinigameBeingPlayed("blockbingo")) return;
        BlockBingoController blockBingoController = (BlockBingoController) MinigameController.getById("blockbingo");
        blockBingoController.playerJoin(playerJoinEvent.getPlayer());
    }

    // thank you copilot
    public boolean checkBingo(Map<Material, Boolean> board) {
        if (board == null) return false;

        Material[] blocks = board.keySet().toArray(new Material[0]);

        for (int i = 0; i < 5; i++) {
            boolean rowBingo = true;
            for (int j = 0; j < 5; j++) {
                if (!board.get(blocks[i * 5 + j])) {
                    rowBingo = false;
                    break;
                }
            }
            if (rowBingo) return true;
        }

        for (int i = 0; i < 5; i++) {
            boolean colBingo = true;
            for (int j = 0; j < 5; j++) {
                if (!board.get(blocks[j * 5 + i])) {
                    colBingo = false;
                    break;
                }
            }
            if (colBingo) return true;
        }

        boolean diag1Bingo = true;
        boolean diag2Bingo = true;
        for (int i = 0; i < 5; i++) {
            if (!board.get(blocks[i * 5 + i])) {
                diag1Bingo = false;
            }
            if (!board.get(blocks[i * 5 + (5 - 1 - i)])) {
                diag2Bingo = false;
            }
        }
        return diag1Bingo || diag2Bingo;
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event){
        if (!MinigameController.isMinigameActive("blockbingo")) return;
        if (event.getItem() == null) return;
        if (!event.getItem().hasItemMeta()) return;
        if (!Objects.requireNonNull(event.getItem().getItemMeta()).getItemName().equals("Board")) return;


        BlockBingoController blockBingoController = (BlockBingoController) MinigameController.getById("blockbingo");
        Map<Player, Map<Material, Boolean>> boards = blockBingoController.Boards;
        Map<Material, Boolean> board = boards.get(event.getPlayer());
        if(board == null) return;

        BingoBoardMenu.initialize(event.getPlayer(), board);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        String title = event.getView().getTitle();

        if(title.equals(ChatColor.WHITE + "Board")){
            event.setCancelled(true);
            return;
        }

        if(!event.isCancelled() && event.getWhoClicked() instanceof Player player && !(event.getCurrentItem() == null)){
            if (!MinigameController.isMinigameBeingPlayed("blockbingo")) return;
            BlockBingoController blockBingoController = (BlockBingoController) MinigameController.getById("blockbingo");
            Map<Player, Map<Material, Boolean>> boards = blockBingoController.Boards;
            Map<Material, Boolean> board = boards.get(player);
            if (board == null) return;

            Material block = event.getCurrentItem().getType();
            if (board.containsKey(block)) {
                board.put(block, true);
                player.sendMessage(ChatColor.YELLOW + "You have found a " + ChatColor.BOLD + Utilities.getDisplayName(block) + "!");

                if (checkBingo(board)) {
                    Bukkit.broadcastMessage(ChatColor.BLUE + ChatColor.BOLD.toString() + player.getName() + ChatColor.RESET + ChatColor.BLUE + " has won Block Bingo!");
                    blockBingoController.winner = player;
                    MinigameController.endMinigame(player, "blockbingo", false);
                }
            }
        }
    }
}
