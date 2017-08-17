package at.witho.totally_op.blocks;

import at.witho.totally_op.TotallyOP;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPeacefulFlower extends BlockBush {
	
	public BlockPeacefulFlower() {
		super(Material.PLANTS, Material.PLANTS.getMaterialMapColor());
		setUnlocalizedName(TotallyOP.MODID + ".peaceful_flower");
		setRegistryName("peaceful_flower");
        this.setTickRandomly(false);
        this.setSoundType(SoundType.PLANT);
        setHardness(0F);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

	@Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return true;
    }

	@Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return true;
	}
    

}
