package at.witho.totally_op;

import at.witho.totally_op.items.ItemPeacefulTool;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;;

public class ModItems {

    @GameRegistry.ObjectHolder("totally_op:peaceful_tool")
    public static ItemPeacefulTool peacefulTool;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
    	peacefulTool.initModel();
    }

}
