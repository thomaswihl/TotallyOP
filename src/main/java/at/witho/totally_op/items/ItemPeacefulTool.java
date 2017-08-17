package at.witho.totally_op.items;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.Level;

import at.witho.totally_op.TotallyOP;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPeacefulTool extends ItemTool {
	
	protected ConcurrentLinkedQueue<BlockPos> blocksToBreak = new ConcurrentLinkedQueue<BlockPos>();
	protected EntityPlayerMP player = null;
	protected World world = null;
	
	public ItemPeacefulTool() {
		super(TotallyOP.peacefulMaterial, new HashSet<>());
		setRegistryName("peaceful_tool");
		setUnlocalizedName(TotallyOP.MODID + ".peaceful_tool");
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }    

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState blockState)
	{
		return blockState.getBlock() != Blocks.BEDROCK ? efficiencyOnProperMaterial : 1.0F;
	}

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack)
	{
		return true;
	}
	
	@Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!worldIn.isRemote) {
			boolean wasEmpty = blocksToBreak.isEmpty(); 
			player = (EntityPlayerMP) entityLiving;
			world = worldIn;
			if (state.getBlock() == Blocks.AIR) return false;
			if (player.isSneaking()) {
				for (int x = -1; x < 2; ++x) {
					for (int y = -1; y < 2; ++y) {
						for (int z = -1; z < 2; ++z) {
							if (x != 0 || y != 0 || z != 0) {
								BlockPos p = pos.add(x, y, z);
								IBlockState pstate = worldIn.getBlockState(p);
								if (pstate.getBlock() == state.getBlock()) blocksToBreak.add(p);
							}
						}
					}
				}
				if (!blocksToBreak.isEmpty() && wasEmpty) {
					MinecraftForge.EVENT_BUS.register(this);
				}
			}
		}
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}

	@SubscribeEvent
	public void onPostServerTick(ServerTickEvent event) {
		if (player == null || world == null) return;
		BlockPos p;
		while ((p = blocksToBreak.poll()) != null) {
			IBlockState pstate = world.getBlockState(p);
			if (pstate.getBlock() != Blocks.AIR) {
				player.interactionManager.tryHarvestBlock(p);
				break;
			}
		}
		if (blocksToBreak.isEmpty()) {
			MinecraftForge.EVENT_BUS.unregister(this);
		}
	}
}

