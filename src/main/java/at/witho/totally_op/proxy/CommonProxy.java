package at.witho.totally_op.proxy;

import java.io.File;

import at.witho.totally_op.MobInteraction;
import at.witho.totally_op.ModBlocks;
import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.WorldGen;
import at.witho.totally_op.blocks.FarmingFlower;
import at.witho.totally_op.blocks.PeacefulFlower;
import at.witho.totally_op.blocks.tileentity.TileFarmingFlower;
import at.witho.totally_op.config.Config;
import at.witho.totally_op.items.Alphorn;
import at.witho.totally_op.items.PeacefulDiamondTool;
import at.witho.totally_op.items.PeacefulIronTool;
import at.witho.totally_op.items.PeacefulWoodTool;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {
    public static Configuration config;

    public void preInit(FMLPreInitializationEvent e) {
        File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "totally_op.cfg"));
        Config.readConfig();
    }

    public void init(FMLInitializationEvent e) {
    	MinecraftForge.TERRAIN_GEN_BUS.register(WorldGen.class);
		MinecraftForge.EVENT_BUS.register(MobInteraction.class);
    }

    public void postInit(FMLPostInitializationEvent e) {
        if (config.hasChanged()) {
            config.save();
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
    	event.getRegistry().register(new PeacefulFlower());
    	event.getRegistry().register(new FarmingFlower());
    	GameRegistry.registerTileEntity(TileFarmingFlower.class, TotallyOP.MODID + "_farming_flower");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	event.getRegistry().register(new ItemBlock(ModBlocks.peacefulFlower).setRegistryName(ModBlocks.peacefulFlower.getRegistryName()));
    	event.getRegistry().register(new ItemBlock(ModBlocks.farmingFlower).setRegistryName(ModBlocks.farmingFlower.getRegistryName()));
    	event.getRegistry().register(new PeacefulWoodTool());
    	event.getRegistry().register(new PeacefulIronTool());
    	event.getRegistry().register(new PeacefulDiamondTool());
    	event.getRegistry().register(new Alphorn());
    }

}
