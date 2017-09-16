package at.witho.totally_op.items;

import at.witho.totally_op.TotallyOP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Wings extends ItemArmor {
	public Wings() {
        super(TotallyOP.armorMaterial, 0, EntityEquipmentSlot.CHEST);
		setRegistryName("wings");
		setUnlocalizedName(TotallyOP.MODID + "." + getRegistryName());
        MinecraftForge.EVENT_BUS.register(this);
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
        return String.format("%s:textures/models/armor/armor_layer_1.png", TotallyOP.MODID, TotallyOP.MODID);
    }

    public static ItemStack getWingsFromPlayer(EntityPlayer player) {
        for(int i = 0; i < player.inventory.armorInventory.size(); i++) {
            if(player.inventory.armorInventory.get(i).getItem() instanceof Wings){
                return player.inventory.armorInventory.get(i);
            }
        }
        return ItemStack.EMPTY;
    }

    @SubscribeEvent
    public void livingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if(event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            //if (player.world.isRemote) return;
            ItemStack wings = getWingsFromPlayer(player);
            player.capabilities.allowFlying = !wings.isEmpty();
        }
    }

}
