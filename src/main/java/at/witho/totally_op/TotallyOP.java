package at.witho.totally_op;

import at.witho.totally_op.util.CraftingUtils;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLogic;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import at.witho.totally_op.proxy.CommonProxy;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.util.EnumHelper;

import java.util.UUID;

@Mod(modid = TotallyOP.MODID, version = TotallyOP.VERSION)
public class TotallyOP
{
    public static final String MODID = "totally_op";
    public static final String VERSION = "1.0";
    
    public static final ToolMaterial peacefulWoodMaterial = EnumHelper.addToolMaterial("PEACEFUL_WOOD", 2, 400, 20F, -40F, 10);
    public static final ToolMaterial peacefulIronMaterial = EnumHelper.addToolMaterial("PEACEFUL_IRON", 3, 8000, 100F, -40F, 30);
    public static final ToolMaterial peacefulDiamondMaterial = EnumHelper.addToolMaterial("PEACEFUL_IRON", 3, -1, 1000F, -40F, 30);
    public static final ItemArmor.ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial("PEACEFUL_RUBBER", "rubber_boots", -1, new int[]{3, 6, 8, 3}, 40, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 2.0f);
    public static final Material flowerMaterial = new Material(MapColor.FOLIAGE);

    @SidedProxy(clientSide = "at.witho.totally_op.proxy.ClientProxy", serverSide = "at.witho.totally_op.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static TotallyOP instance;

    public static Logger logger;
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
        CraftingUtils.init();
    }
}
