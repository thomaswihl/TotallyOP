package at.witho.totally_op.items;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.storage.RucksackStorage;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.util.Random;

public class Rucksack extends Item {
    public static final int GUI_ID = 1;
    public static final int INV_SIZE = 9 * 9;
    private ItemStack[] inventory = new ItemStack[INV_SIZE];

    public Rucksack() {
        String name = "rucksack";
        setRegistryName(name);
        setUnlocalizedName(TotallyOP.MODID + "." + name);
        setMaxStackSize(1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            if (!playerIn.isSneaking()) {
                playerIn.openGui(TotallyOP.instance, GUI_ID, worldIn, 0, 0, 0);
            } else {
                NBTTagList ench = stack.getEnchantmentTagList();
                if (ench.hasNoTags()) {
                    stack.setTagInfo("ench", new NBTTagList());
                    stack.addEnchantment(Enchantment.getEnchantmentByID(33), 1);
                    playerIn.sendMessage(new TextComponentString("Your rucksack will suck up items."));
                } else {
                    stack.setTagInfo("ench", new NBTTagList());
                    playerIn.sendMessage(new TextComponentString("Your rucksack won't suck up items."));
                }
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if (event.isCanceled()) return;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack rucksack = getActiveRucksack(event.getEntityPlayer());
        if (!rucksack.isEmpty()) {
            RucksackStorage storage = new RucksackStorage(rucksack);
            ItemStack insert = event.getItem().getItem();
            ItemStack remain = storage.addItem(insert);
            if (remain.getCount() != insert.getCount()) {
                insert.setCount(remain.getCount());
                World world = player.world;
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                if (remain.isEmpty() && event.isCancelable()) event.setCanceled(true);
            }
        }
    }

    public ItemStack getActiveRucksack(EntityPlayer player) {
        InventoryPlayer inventory = player.inventory;
        for (ItemStack item : inventory.mainInventory) {
            if (isActiveRucksack(item)) return item;
        }
        for (ItemStack item : inventory.offHandInventory) {
            if (isActiveRucksack(item)) return item;
        }
        return ItemStack.EMPTY;
    }

    public static boolean isActiveRucksack(ItemStack item) {
        return !item.isEmpty() && (item.getItem() instanceof Rucksack) && !item.getEnchantmentTagList().hasNoTags();
    }

}
