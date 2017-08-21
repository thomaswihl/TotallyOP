package at.witho.totally_op;

import at.witho.totally_op.items.Alphorn;
import at.witho.totally_op.items.PeacefulDiamondTool;
import at.witho.totally_op.items.PeacefulIronTool;
import at.witho.totally_op.items.PeacefulWoodTool;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {
	// Forge fills those with real objects when you register your item in the CommonProxy
    @GameRegistry.ObjectHolder("totally_op:peaceful_wood_tool")
    public static final PeacefulWoodTool peacefulWoodTool = null;
    @GameRegistry.ObjectHolder("totally_op:peaceful_iron_tool")
    public static final PeacefulIronTool peacefulIronTool = null;
    @GameRegistry.ObjectHolder("totally_op:peaceful_diamond_tool")
    public static final PeacefulDiamondTool peacefulDiamondTool = null;
    @GameRegistry.ObjectHolder("totally_op:alphorn")
    public static final Alphorn alphorn = null;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
    	peacefulWoodTool.initModel();
    	peacefulIronTool.initModel();
		peacefulDiamondTool.initModel();
		alphorn.initModel();
    }

}
