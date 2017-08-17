package at.witho.totally_op.config;

import org.apache.logging.log4j.Level;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

public class Config {
    private static final String CATEGORY_GENERAL = "general";

    // This values below you can access elsewhere in your mod:
    public static float flowersPerChunk = 0.2f;

    // Called from CommonProxy.preInit(). It will create our config if it doesn't
    // exist yet and read the values if it does exist.
    public static void readConfig() {
        Configuration cfg = CommonProxy.config;
        try {
            cfg.load();
            initGeneralConfig(cfg);
        } catch (Exception e1) {
            TotallyOP.logger.log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initGeneralConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
        flowersPerChunk = cfg.getFloat("FlowersPerChunk", CATEGORY_GENERAL, 1, 0, 10, "Specifies the number of flowers per chunk, so 0.1 means one flower every 10 chunks, 10 means 10 flowers in each chunk");
    }

}
