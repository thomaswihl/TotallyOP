package at.witho.totally_op.blocks;

import at.witho.totally_op.TotallyOP;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.util.EnumFacing;

public class SteadyVine extends BlockVine {
    public static final String name = "steady_vine";
    public SteadyVine() {
        super();
        setUnlocalizedName(TotallyOP.MODID + "." + name);
        setRegistryName(name);
        setTickRandomly(false);
        setHardness(0.2F);
        setSoundType(SoundType.PLANT);
    }
}
