package at.witho.totally_op.items;

import at.witho.totally_op.TotallyOP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

public class Rucksack extends Item {
    public static final int GUI_ID = 1;
    public static final int INV_SIZE = 9 * 9;
    private ItemStack[] inventory = new ItemStack[INV_SIZE];

    public Rucksack() {
		setRegistryName("rucksack");
		setUnlocalizedName(TotallyOP.MODID + "." + getRegistryName());
		setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!playerIn.isSneaking()) {
            if (!worldIn.isRemote) {
                playerIn.openGui(TotallyOP.instance, GUI_ID, worldIn, 0, 0, 0);
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

}
