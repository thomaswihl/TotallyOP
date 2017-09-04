package at.witho.totally_op.proxy;

import java.io.File;

import at.witho.totally_op.MobInteraction;
import at.witho.totally_op.ModBlocks;
import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.WorldGen;
import at.witho.totally_op.blocks.*;
import at.witho.totally_op.blocks.tileentity.TileFarmingFlower;
import at.witho.totally_op.blocks.tileentity.TileSuckingFlower;
import at.witho.totally_op.config.Config;
import at.witho.totally_op.entity.Car;
import at.witho.totally_op.items.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
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
        event.getRegistry().register(new PeacefulDoubleFlower());
    	event.getRegistry().register(new FarmingFlower());
        event.getRegistry().register(new SuckingFlower());
        event.getRegistry().register(new Fortune(0));
        event.getRegistry().register(new Fortune(1));
        event.getRegistry().register(new Fortune(2));
        event.getRegistry().register(new Fortune(3));
        event.getRegistry().register(new Fortune(4));
        event.getRegistry().register(new Fortune(5));
        event.getRegistry().register(new Fortune(6));
        event.getRegistry().register(new Efficiency(0));
        event.getRegistry().register(new Efficiency(1));
        event.getRegistry().register(new Efficiency(2));
        event.getRegistry().register(new Efficiency(3));
        event.getRegistry().register(new Efficiency(4));
        event.getRegistry().register(new Efficiency(5));
        event.getRegistry().register(new Efficiency(6));
    	GameRegistry.registerTileEntity(TileFarmingFlower.class, TotallyOP.MODID + "_farming_flower");
        GameRegistry.registerTileEntity(TileSuckingFlower.class, TotallyOP.MODID + "_sucking_flower");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	event.getRegistry().register(new ItemBlock(ModBlocks.peaceful_flower).setRegistryName(ModBlocks.peaceful_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.peaceful_double_flower).setRegistryName(ModBlocks.peaceful_double_flower.getRegistryName()));
    	event.getRegistry().register(new ItemBlock(ModBlocks.farming_flower).setRegistryName(ModBlocks.farming_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.sucking_flower).setRegistryName(ModBlocks.sucking_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier0).setRegistryName(ModBlocks.fortune_tier0.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier1).setRegistryName(ModBlocks.fortune_tier1.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier2).setRegistryName(ModBlocks.fortune_tier2.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier3).setRegistryName(ModBlocks.fortune_tier3.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier4).setRegistryName(ModBlocks.fortune_tier4.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier5).setRegistryName(ModBlocks.fortune_tier5.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier6).setRegistryName(ModBlocks.fortune_tier6.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.efficiency_tier0).setRegistryName(ModBlocks.efficiency_tier0.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.efficiency_tier1).setRegistryName(ModBlocks.efficiency_tier1.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.efficiency_tier2).setRegistryName(ModBlocks.efficiency_tier2.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.efficiency_tier3).setRegistryName(ModBlocks.efficiency_tier3.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.efficiency_tier4).setRegistryName(ModBlocks.efficiency_tier4.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.efficiency_tier5).setRegistryName(ModBlocks.efficiency_tier5.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.efficiency_tier6).setRegistryName(ModBlocks.efficiency_tier6.getRegistryName()));
    	event.getRegistry().register(new PeacefulWoodTool());
    	event.getRegistry().register(new PeacefulIronTool());
    	event.getRegistry().register(new PeacefulDiamondTool());
    	event.getRegistry().register(new Alphorn());
        event.getRegistry().register(new Coin());
        event.getRegistry().register(new Euro());
        event.getRegistry().register(new RubberBoots());
        event.getRegistry().register(new ScubaHelmet());
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        int id = 1;
        //EntityRegistry.registerModEntity(new ResourceLocation(TotallyOP.MODID, "car"), Car.class, "car", id++, TotallyOP.instance, 64, 3, true, 0x996600, 0x00ff00);
    }

}
