package at.witho.totally_op.items;

import at.witho.totally_op.TotallyOP;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Alphorn extends Item {
	protected int rangeH = 16;
	protected int rangeV = 4;
	public Alphorn() {
		setRegistryName("alphorn");
		setUnlocalizedName(TotallyOP.MODID + "." + getRegistryName());
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 200;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		return ActionResult.newResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (player.world.isRemote) return;
		// TODO: Make it work the other way round
		World world = player.world;
		EnumHand activeHand = player.getActiveHand();
		Block blockHeld = Block.getBlockFromItem((activeHand == EnumHand.MAIN_HAND) ?
				player.getHeldItemOffhand().getItem() :
				player.getHeldItemMainhand().getItem());
		BlockPos playerPos = player.getPosition();
		double x = playerPos.getX();
		double y = playerPos.getY() + 0.5f;
		double z = playerPos.getZ();
		for(BlockPos pos : BlockPos.getAllInBox(playerPos.add(-rangeH, -rangeV, -rangeH), playerPos.add(rangeH, rangeV, rangeH))) {
			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof BlockBush) {
				boolean harvest = true;
				if (blockHeld != Blocks.AIR) {
					harvest = blockHeld == block;
				}
				if (harvest) {
					NonNullList<ItemStack> drops = NonNullList.create();
					block.getDrops(drops, world, pos, world.getBlockState(pos), 0);
					world.setBlockToAir(pos);
					for(ItemStack s : drops) {
						world.spawnEntity(new EntityItem(world, x, y, z, s));
					}
				}
			}
		}
	}
}
