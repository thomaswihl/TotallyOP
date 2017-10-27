package at.witho.totally_op.items;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import at.witho.totally_op.Helper;
import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.util.VeinMiner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
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
import org.omg.CORBA.PRIVATE_MEMBER;

public class PeacefulTool extends ItemTool {
    private static final String VEIN_MINE_INFO = "VeinMineId";
    private static final int MAGNET_MAX_TIME = 80;
    private static final int MAGNET_ACTIVE_TIME = 20;
    private static final int VEINMINE_COOLDOWN_TIME = 10;
    private int magnetRange = 0;
    private int magnetActive = 0;
    private int cooldown = 0;
    private int fortune = 0;
    private int nextId = 1;
    private HashMap<Integer, VeinMiner> veinMiners = new HashMap<>();
	
	public PeacefulTool(ToolMaterial material, String name, int magnetRange, int fortune) {
		super(material, new HashSet<>());
		setRegistryName(name);
		setUnlocalizedName(TotallyOP.MODID + "." + name);
		this.magnetRange = magnetRange;
		this.fortune = fortune;
        MinecraftForge.EVENT_BUS.register(this);
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
				List<EntityItem> items = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(x - magnetRange, y - magnetRange, z - magnetRange, x + magnetRange + 1, y + magnetRange + 1, z + magnetRange + 1));
				for(EntityItem item : items) {
					item.setPosition(x,  y,  z);
				}
                List<EntityXPOrb> xpOrbs = worldIn.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(x - magnetRange, y - magnetRange, z - magnetRange, x + magnetRange + 1, y + magnetRange + 1, z + magnetRange + 1));
                for(EntityXPOrb item : xpOrbs) {
                    item.setPosition(x,  y,  z);
                }
			}
			if (cooldown > 0) cooldown--;
		}
    }

    @Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IShearable) {
            if (!player.world.isRemote) {
                IShearable shear = (IShearable)block;
                List<ItemStack> drops = shear.onSheared(player.getHeldItem(hand), player.world, pos, fortune);
                player.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
                BlockPos currentPos = player.getPosition();
                for (ItemStack stack : drops)
                    player.world.spawnEntity(new EntityItem(player.world, currentPos.getX(), currentPos.getY(), currentPos.getZ(), stack));
            }
            return EnumActionResult.SUCCESS;
		}
        return EnumActionResult.PASS;
	}

	private VeinMiner getVeinMiner(ItemStack stack) {
	    NBTTagCompound comp = stack.getTagCompound();
	    VeinMiner veinMiner = null;
	    if (comp != null && comp.hasKey(VEIN_MINE_INFO)) {
	        int id = comp.getInteger(VEIN_MINE_INFO);
	        veinMiner = veinMiners.get(id);
        }
        return veinMiner;
    }

    private VeinMiner createVeinMiner(ItemStack stack, World world, EntityPlayerMP player, Block block) {
        NBTTagCompound comp = stack.getTagCompound();
        VeinMiner veinMiner = new VeinMiner(world, player, block);
        veinMiners.put(nextId, veinMiner);
        if (comp == null) {
            comp = new NBTTagCompound();
            stack.setTagCompound(comp);
        }
        comp.setInteger(VEIN_MINE_INFO, nextId);
        ++nextId;
        return veinMiner;
    }

    private void removeVeinMiner(ItemStack stack) {
        NBTTagCompound comp = stack.getTagCompound();
        VeinMiner veinMiner = null;
        if (comp != null && comp.hasKey(VEIN_MINE_INFO)) {
            int id = comp.getInteger(VEIN_MINE_INFO);
            veinMiners.remove(id);
            comp.setInteger(VEIN_MINE_INFO, 0);
        }
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
		if (!worldIn.isRemote) {
			VeinMiner veinMiner = getVeinMiner(stack);
            EntityPlayerMP player = (EntityPlayerMP) entityLiving;
			if (state.getBlock() == Blocks.AIR) return false;
			//TotallyOP.logger.log(Level.ERROR, "btb = " + blockToBreak + ", block = " + state.getBlock());
			if (player.isSneaking()) {
				if (veinMiner == null && cooldown == 0) veinMiner = createVeinMiner(stack, worldIn, player, state.getBlock());
				if (veinMiner != null) veinMiner.addBlock(pos);
			}
			else if (veinMiner != null) {
			    removeVeinMiner(stack);
            }
			magnetActive += MAGNET_ACTIVE_TIME;
			if (magnetActive > MAGNET_MAX_TIME) magnetActive = MAGNET_MAX_TIME;
			cooldown = VEINMINE_COOLDOWN_TIME;
		}
		return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
	}

	@SubscribeEvent
	public void onPostServerTick(ServerTickEvent event) {
	    Iterator<Map.Entry<Integer, VeinMiner>> iterator = veinMiners.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, VeinMiner> entry = iterator.next();
            if (!entry.getValue().harvestBlock()) iterator.remove();
        }
	}
}

