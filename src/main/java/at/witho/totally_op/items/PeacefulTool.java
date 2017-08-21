package at.witho.totally_op.items;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.List;
import at.witho.totally_op.Helper;
import at.witho.totally_op.TotallyOP;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PeacefulTool extends ItemTool {
	protected ConcurrentLinkedQueue<BlockPos> blockPositionsToBreak = new ConcurrentLinkedQueue<BlockPos>();
	protected Block blockToBreak = null;
	protected EntityPlayerMP player = null;
	protected World world = null;
	protected int magnetRange = 0;
	protected int magnetActive = 0; 
	
	public PeacefulTool(ToolMaterial material, String name, int magnetRange) {
		super(material, new HashSet<>());
		setRegistryName(name);
		setUnlocalizedName(TotallyOP.MODID + "." + name);
		this.magnetRange = magnetRange;
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }    

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
		magnetActive = 20; 
		return super.onEntitySwing(entityLiving, stack);
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
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		if (!worldIn.isRemote && magnetRange > 0 && magnetActive > 0) {
			magnetActive--;
			double x = entityIn.posX;
			double y = entityIn.posY;
			double z = entityIn.posZ;
			List<EntityItem> items = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(x - magnetRange, y - magnetRange, z - magnetRange, x + magnetRange, y + magnetRange, z + magnetRange));
			for(EntityItem item : items) {
				item.setPosition(x,  y,  z);
			}
		}
    }
	
	@Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!worldIn.isRemote) {
			boolean wasEmpty = blockPositionsToBreak.isEmpty(); 
			player = (EntityPlayerMP) entityLiving;
			world = worldIn;
			if (state.getBlock() == Blocks.AIR) return false;
			//TotallyOP.logger.log(Level.ERROR, "btb = " + blockToBreak + ", block = " + state.getBlock());
			if (player.isSneaking() && (blockToBreak == null || Helper.isSameBlock(blockToBreak, state.getBlock()))) {
				for (int x = -1; x < 2; ++x) {
					for (int y = -1; y < 2; ++y) {
						for (int z = -1; z < 2; ++z) {
							if (x != 0 || y != 0 || z != 0) {
								BlockPos p = pos.add(x, y, z);
								IBlockState pstate = worldIn.getBlockState(p);
								if (Helper.isSameBlock(pstate.getBlock(), state.getBlock())) blockPositionsToBreak.add(p);
							}
						}
					}
				}
				if (!blockPositionsToBreak.isEmpty() && wasEmpty) {
					blockToBreak = state.getBlock();
					MinecraftForge.EVENT_BUS.register(this);
				}
			}
			magnetActive = 20;
		}
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}

	@SubscribeEvent
	public void onPostServerTick(ServerTickEvent event) {
		if (player == null || world == null) return;
		BlockPos p;
		while ((p = blockPositionsToBreak.poll()) != null) {
			IBlockState pstate = world.getBlockState(p);
			if (pstate.getBlock() != Blocks.AIR) {
				player.interactionManager.tryHarvestBlock(p);
				break;
			}
		}
		if (blockPositionsToBreak.isEmpty()) {
			MinecraftForge.EVENT_BUS.unregister(this);
			blockToBreak = null;
		}
	}
}

