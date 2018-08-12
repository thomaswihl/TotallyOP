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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

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

//    @SubscribeEvent
//    @SideOnly(Side.CLIENT)
//    public void livingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
//        if (event.getEntityLiving() instanceof EntityPlayer) {
//            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
//            ItemStack leggings = getLeggingsFromPlayer(player);
//            if (!leggings.isEmpty()) {
//                NBTTagCompound nbt = leggings.getTagCompound();
//                if (nbt == null) {
//                    nbt = new NBTTagCompound();
//                }
//                counter++;
//                if (counter == 20) {
//                    TotallyOP.logger.log(Level.ERROR, nbt.toString());
//                    counter = 0;
//                }
//                NBTTagList attr = nbt.getTagList("AttributeModifiers", 10);
//                if (attr == null) {
//                    attr = new NBTTagList();
//                }
//                NBTTagCompound comp = null;
//                int index = 0;
//                for (; index < attr.tagCount(); ++index) {
//                    NBTTagCompound c = (NBTTagCompound)attr.get(index);
//                    if (c != null) {
//                        if (c.getString("Name") == "generic.movementSpeed") {
//                            comp = c;
//                            break;
//                        }
//                    }
//                }
//                if (comp == null) {
//                    comp = new NBTTagCompound();
//                    comp.setString("AttributeName", "generic.movementSpeed");
//                    comp.setString("Name", "generic.movementSpeed");
//                    comp.setString("Slot", "legs");
//                    comp.setInteger("Operation", 0);
//                    comp.setInteger("UUIDLeast", 384071);
//                    comp.setInteger("UUIDMost", 23359);
//                    index = attr.tagCount();
//                    attr.appendTag(comp);
//                }
//                comp.setFloat("Amount", 0.25f);
//                attr.set(index, comp);
//                nbt.setTag("AttributeModifiers", attr);
//                leggings.setTagCompound(nbt);
////                if (player.isSprinting()) player.capabilities.setPlayerWalkSpeed(0.4f);
////                else player.capabilities.setPlayerWalkSpeed(0.25f);
//            }
//        }
//    }


}
