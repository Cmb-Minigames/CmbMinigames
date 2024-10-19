package xyz.devcmb.cmbminigames.controllers.minigames;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.misc.Utilities;
import java.util.ArrayList;
import java.util.List;

public class ManhuntController implements Minigame {
    public List<Player> Runners = new ArrayList<>();
    public List<Player> AliveRunners = new ArrayList<>();
    public List<Player> Hunters = new ArrayList<>();
    public boolean RunnerWin = false;

    public Boolean setRunner(Player player){
        if(!MinigameController.isMinigameActive(this.getId()) || MinigameController.isMinigameBeingPlayed(this.getId())){
            player.sendMessage(ChatColor.RED + "Manhunt is not currently active or has already started");
            return false;
        }

        CmbMinigames.LOGGER.info("Adding player to Runners list");

        Hunters.remove(player);

        if(!Runners.contains(player)){
            Runners.add(player);
        }
        return true;
    }

    public Boolean setHunter(Player player){
        if(!MinigameController.isMinigameActive(this.getId()) || MinigameController.isMinigameBeingPlayed(this.getId())){
            player.sendMessage(ChatColor.RED + "Manhunt is not currently active or has already started");
            return false;
        }

        CmbMinigames.LOGGER.info("Adding player to Hunters list");
        Runners.remove(player);

        if(!Hunters.contains(player)){
            Hunters.add(player);
        }

        return true;
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
    public void activateGame(Player player, Boolean automaticTrigger) {
        Utilities.AnnounceMinigame(this);
    }

    @Override
    public void deactivateGame(Player player, Boolean automaticTrigger) {}

    @Override
    public void startGame(Player player, Boolean automaticTrigger){
        if(
            (Runners.isEmpty() || Hunters.isEmpty())
            && !CmbMinigames.DeveloperModeEnabled
        ){
            if(!automaticTrigger) player.sendMessage(ChatColor.RED + "Not enough players to start the game");
            return;
        }

        CmbMinigames.LOGGER.info("Runners: " + Runners + " | Hunters: " + Hunters);

        for(Player runner : Runners) {
            if(runner != null && runner.isOnline()){
                Utilities.Countdown(runner, 10);
                runner.setGameMode(GameMode.SPECTATOR);
                runner.getInventory().clear();
            }
        }
        for(Player hunter : Hunters){
            if (hunter != null && hunter.isOnline()) {
                Utilities.Countdown(hunter, 10);
                hunter.setGameMode(GameMode.SPECTATOR);
                hunter.getInventory().clear();
            }
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                for(Player runner : Runners) {
                    if(runner != null && runner.isOnline()){
                        AliveRunners.add(runner);
                        runner.teleport(runner.getWorld().getSpawnLocation());
                        runner.setGameMode(GameMode.SURVIVAL);
                        runner.getInventory().clear();
                    }
                }

                new BukkitRunnable(){
                    @Override
                    public void run(){
                        Bukkit.broadcastMessage(ChatColor.GREEN + "The " + Hunters.size() + " hunter(s) have been released!");

                        for (Player hunter : Hunters){
                            if (hunter != null && hunter.isOnline()) {
                                hunter.teleport(hunter.getWorld().getSpawnLocation());
                                hunter.setGameMode(GameMode.SURVIVAL);
                                hunter.getInventory().clear();

                                ItemStack compass = new ItemStack(Material.COMPASS);
                                ItemMeta meta = compass.getItemMeta();
                                if (meta != null) {
                                    meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
                                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                                    compass.setItemMeta(meta);
                                }
                                hunter.getInventory().addItem(compass);
                                Bukkit.getLogger().info("Hunter " + hunter.getName() + " has been released and teleported.");
                            } else {
                                Bukkit.getLogger().warning("Hunter is null or offline.");
                            }
                        }
                    }
                }.runTaskLater(CmbMinigames.getPlugin(), 200);

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(
                                ((AliveRunners.isEmpty() || Hunters.isEmpty())
                                || !MinigameController.isMinigameBeingPlayed(getId()))
                                && (!CmbMinigames.DeveloperModeEnabled)
                        ){
                            this.cancel();
                            if(Hunters.isEmpty()){
                                for(Player runner: Runners){
                                    runner.sendMessage(ChatColor.YELLOW + "All of the hunters have left the game, and the game has been ended");
                                }
                            }

                            CmbMinigames.LOGGER.info("Manhunt minigame has ended");

                            MinigameController.endMinigame(null, getId(), true);
                            MinigameController.resetMinigame(null, getId(), true);
                            return;
                        }
                        for (Player hunter : Hunters) {
                            Player closestRunner = null;
                            double closestDistance = Double.MAX_VALUE;

                            for (Player runner : Runners) {
                                if(runner == null || !runner.isOnline()) continue;
                                if (!hunter.getWorld().equals(runner.getWorld())) continue;

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
            }
        }.runTaskLater(CmbMinigames.getPlugin(), 10);
    }

    @Override
    public void endGame(Boolean automaticTrigger) {
        if(AliveRunners.isEmpty()){
            for(Player hunter: Hunters){
                hunter.sendTitle(ChatColor.BOLD.toString() + ChatColor.GOLD + "VICTORY", "You have won the game!", 5, 120, 5);
                hunter.playSound(hunter.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
            }

            for(Player runner : Runners){
                runner.sendTitle(ChatColor.BOLD.toString() + ChatColor.RED + "DEFEAT", "Better luck next time", 5, 120, 5);
                runner.playSound(runner.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10, 1);
            }
        } else if(Hunters.isEmpty() || RunnerWin){
            for(Player runner : Runners){
                runner.sendTitle(ChatColor.BOLD.toString() + ChatColor.GOLD + "VICTORY", "You have won the game!", 5, 120, 5);
                runner.playSound(runner.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10, 1);
            }

            for(Player hunter : Hunters){
                hunter.sendTitle(ChatColor.BOLD.toString() + ChatColor.RED + "DEFEAT", "Better luck next time", 5, 120, 5);
                hunter.playSound(hunter.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10, 1);
            }
        }
    }

    @Override
    public void resetGame(Boolean automaticTrigger) {
        Runners.clear();
        Hunters.clear();
    }
}
