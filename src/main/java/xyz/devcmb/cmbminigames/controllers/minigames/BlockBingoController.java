package xyz.devcmb.cmbminigames.controllers.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.misc.BlockBingoBlocks;
import xyz.devcmb.cmbminigames.misc.ModelDataConstants;
import xyz.devcmb.cmbminigames.misc.Utilities;

import java.util.*;

public class BlockBingoController implements Minigame {
    public Map<Player, Map<Material, Boolean>> Boards = new HashMap<>();
    public Player winner = null;

    @Override
    public String getId() {
        return "blockbingo";
    }

    @Override
    public String getName() {
        return "Block Bingo";
    }

    @Override
    public String getDescription() {
        return "Get a random 25 blocks and try to get a bingo!";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void activateGame(Player player, Boolean automaticTrigger) {
        for(Player p : Bukkit.getOnlinePlayers()){
            playerJoin(p);
        }
    }

    public void playerJoin(Player player){
        ItemStack boardView = new ItemStack(Material.PAPER);
        ItemMeta meta = boardView.getItemMeta();
        if(meta != null){
            meta.setItemName("Board");
            meta.setCustomModelData(ModelDataConstants.BLOCK_BINGO_BOARD);
            boardView.setItemMeta(meta);
        }

        player.getInventory().addItem(boardView);
        Boards.put(player, new HashMap<>());

        Set<Material> usedBlocks = new HashSet<>();
        Material[] blocks = BlockBingoBlocks.Blocks;
        Random random = new Random();

        for (int i = 0; i < 25; i++) {
            Material selectedBlock;
            do {
                int randomIndex = random.nextInt(blocks.length);
                selectedBlock = blocks[randomIndex];
            } while (usedBlocks.contains(selectedBlock));

            usedBlocks.add(selectedBlock);
            Boards.get(player).put(selectedBlock, false);
        }
    }

    @Override
    public void deactivateGame(Player player, Boolean automaticTrigger) {
        Boards.clear();
    }

    @Override
    public void startGame(Player player, Boolean automaticTrigger) {
        if(
            Bukkit.getOnlinePlayers().size() < 2
            && !CmbMinigames.DeveloperModeEnabled
        ){
            if(!automaticTrigger) player.sendMessage(ChatColor.RED + "There are not enough players to start the game");
            return;
        }

        for(Player player1 : Boards.keySet()){
            if(!player1.isOnline()) {
                Boards.remove(player1);
                continue;
            }

            Utilities.Countdown(player1, 10);
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player player1 : Boards.keySet()){
                    if(!player1.isOnline()) {
                        Boards.remove(player1);
                        continue;
                    }

                    player1.teleport(player1.getWorld().getSpawnLocation());
                    player1.setGameMode(GameMode.SURVIVAL);
                    player1.getInventory().forEach(item -> {
                        if (item != null && item.getType() != Material.PAPER) {
                            player1.getInventory().remove(item);
                        }
                    });
                }
            }
        }.runTaskLater(CmbMinigames.getPlugin(), 10 * 20);

    }

    @Override
    public void endGame(Boolean automaticTrigger) {
        for(Player player1 : Boards.keySet()){
            if(!player1.isOnline()) continue;
            player1.getInventory().clear();
            player1.setGameMode(GameMode.SPECTATOR);
            if(player1 == winner){
                player1.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "VICTORY", null, 5, 60, 5);
            } else {
                player1.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "DEFEAT", null, 5, 60, 5);
            }
        }
    }

    @Override
    public void resetGame(Boolean automaticTrigger) {
        Boards.clear();
    }
}
