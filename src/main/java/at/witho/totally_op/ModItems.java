package at.witho.totally_op;

import at.witho.totally_op.items.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@GameRegistry.ObjectHolder("totally_op")
public class ModItems {
	// Forge fills those with real objects when you register your item in the CommonProxy
    public static final PeacefulWoodTool peaceful_wood_tool = null;
    public static final PeacefulIronTool peaceful_iron_tool = null;
    public static final PeacefulDiamondTool peaceful_diamond_tool = null;
    public static final Alphorn alphorn = null;
    public static final Coin coin = null;
    public static final Euro euro = null;
    public static final RubberBoots rubber_boots = null;
    public static final ScubaHelmet scuba_helmet = null;
    public static final Rucksack rucksack = null;


    @SideOnly(Side.CLIENT)
    public static void initModels() {
    	peaceful_wood_tool.initModel();
    	peaceful_iron_tool.initModel();
		peaceful_diamond_tool.initModel();
		alphorn.initModel();
        coin.initModel();
        euro.initModel();
        rubber_boots.initModel();
        scuba_helmet.initModel();
        rucksack.initModel();
    }

}
