package at.witho.totally_op;

import org.apache.logging.log4j.Logger;

import at.witho.totally_op.proxy.CommonProxy;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.util.EnumHelper;

@Mod(modid = TotallyOP.MODID, version = TotallyOP.VERSION)
public class TotallyOP
{
    public static final String MODID = "totally_op";
    public static final String VERSION = "1.0";
    
    public static final ToolMaterial peacefulWoodMaterial = EnumHelper.addToolMaterial("PEACEFUL_WOOD", 2, 200, 10F, -40F, 10);
    public static final ToolMaterial peacefulIronMaterial = EnumHelper.addToolMaterial("PEACEFUL_IRON", 3, 5000, 100F, -40F, 30);
    
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
    }
}
