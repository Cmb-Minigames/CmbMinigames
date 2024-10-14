package xyz.devcmb.cmbminigames.controllers.minigames;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.misc.Utilities;

import java.util.Arrays;
import java.util.List;

public class ManhuntController implements Minigame {
    public static List<Player> Runners = List.of();
    public static List<Player> Hunters = List.of();


    public void setRunner(Player player){
        if(!MinigameController.isMinigameActive(this.getId()) || MinigameController.isMinigameBeingPlayed(this.getId())){
            player.sendMessage(ChatColor.RED + "Manhunt is not currently active or has already started");
            return;
        }

        Hunters.remove(player);

        if(!Runners.contains(player)){
            Runners.add(player);
        }
    }

    public void setHunter(Player player){
        if(!MinigameController.isMinigameActive(this.getId()) || MinigameController.isMinigameBeingPlayed(this.getId())){
            player.sendMessage(ChatColor.RED + "Manhunt is not currently active or has already started");
            return;
        }

        Runners.remove(player);

        if(!Hunters.contains(player)){
            Hunters.add(player);
        }
    }

    @Override
    public String getId() {
        return "manhunt";
    }

    @Override
    public String getName() {
        return "Manhunt";
    }

    @Override
    public String getDescription() {
        return "Beat the game while being hunted.";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public void activateGame(Player player) {
        Utilities.AnnounceMinigame(this);
    }

    @Override
    public void deactivateGame(Player player) {}

    @Override
    public void startGame(Player player){
        if(
            (Runners.isEmpty() || Hunters.isEmpty())
            && !CmbMinigames.DeveloperModeEnabled
        ){
            player.sendMessage(ChatColor.RED + "Not enough players to start the game");
            return;
        }

        Utilities.doCountdown(player, 10);

        for(Player runner : Runners) {
            runner.getInventory().clear();
            runner.teleport(runner.getWorld().getSpawnLocation());
        }

        for(Player hunter : Hunters){
            hunter.getInventory().clear();
            hunter.setGameMode(GameMode.SPECTATOR);
        }

        new BukkitRunnable(){
            @Override
            public void run(){
                for (Player hunter : Hunters){
                    hunter.setGameMode(GameMode.SURVIVAL);
                    ItemStack compass = new ItemStack(Material.COMPASS);
                    ItemMeta meta = compass.getItemMeta();
                    if (meta != null) {
                        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        compass.setItemMeta(meta);
                    }
                    player.getInventory().addItem(compass);
                }
            }
        }.runTaskLater(CmbMinigames.getPlugin(), 200);

        new BukkitRunnable(){
            @Override
            public void run() {
                if(
                    ((Runners.isEmpty() || Hunters.isEmpty())
                    || !MinigameController.isMinigameBeingPlayed(getId()))
                    && (!CmbMinigames.DeveloperModeEnabled)
                ){
                    this.cancel();
                    return;
                }
                for (Player hunter : Hunters) {
                    hunter.teleport(hunter.getWorld().getSpawnLocation());

                    Player closestRunner = null;
                    double closestDistance = Double.MAX_VALUE;

                    for (Player runner : Runners) {
                        double distance = hunter.getLocation().distance(runner.getLocation());
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestRunner = runner;
                        }
                    }

                    if (closestRunner != null) {
                        hunter.setCompassTarget(closestRunner.getLocation());
                    }
                }
            }
        }.runTaskTimer(CmbMinigames.getPlugin(), 0, 100);
        if(Hunters.isEmpty()){
            for(Player runner: Runners){
                runner.sendMessage(ChatColor.YELLOW + "All of the hunters have left the game, and the game has been ended");
            }
            this.resetGame();
            MinigameController.endMinigame(null, this.getId(), true);
        }
    }

    @Override
    public void endGame(Player player) {

    }

    @Override
    public void resetGame() {
        Runners.clear();
        Hunters.clear();
    }
}
