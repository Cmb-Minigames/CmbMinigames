package xyz.devcmb.cmbminigames.controllers.minigames;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.devcmb.cmbminigames.CmbMinigames;
import xyz.devcmb.cmbminigames.Constants;
import xyz.devcmb.cmbminigames.controllers.MinigameController;
import xyz.devcmb.cmbminigames.util.Helpers;
import xyz.devcmb.cmbminigames.util.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeathSwapController implements Minigame {
    public boolean isActive = false;

    public final List<Player> players = new ArrayList<>();
    public final List<Player> alivePlayers = new ArrayList<>();
    public final List<Player> waitingPlayers = new ArrayList<>();

    @Override
    public String getId() {
        return "deathswap";
    }

    @Override
    public String getName() {
        return "Death Swap";
    }

    @Override
    public Integer minimumPlayers() {
        return 2;
    }

    @Override
    public Component getHowToPlay() {
        return
                Component.text("Welcome to ")
                        .append(Component.text("Death Swap").decorate(TextDecoration.BOLD).color(NamedTextColor.DARK_GRAY))
                        .append(Component.newline())
                        .append(Component.text("The premise is simple, every ")
                                .append(Component.text(Math.round(Constants.DeathSwapTimer / 60f)).append(Component.text(" minutes, ")).color(NamedTextColor.AQUA))
                        )
                        .append(Component.text("players will swap places, attempting to make all other players die."))
                        .append(Component.newline())
                        .append(Component.text("You can jump off a cliff, plunge into lava, or whatever else comes to mind right before the swap in order to get who swaps with you killed."))
                        .append(Component.newline())
                        .append(Component.text("If you are killed, then you will be ").append(Component.text("ELIMINATED").color(NamedTextColor.RED).decorate(TextDecoration.BOLD)))
                        .append(Component.newline())
                        .append(Component.text("Have fun!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
        ;
    }

    @Override
    public void start() {
        players.addAll(Bukkit.getOnlinePlayers());
        alivePlayers.addAll(players);

        Helpers.Countdown(players, 10, Component.text("The game is about to begin!"), this::Game, () -> !isActive);
    }

    private void Game() {
        Timer.CreateTimer("deathswap_timer", Constants.DeathSwapTimer, (timeLeft) -> {
            alivePlayers.removeIf(plr -> !plr.isOnline());
            players.removeIf(plr -> !plr.isOnline());
            waitingPlayers.removeIf(plr -> !plr.isOnline());

            if(players.isEmpty()) {
                MinigameController.stopMinigame();
                return;
            }

            players.forEach(player -> player.sendActionBar(
                    Component.text(Helpers.formatTime(timeLeft))
                            .color(NamedTextColor.YELLOW)
                            .decorate(TextDecoration.BOLD)
            ));

            if(timeLeft == 10) {
                Helpers.Countdown(alivePlayers, 10, Component.text("Swapping in..."), () -> {}, () -> false);
            }
        }, (early) -> {
            if(early) {
                endGame();
                return;
            }

            swap();
            Game();
        }, () -> alivePlayers.size() <= 1 || !isActive);
    }

    private void endGame(){
        if(!isActive) return;

        players.forEach(player -> Helpers.GameEndAnnouncement(
                player,
                player.equals(alivePlayers.getFirst()) ? Helpers.GameEndStatus.VICTORY : Helpers.GameEndStatus.DEFEAT,
                Component.text("You will be placed into survival in 5 seconds")
        ));

        Bukkit.getScheduler().runTaskLater(CmbMinigames.getPlugin(), () -> {
            if(!isActive) return;
            MinigameController.stopMinigame();
        }, 5 * 20);
    }

    private void swap() {
        // Thanks copilot this was probably the easiest code I could've written myself :D
        List<Player> swapPlayers = new ArrayList<>(alivePlayers);
        if (swapPlayers.size() < 2) {
            endGame();
            return;
        }

        Collections.shuffle(swapPlayers);

        List<Location> targetLocations = new ArrayList<>();
        for (Player target : swapPlayers) {
            targetLocations.add(target.getLocation().clone());
        }

        for (int i = 0; i < swapPlayers.size(); i++) {
            Player player = swapPlayers.get(i);
            Location swapTo = targetLocations.get((i + 1) % swapPlayers.size());
            player.teleport(swapTo);
        }
    }

    @Override
    public void end() {
        players.forEach(plr -> plr.setGameMode(GameMode.SURVIVAL));
        waitingPlayers.forEach(plr -> plr.setGameMode(GameMode.SURVIVAL));

        players.clear();
        alivePlayers.clear();
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event){
        if(!isActive) return;

        Player player = event.getPlayer();
        alivePlayers.remove(player);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        if(!isActive) return;

        Player player = event.getPlayer();
        waitingPlayers.add(player);
        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void playerLeave(PlayerJoinEvent event){
        if(!isActive) return;

        Player player = event.getPlayer();
        if(player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.setGameMode(GameMode.SURVIVAL);
        }
//        alivePlayers.remove(player);
//        players.remove(player);
//        waitingPlayers.remove(player);
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean getActive() {
        return isActive;
    }
}
