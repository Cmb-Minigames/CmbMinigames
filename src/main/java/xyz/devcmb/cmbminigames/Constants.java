package xyz.devcmb.cmbminigames;

import org.bukkit.Material;

import java.util.List;

public class Constants {
    public static final boolean IsDevelopment = true;
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
                Material.STRIPPED_ACACIA_LOG,
                Material.ACACIA_WOOD,
                Material.STRIPPED_ACACIA_WOOD,
                Material.ACACIA_PLANKS,

                // Dark Oak
                Material.DARK_OAK_LOG,
                Material.STRIPPED_DARK_OAK_LOG,
                Material.DARK_OAK_WOOD,
                Material.STRIPPED_DARK_OAK_WOOD,
                Material.DARK_OAK_PLANKS,

                // Mangrove
                Material.MANGROVE_LOG,
                Material.STRIPPED_MANGROVE_LOG,
                Material.MANGROVE_WOOD,
                Material.STRIPPED_MANGROVE_WOOD,
                Material.MANGROVE_PLANKS,

                // Cherry
                Material.CHERRY_LOG,
                Material.STRIPPED_CHERRY_LOG,
                Material.CHERRY_WOOD,
                Material.STRIPPED_CHERRY_WOOD,
                Material.CHERRY_PLANKS,

                // Bamboo
                Material.BAMBOO_BLOCK,
                Material.STRIPPED_BAMBOO_BLOCK,
                Material.BAMBOO_PLANKS,

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

                // other stuff IDK, im out of ideas
                Material.CACTUS,
                Material.SAND,
                Material.GRASS_BLOCK,
                Material.DIRT,
                Material.GRAVEL,
                Material.CLAY,
                Material.SNOW_BLOCK,
                Material.BAMBOO_BLOCK,

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

                // colored stuff
                Material.WHITE_STAINED_GLASS,
                Material.ORANGE_STAINED_GLASS,
                Material.MAGENTA_STAINED_GLASS,
                Material.LIGHT_BLUE_STAINED_GLASS,
                Material.YELLOW_STAINED_GLASS,
                Material.LIME_STAINED_GLASS,
                Material.PINK_STAINED_GLASS,
                Material.GRAY_STAINED_GLASS,
                Material.LIGHT_GRAY_STAINED_GLASS,
                Material.CYAN_STAINED_GLASS,
                Material.PURPLE_STAINED_GLASS,
                Material.BLUE_STAINED_GLASS,
                Material.BROWN_STAINED_GLASS,
                Material.RED_STAINED_GLASS,
                Material.BLACK_STAINED_GLASS
        );
    }
}
