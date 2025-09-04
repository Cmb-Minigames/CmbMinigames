package xyz.devcmb.cmbminigames;

import org.bukkit.Material;

import java.util.List;

public class Constants {
    public static final boolean IsDevelopment = true;

    // Do later: move these to config
    public static final Integer BlockShuffleTimer = 60 * 5;
    public static final Integer MinimumManhuntRunners = 1;
    public static final Integer MaximumManhuntRunners = Integer.MAX_VALUE;
    public static final Integer MinimumManhuntHunters = 1;
    public static final Integer MaximumManhuntHunters = Integer.MAX_VALUE;
    public static final Integer ManhuntRunnerHeadstart = 20;

    public static List<Material> GetBlockShuffleBlocks() {
        return List.of(
                // Oak
                Material.OAK_LOG,
                Material.STRIPPED_OAK_LOG,
                Material.OAK_WOOD,
                Material.STRIPPED_OAK_WOOD,
                Material.OAK_PLANKS,

                // Spruce
                Material.SPRUCE_LOG,
                Material.STRIPPED_SPRUCE_LOG,
                Material.SPRUCE_WOOD,
                Material.STRIPPED_SPRUCE_WOOD,
                Material.SPRUCE_PLANKS,

                // Birch
                Material.BIRCH_LOG,
                Material.STRIPPED_BIRCH_LOG,
                Material.BIRCH_WOOD,
                Material.STRIPPED_BIRCH_WOOD,
                Material.BIRCH_PLANKS,

                // Jungle
                Material.JUNGLE_LOG,
                Material.STRIPPED_JUNGLE_LOG,
                Material.JUNGLE_WOOD,
                Material.STRIPPED_JUNGLE_WOOD,
                Material.JUNGLE_PLANKS,

                // Acacia
                Material.ACACIA_LOG,
                Material.ACACIA_WOOD,
                Material.ACACIA_PLANKS,

                // Dark Oak
                Material.DARK_OAK_LOG,
                Material.DARK_OAK_WOOD,
                Material.DARK_OAK_PLANKS,

                // Workstations
                Material.CRAFTING_TABLE,
                Material.FURNACE,
                Material.SMOKER,
                Material.BLAST_FURNACE,
                Material.FLETCHING_TABLE,
                Material.SMITHING_TABLE,
                Material.CARTOGRAPHY_TABLE,
                Material.ANVIL,
                Material.LOOM,
                Material.LECTERN,
                Material.BARREL,
                Material.STONECUTTER,
                Material.COMPOSTER,
                Material.GRINDSTONE,

                // Annoying items
                Material.NETHERRACK,
                Material.GREEN_STAINED_GLASS,
                Material.QUARTZ_BLOCK,
                Material.IRON_BLOCK,
                Material.GOLD_BLOCK,
                Material.AMETHYST_BLOCK,
                Material.GREEN_TERRACOTTA,
                Material.GREEN_GLAZED_TERRACOTTA,

                // Underground stuff
                Material.GRANITE,
                Material.ANDESITE,
                Material.DEEPSLATE,
                Material.TUFF,
                Material.BEDROCK,
                Material.STONE,
                Material.SMOOTH_STONE,

                // other stuff IDK, im out of ideas
                Material.CACTUS,
                Material.SAND,
                Material.RED_SAND,
                Material.GRASS_BLOCK,
                Material.DIRT,
                Material.GRAVEL,
                Material.CLAY,
                Material.SNOW_BLOCK,
                Material.HAY_BLOCK,
                Material.MAGMA_BLOCK,

                // redstone
                Material.CRAFTER,
                Material.OBSERVER,
                Material.DAYLIGHT_DETECTOR,
                Material.DISPENSER,
                Material.DROPPER,
                Material.TARGET,
                Material.PISTON,
                Material.HOPPER,
                Material.CHEST,
                Material.JUKEBOX,
                Material.REDSTONE_LAMP,
                Material.LIGHTNING_ROD,

                // crops
                Material.MELON,
                Material.PUMPKIN,
                Material.JACK_O_LANTERN,

                // ores
                Material.IRON_ORE,
                Material.COAL_ORE,
                Material.GOLD_ORE,
                Material.COPPER_ORE,
                Material.REDSTONE_ORE,
                Material.EMERALD_ORE,
                Material.DIAMOND_ORE,
                Material.DEEPSLATE_IRON_ORE,
                Material.DEEPSLATE_GOLD_ORE,
                Material.DEEPSLATE_REDSTONE_ORE,
                Material.DEEPSLATE_DIAMOND_ORE
        );
    }
}
