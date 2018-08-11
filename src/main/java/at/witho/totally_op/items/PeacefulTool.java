package at.witho.totally_op.items;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.util.HightlightBlock;
import at.witho.totally_op.util.VeinMiner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

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
        ModelResourceLocation silkModel = new ModelResourceLocation(getRegistryName() + "_silk", "inventory");
        ModelResourceLocation fortuneModel = new ModelResourceLocation(getRegistryName() + "_fortune", "inventory");
        ModelBakery.registerItemVariants(this, silkModel, fortuneModel);
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                if (isSilk(stack)) {
                    return silkModel;
                } else {
                    return fortuneModel;
                }
            }
        });
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
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) { return false; }

	boolean isSilk(ItemStack stack) {
        NBTTagList ench = stack.getEnchantmentTagList();
        NBTTagCompound nbt = ench.getCompoundTagAt(0);
        return nbt.getShort("id") == 33;
    }

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            if (!worldIn.isRemote) {
                if (isSilk(stack)) {
                    stack.setTagInfo("ench", new NBTTagList());
                    stack.addEnchantment(Enchantment.getEnchantmentByID(35), fortune);
                    playerIn.sendMessage(new TextComponentString("Your tool has now fortune " + StringUtils.repeat('I', fortune) + "."));
                } else {
                    stack.setTagInfo("ench", new NBTTagList());
                    stack.addEnchantment(Enchantment.getEnchantmentByID(33), 1);
                    playerIn.sendMessage(new TextComponentString("Your tool has now silk touch."));
                }
                magnetActive = MAGNET_ACTIVE_TIME;
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState blockState)
	{
		return blockState.getBlock() != Blocks.BEDROCK ? efficiency : 1.0F;
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
        if (player.rotationPitch < -85 || player.rotationPitch > 85) veinMiner.setHorizontalPlane(true);
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
				if (veinMiner != null) veinMiner.addSurroundings(pos);
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

/*    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void renderOutline(RenderWorldLastEvent ev) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemStack itemstack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (!(itemstack.getItem() instanceof PeacefulTool)) {
            itemstack = player.getHeldItem(EnumHand.OFF_HAND);
            if (!(itemstack.getItem() instanceof PeacefulTool)) return;
        }
        BlockPos pos = HightlightBlock.getBlockPos(player);
        if (pos == null) return;

        HightlightBlock.begin();

        boolean fortune = false;
        NBTTagList ench = itemstack.getEnchantmentTagList();
        NBTTagCompound nbt = ench.getCompoundTagAt(0);
        if (nbt.getShort("id") != 33) fortune = true;

        switch (HightlightBlock.sideHit) {
            case DOWN:
                HightlightBlock.mY = true;
                break;
            case UP:
                HightlightBlock.pY = true;
                break;
            case NORTH:
                HightlightBlock.mZ = true;
                break;
            case SOUTH:
                HightlightBlock.pZ = true;
                break;
            case WEST:
                HightlightBlock.mX = true;
                break;
            case EAST:
                HightlightBlock.pX = true;
                break;
        }
        HightlightBlock.color = new Color(128, fortune ? 128 : 255, fortune ? 255 : 128, 32);
        HightlightBlock.outlineBlock(pos, ev.getPartialTicks());

        HightlightBlock.end();
    }*/
}

