package at.witho.totally_op.config;

import at.witho.totally_op.blocks.FarmingFlower;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

public class Config {
    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_FARMING = "farming";

    public static final int DEFAULT_FORTUNE_MULTIPLIER[] = { 1, 2, 4, 6, 12, 18, 32 };
    public static final int DEFAULT_EFFICIENCY_DELAY[] = { 40, 20, 10, 5, 2, 1, 0 };
    public static final int DEFAULT_SUCKING_RANGE[] = { 1, 3, 5, 7, 9, 11, 15 };

    public static float flowersPerChunk = 0.1f;
    public static Property fortuneMultiplier;
    public static Property efficiencyDelay;
    public static Property suckingRange;

    public static void readConfig() {
        Configuration cfg = CommonProxy.config;
        try {
            cfg.load();
            initGeneralConfig(cfg);
        }
        catch (Exception e1) {
            TotallyOP.logger.log(Level.ERROR, "Problem loading config file!", e1);
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
        cfg.addCustomCategoryComment(CATEGORY_FARMING, "Farming configuration, all arrays mean: Index 0 is no tier block and the rest is tier 1 - 6.");
        fortuneMultiplier = cfg.get(CATEGORY_FARMING, "FortuneMultiplier", DEFAULT_FORTUNE_MULTIPLIER, "The amount the received items are multiplied for each fortune tier below the plant.");
        efficiencyDelay = cfg.get(CATEGORY_FARMING, "EfficiencyDelay", DEFAULT_EFFICIENCY_DELAY, "The number of ticks between operations for each efficiency tier below the plant.");
        suckingRange = cfg.get(CATEGORY_FARMING, "SuckingRange", DEFAULT_SUCKING_RANGE, "The number of blocks that are checked for items in x and z direction, so 3 means a 3x3 area in front of it. there is always a border of one that gets checked as well.");
    }

}
