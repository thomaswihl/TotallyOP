package at.witho.totally_op.config;

import at.witho.totally_op.blocks.FarmingFlower;
import at.witho.totally_op.blocks.tileentity.TileFarmingFlower;
import at.witho.totally_op.blocks.tileentity.TileFunctionFlower;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

public class Config {
    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_FARMING = "farming";

    public static float flowersPerChunk = 0.1f;
    public static Property fortuneMultiplier;
    public static Property growProbability;
    public static Property efficiencyDelay;
    public static Property farmingRange;
    public static Block fortunateUpgradeTierBlock;
    public static Block efficiencyUpgradeTierBlock;
    public static Block rangeUpgradeTierBlock;
    public static int xpForHarvesting = 1;
    public static boolean friendlyMobs;

    public static void readConfig() {
        Configuration cfg = CommonProxy.config;
        try {
            cfg.load();
            initGeneralConfig(cfg);
        }
        catch (Exception e1) {
            TotallyOP.logger.log(Level.WARN, "Problem loading config file!", e1);
        }
        finally {
            if (cfg.hasChanged()) cfg.save();
        }
    }

    public static int[] intArray(String[] s) {
        int[] result = new int[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = Integer.parseInt(s[i]);
        }
        return result;
    }

    private static void initGeneralConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
        flowersPerChunk = cfg.getFloat("FlowersPerChunk", CATEGORY_GENERAL, 0.1f, 0, 10, "Specifies the number of flowers per chunk, so 0.1 means one flower every 10 chunks, 10 means 10 flowers in each chunk");
        String blockName = cfg.getString("FortunateTierUpgradeBlock", CATEGORY_GENERAL, "minecraft:lapis_block", "The block used to upgrade fortunate to the next tier.");
        fortunateUpgradeTierBlock = Block.getBlockFromName(blockName);
        blockName = cfg.getString("EfficiencyTierUpgradeBlock", CATEGORY_GENERAL, "minecraft:redstone_block", "The block used to upgrade efficiency to the next tier.");
        efficiencyUpgradeTierBlock = Block.getBlockFromName(blockName);
        blockName = cfg.getString("RangeTierUpgradeBlock", CATEGORY_GENERAL, "minecraft:coal_block", "The block used to upgrade range to the next tier.");
        rangeUpgradeTierBlock = Block.getBlockFromName(blockName);
        cfg.addCustomCategoryComment(CATEGORY_FARMING, "Farming configuration, all arrays mean: Index is tier number, so index 0 is tier 0 and index 6 is tier 6.");
        fortuneMultiplier = cfg.get(CATEGORY_FARMING, "TierableItem", TileFunctionFlower.DEFAULT_FORTUNE_CONFIG, "The amount the received items are multiplied for each fortune tier below the plant. 0 means never, 1000 means always.");
        growProbability = cfg.get(CATEGORY_FARMING, "GrowProbability", TileFarmingFlower.DEFAULT_GROW_PROBABILITY_CONFIG, "The probability for executing a grow action on a plant when it can not be harvested.");
        efficiencyDelay = cfg.get(CATEGORY_FARMING, "Efficiency", TileFunctionFlower.DEFAULT_EFFICIENCY_CONFIG, "The number of ticks between operations for each efficiency tier below the plant.");
        farmingRange = cfg.get(CATEGORY_FARMING, "Range", TileFunctionFlower.DEFAULT_RANGE_CONFIG, "The number of blocks that are checked for farming in x and z direction, for sucking in all 3 directions, so 3 means a 3x3 area in front of it or a 3x3x3 cube. The sucking flower adds a border of 1 block.");
        xpForHarvesting = cfg.getInt("XpForHarvesting", CATEGORY_GENERAL, 0, 0, 10, "Specifies the XP amount for harvesting logs or bushes");
        friendlyMobs = cfg.getBoolean("FriendlyMobs", CATEGORY_GENERAL, true, "If you select true and have a peacful tool on you, mobs won't attack you.");
    }

}
