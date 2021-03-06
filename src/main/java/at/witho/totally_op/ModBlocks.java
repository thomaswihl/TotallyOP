package at.witho.totally_op;

import at.witho.totally_op.blocks.*;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
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
    public static final FluidSuckingFlower fluid_sucking_flower = null;
    public static final PlacingFlower placing_flower = null;
    public static final BreakingFlower breaking_flower = null;
    public static final CompressingFlower compressing_flower = null;
    public static final FishingFlower fishing_flower = null;
    public static final SplittingFlower splitting_flower = null;
    public static final CuttingFlower cutting_flower = null;
    public static final TierableBlock fortune = null;
    public static final TierableBlock efficiency = null;
    public static final TierableBlock range = null;
    public static final SunPowder sun_powder = null;
    public static final ThinAsAir thin_as_air = null;
    public static final WitherMesh wither_mesh = null;
    //public static final SteadyVine steady_vine = null;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        peaceful_flower.initModel();
        peaceful_double_flower.initModel();
        farming_flower.initModel();
        sucking_flower.initModel();
        fluid_sucking_flower.initModel();
        placing_flower.initModel();
        breaking_flower.initModel();
        compressing_flower.initModel();
        fishing_flower.initModel();
        splitting_flower.initModel();
        cutting_flower.initModel();
        fortune.initModel();
        efficiency.initModel();
        range.initModel();
        sun_powder.initModel();
        thin_as_air.initModel();
        wither_mesh.initModel();
        //steady_vine.initModel();
    }

}
