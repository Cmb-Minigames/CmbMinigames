![CmbMinigamesBanner.png](https://github.com/29cmb/CmbMinigames/blob/master/docs/images/CmbMinigamesBanner.png?raw=true)

# Cmb Minigames
An open source minecraft plugin for playing a variety of minigames with your friends

### Disclaimer
This is a **plugin**, not a mod, meaning you need a spigot or paper server to run it. It also requires at least 2 players to start any minigames (unless debugging mode is enabled)

### Usage
In order to interface with the commands of the plugin, all the subcommands are accessible under the `/minigame` command

`/minigame start <manhunt | blockshuffle | ...>` - Start a minigame with the requested ID

`/minigame forcestop` - Force stop the active minigame. **May lead to buggy behavior**

`/minigame list` - See all the playable minigames

## Minigames
- Manhunt
    - Players pick if they want to be a runner or a hunter using the `/runner` and `/hunter` commands
    - Once the game starts after 30 seconds, runners will be placed into survival and put at the world spawn
    - After another 15 seconds, hunters are also put into survival and put at world spawn
    - Hunters win if they kill all the runners
    - Runners win if they kill the ender dragon
    - Hunters have unlimited lives, and runners will be ELIMINATED if they die
- Block Shuffle
    - Players are assigned a block every 5 minutes
    - Everyone must obtain and stand on their assigned block before the timer runs out
    - If you do not find your block in time you will be ELIMINATED
- Death Swap
    - Every 7 minutes, players will be swapped with one another
    - The goal of the game is to kill the other players and be the last man standing
    - If you die, you will be ELIMINATED
- Death Shuffle
    - This minigame is very similar to block shuffle
    - Every 5 minutes you will be assigned a source of death
    - Everyone must die to that source before the timer runs out
    - If you do not die by the proper source, you will be ELIMINATED

### Available on modrinth!
The plugin is available for download on the Modrinth website! Click [here](https://modrinth.com/plugin/cmb-minigames) to download it!