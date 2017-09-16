package at.witho.totally_op.items;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.List;
import at.witho.totally_op.Helper;
import at.witho.totally_op.TotallyOP;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

public class PeacefulTool extends ItemTool {
    protected static final int MAGNET_ACTIVE_TIME = 40;
    protected static final int VEINMINE_COOLDOWN_TIME = 10;
	protected ConcurrentLinkedQueue<BlockPos> blockPositionsToBreak = new ConcurrentLinkedQueue<BlockPos>();
	protected Block blockToBreak = null;
	protected EntityPlayerMP player = null;
	protected World worldUsed = null;
	protected int magnetRange = 0;
	protected int magnetActive = 0;
	protected int cooldown = 0;
	protected int fortune = 0;
	
	public PeacefulTool(ToolMaterial material, String name, int magnetRange, int fortune) {
		super(material, new HashSet<>());
		setRegistryName(name);
		setUnlocalizedName(TotallyOP.MODID + "." + name);
		this.magnetRange = magnetRange;
		this.fortune = fortune;
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }    

	@Override
	public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer playerIn, EntityLivingBase entity, EnumHand hand) {
        if (entity instanceof net.minecraftforge.common.IShearable)
        {
            if (!entity.world.isRemote) {
                net.minecraftforge.common.IShearable target = (net.minecraftforge.common.IShearable) entity;
                BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
                if (target.isShearable(itemstack, entity.world, pos)) {
                    java.util.List<ItemStack> drops = target.onSheared(itemstack, entity.world, pos, fortune);

                    java.util.Random rand = new java.util.Random();
                    for (ItemStack stack : drops) {
                        EntityItem ent = entity.entityDropItem(stack, 1.0F);
                        ent.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
                    }
                    itemstack.damageItem(1, entity);
                }
            }
            return true;
        }
        return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            if (!worldIn.isRemote) {
                NBTTagList ench = stack.getEnchantmentTagList();
                NBTTagCompound nbt = ench.getCompoundTagAt(0);
                if (nbt.getShort("id") != 33) {
                    stack.setTagInfo("ench", new NBTTagList());
                    stack.addEnchantment(Enchantment.getEnchantmentByID(33), 1);
                    playerIn.sendMessage(new TextComponentString("Your tool has now silk touch."));
                } else {
                    stack.setTagInfo("ench", new NBTTagList());
                    stack.addEnchantment(Enchantment.getEnchantmentByID(35), fortune);
                    playerIn.sendMessage(new TextComponentString("Your tool has now fortune " + StringUtils.repeat('I', fortune) + "."));
                }
                magnetActive = MAGNET_ACTIVE_TIME;
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

//	@Override
//	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
//		magnetActive = MAGNET_ACTIVE_TIME;
//		return super.onEntitySwing(entityLiving, stack);
//	}

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
		if (!worldIn.isRemote) {
			if (magnetRange > 0 && magnetActive > 0) {
				magnetActive--;
				double x = entityIn.posX;
				double y = entityIn.posY;
				double z = entityIn.posZ;
				List<EntityItem> items = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(x - magnetRange, y - magnetRange, z - magnetRange, x + magnetRange, y + magnetRange, z + magnetRange));
				for(EntityItem item : items) {
					item.setPosition(x,  y,  z);
				}
			}
			if (cooldown > 0) cooldown--;
		}
    }
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			IBlockState state = worldIn.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof IShearable) {
				IShearable shear = (IShearable)block;
				List<ItemStack> drops = shear.onSheared(player.getHeldItem(hand), worldIn, pos, fortune);
                player.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
				BlockPos currentPos = player.getPosition();
				for (ItemStack stack : drops)
					worldIn.spawnEntity(new EntityItem(worldIn, currentPos.getX(), currentPos.getY(), currentPos.getZ(), stack));
			}
		}
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	
	@Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!worldIn.isRemote) {
			boolean wasEmpty = blockPositionsToBreak.isEmpty(); 
			player = (EntityPlayerMP) entityLiving;
			worldUsed = worldIn;
			if (state.getBlock() == Blocks.AIR) return false;
			//TotallyOP.logger.log(Level.ERROR, "btb = " + blockToBreak + ", block = " + state.getBlock());
			if (player.isSneaking()) {
				if (((blockToBreak == null && cooldown == 0) ||
					 (blockToBreak != null && Helper.isSameBlock(blockToBreak, state.getBlock())))) {
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
				}
				if (!blockPositionsToBreak.isEmpty() && wasEmpty) {
					blockToBreak = state.getBlock();
					MinecraftForge.EVENT_BUS.register(this);
				}
			}
			else if (!blockPositionsToBreak.isEmpty()) blockPositionsToBreak.clear();
			magnetActive = MAGNET_ACTIVE_TIME;
			cooldown = VEINMINE_COOLDOWN_TIME;
		}
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}

	@SubscribeEvent
	public void onPostServerTick(ServerTickEvent event) {
		if (player == null || worldUsed == null) return;
		BlockPos p;
		while ((p = blockPositionsToBreak.poll()) != null) {
			IBlockState pstate = worldUsed.getBlockState(p);
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

