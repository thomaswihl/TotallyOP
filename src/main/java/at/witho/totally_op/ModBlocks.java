package at.witho.totally_op;

import at.witho.totally_op.blocks.BlockPeacefulFlower;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;;

public class ModBlocks {
	@GameRegistry.ObjectHolder("totally_op:peaceful_flower")
    public static BlockPeacefulFlower peacefulFlower;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        peacefulFlower.initModel();
    }

}
