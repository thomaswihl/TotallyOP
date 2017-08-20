package at.witho.totally_op;

import at.witho.totally_op.blocks.FarmingFlower;
import at.witho.totally_op.blocks.PeacefulFlower;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;;

public class ModBlocks {
	@GameRegistry.ObjectHolder("totally_op:peaceful_flower")
    public static final PeacefulFlower peacefulFlower = null;
	@GameRegistry.ObjectHolder("totally_op:farming_flower")
    public static final FarmingFlower farmingFlower = null;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        peacefulFlower.initModel();
        farmingFlower.initModel();
    }

}
