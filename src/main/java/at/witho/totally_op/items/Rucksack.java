package at.witho.totally_op.items;

import at.witho.totally_op.TotallyOP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Rucksack extends Item {
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

}
