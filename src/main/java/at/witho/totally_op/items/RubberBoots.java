package at.witho.totally_op.items;

import at.witho.totally_op.TotallyOP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RubberBoots extends ItemArmor {
	public RubberBoots() {
	    super(TotallyOP.armorMaterial, 0, EntityEquipmentSlot.FEET);
		setRegistryName("rubber_boots");
		setUnlocalizedName(TotallyOP.MODID + "." + getRegistryName());
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        ItemStack stack = event.crafting;
        if (stack.getItem() instanceof RubberBoots) {
            NBTTagList ench = stack.getEnchantmentTagList();
            if (ench.tagCount() == 0) {
                stack.addEnchantment(Enchantment.getEnchantmentByID(2), 4); // feather falling IV
                stack.addEnchantment(Enchantment.getEnchantmentByID(8), 3); // depth strider III
            } else {
                stack.setTagInfo("ench", new NBTTagList());
            }
        }
    }

}
