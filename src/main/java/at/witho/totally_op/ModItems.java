package at.witho.totally_op;

import at.witho.totally_op.items.PeacefulIronTool;
import at.witho.totally_op.items.PeacefulWoodTool;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;;

public class ModItems {

    @GameRegistry.ObjectHolder("totally_op:peaceful_wood_tool")
    public static final PeacefulWoodTool peacefulWoodTool = null;
    @GameRegistry.ObjectHolder("totally_op:peaceful_iron_tool")
    public static final PeacefulIronTool peacefulIronTool = null;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
    	peacefulWoodTool.initModel();
    	peacefulIronTool.initModel();
    }

}
