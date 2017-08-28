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
import at.witho.totally_op.items.*;
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
        MinecraftForge.EVENT_BUS.register(RubberBoots.class);
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
        event.getRegistry().register(new SuckingFlower());
        event.getRegistry().register(new FortuneTier1());
        event.getRegistry().register(new FortuneTier2());
        event.getRegistry().register(new FortuneTier3());
        event.getRegistry().register(new FortuneTier4());
        event.getRegistry().register(new FortuneTier5());
        event.getRegistry().register(new FortuneTier6());
        event.getRegistry().register(new EfficiencyTier1());
        event.getRegistry().register(new EfficiencyTier2());
        event.getRegistry().register(new EfficiencyTier3());
        event.getRegistry().register(new EfficiencyTier4());
        event.getRegistry().register(new EfficiencyTier5());
        event.getRegistry().register(new EfficiencyTier6());
    	GameRegistry.registerTileEntity(TileFarmingFlower.class, TotallyOP.MODID + "_farming_flower");
        GameRegistry.registerTileEntity(TileSuckingFlower.class, TotallyOP.MODID + "_sucking_flower");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	event.getRegistry().register(new ItemBlock(ModBlocks.peaceful_flower).setRegistryName(ModBlocks.peaceful_flower.getRegistryName()));
    	event.getRegistry().register(new ItemBlock(ModBlocks.farming_flower).setRegistryName(ModBlocks.farming_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.sucking_flower).setRegistryName(ModBlocks.sucking_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier1).setRegistryName(ModBlocks.fortune_tier1.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier2).setRegistryName(ModBlocks.fortune_tier2.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier3).setRegistryName(ModBlocks.fortune_tier3.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier4).setRegistryName(ModBlocks.fortune_tier4.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier5).setRegistryName(ModBlocks.fortune_tier5.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune_tier6).setRegistryName(ModBlocks.fortune_tier6.getRegistryName()));
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
    }

}
