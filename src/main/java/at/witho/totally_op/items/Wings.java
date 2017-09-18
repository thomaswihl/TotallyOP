package at.witho.totally_op.items;

import at.witho.totally_op.ModItems;
import at.witho.totally_op.TotallyOP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Wings extends ItemArmor {
    private int counter = 0;

	public Wings() {
        super(TotallyOP.armorMaterial, 0, EntityEquipmentSlot.CHEST);
        setMaxStackSize(1);
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
        ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (stack.getItem() == ModItems.wings) return stack;
        return ItemStack.EMPTY;
    }

    @SubscribeEvent
    public void livingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
	    ++counter;
	    if (counter < 20) return;
	    counter = 0;
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            if (player.capabilities.isCreativeMode) return;
            ItemStack wings = getWingsFromPlayer(player);
            player.capabilities.allowFlying = !wings.isEmpty();
//            if (player.isAirBorne && !player.capabilities.isFlying) {
//                Entity entity = (Entity)player;
//                Class clazz = entity.getClass();
//                try {
//                    EntityPlayerSP
//                    Method setFlag = clazz.getDeclaredMethod("setFlag", int.class, boolean.class);
//                    setFlag.setAccessible(true);
//                    setFlag.invoke(clazz, 7, true);
//                    TotallyOP.logger.log(Level.ERROR, "SUCCESS");
//                } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException x) { TotallyOP.logger.log(Level.ERROR, x); }
//            }
        }
    }
}
