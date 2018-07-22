package at.witho.totally_op.items;

import at.witho.totally_op.ModItems;
import at.witho.totally_op.TotallyOP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SprintLeggings extends ItemArmor {
    private int counter = 0;

	public SprintLeggings() {
	    super(TotallyOP.armorMaterial, 0, EntityEquipmentSlot.LEGS);
        String name = "sprint_leggings";
        setRegistryName(name);
        setUnlocalizedName(TotallyOP.MODID + "." + name);
        MinecraftForge.EVENT_BUS.register(this);
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
        return String.format("%s:textures/models/armor/armor_layer_2.png", TotallyOP.MODID);
    }

    public static ItemStack getLeggingsFromPlayer(EntityPlayer player) {
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        if (stack.getItem() == ModItems.sprint_leggings) return stack;
        return ItemStack.EMPTY;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void livingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        ++counter;
        if (counter < 20) return;
        counter = 0;
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            ItemStack leggings = getLeggingsFromPlayer(player);
            float speed = leggings.isEmpty() ? 0.1f : 0.3f;
            player.capabilities.setPlayerWalkSpeed(speed);
        }
    }


}
