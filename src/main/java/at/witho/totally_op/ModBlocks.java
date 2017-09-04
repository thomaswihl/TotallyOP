package at.witho.totally_op;

import at.witho.totally_op.blocks.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@GameRegistry.ObjectHolder(TotallyOP.MODID)
public class ModBlocks {
	/* Forge will fill these with actual blocks on init, just make sure the names match with the blocks registry name */
    public static final PeacefulFlower peaceful_flower = null;
    public static final PeacefulDoubleFlower peaceful_double_flower = null;
    public static final FarmingFlower farming_flower = null;
    public static final SuckingFlower sucking_flower = null;
    public static final Fortune fortune_tier0 = null;
    public static final Fortune fortune_tier1 = null;
    public static final Fortune fortune_tier2 = null;
    public static final Fortune fortune_tier3 = null;
    public static final Fortune fortune_tier4 = null;
    public static final Fortune fortune_tier5 = null;
    public static final Fortune fortune_tier6 = null;
    public static final Efficiency efficiency_tier0 = null;
    public static final Efficiency efficiency_tier1 = null;
    public static final Efficiency efficiency_tier2 = null;
    public static final Efficiency efficiency_tier3 = null;
    public static final Efficiency efficiency_tier4 = null;
    public static final Efficiency efficiency_tier5 = null;
    public static final Efficiency efficiency_tier6 = null;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        peaceful_flower.initModel();
        peaceful_double_flower.initModel();
        farming_flower.initModel();
        sucking_flower.initModel();
        fortune_tier0.initModel();
        fortune_tier1.initModel();
        fortune_tier2.initModel();
        fortune_tier3.initModel();
        fortune_tier4.initModel();
        fortune_tier5.initModel();
        fortune_tier6.initModel();
        efficiency_tier0.initModel();
        efficiency_tier1.initModel();
        efficiency_tier2.initModel();
        efficiency_tier3.initModel();
        efficiency_tier4.initModel();
        efficiency_tier5.initModel();
        efficiency_tier6.initModel();
    }

}
