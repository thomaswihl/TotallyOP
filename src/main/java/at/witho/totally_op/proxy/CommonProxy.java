package at.witho.totally_op.proxy;

import java.io.File;

import at.witho.totally_op.MobInteraction;
import at.witho.totally_op.ModBlocks;
import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.WorldGen;
import at.witho.totally_op.blocks.*;
import at.witho.totally_op.blocks.tileentity.*;
import at.witho.totally_op.config.Config;
import at.witho.totally_op.entity.Car;
import at.witho.totally_op.items.*;
import at.witho.totally_op.net.PacketHandler;
import at.witho.totally_op.net.RoughToolChange;
import at.witho.totally_op.util.HarvestHelper;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber
public class CommonProxy {
    public static Configuration config;

    public void preInit(FMLPreInitializationEvent e) {
        File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "totally_op.cfg"));
        Config.readConfig();
        PacketHandler.init();
    }

    public void init(FMLInitializationEvent e) {
    	MinecraftForge.TERRAIN_GEN_BUS.register(WorldGen.class);
		MinecraftForge.EVENT_BUS.register(MobInteraction.class);
		if (Config.xpForHarvesting > 0) MinecraftForge.EVENT_BUS.register(HarvestHelper.class);
        NetworkRegistry.INSTANCE.registerGuiHandler(TotallyOP.instance, new GuiProxy());
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
        event.getRegistry().register(new FluidSuckingFlower());
        event.getRegistry().register(new PlacingFlower());
        event.getRegistry().register(new BreakingFlower());
        event.getRegistry().register(new CompressingFlower());
        event.getRegistry().register(new FishingFlower());
        event.getRegistry().register(new SplittingFlower());
        event.getRegistry().register(new CuttingFlower());
        event.getRegistry().register(new TierableBlock(TierableBlock.FORTUNE));
        event.getRegistry().register(new TierableBlock(TierableBlock.EFFICIENCY));
        event.getRegistry().register(new TierableBlock(TierableBlock.RANGE));
        event.getRegistry().register(new SunPowder());
    	GameRegistry.registerTileEntity(TileFarmingFlower.class, TotallyOP.MODID + "_farming_flower");
        GameRegistry.registerTileEntity(TileSuckingFlower.class, TotallyOP.MODID + "_sucking_flower");
        GameRegistry.registerTileEntity(TileFluidSuckingFlower.class, TotallyOP.MODID + "_fluid_sucking_flower");
        GameRegistry.registerTileEntity(TilePlacingFlower.class, TotallyOP.MODID + "_placing_flower");
        GameRegistry.registerTileEntity(TileBreakingFlower.class, TotallyOP.MODID + "_breaking_flower");
        GameRegistry.registerTileEntity(TileCompressingFlower.class, TotallyOP.MODID + "_compressing_flower");
        GameRegistry.registerTileEntity(TileSplittingFlower.class, TotallyOP.MODID + "_splitting_flower");
        GameRegistry.registerTileEntity(TileCuttingFlower.class, TotallyOP.MODID + "_cutting_flower");
        GameRegistry.registerTileEntity(TileFishingFlower.class, TotallyOP.MODID + "_fishing_flower");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
    	event.getRegistry().register(new ItemBlock(ModBlocks.peaceful_flower).setRegistryName(ModBlocks.peaceful_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.peaceful_double_flower).setRegistryName(ModBlocks.peaceful_double_flower.getRegistryName()));
    	event.getRegistry().register(new ItemBlock(ModBlocks.farming_flower).setRegistryName(ModBlocks.farming_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.sucking_flower).setRegistryName(ModBlocks.sucking_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fluid_sucking_flower).setRegistryName(ModBlocks.fluid_sucking_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fishing_flower).setRegistryName(ModBlocks.fishing_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.placing_flower).setRegistryName(ModBlocks.placing_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.breaking_flower).setRegistryName(ModBlocks.breaking_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.compressing_flower).setRegistryName(ModBlocks.compressing_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.splitting_flower).setRegistryName(ModBlocks.splitting_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.cutting_flower).setRegistryName(ModBlocks.cutting_flower.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.fortune).setRegistryName(ModBlocks.fortune.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.efficiency).setRegistryName(ModBlocks.efficiency.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.range).setRegistryName(ModBlocks.range.getRegistryName()));
        event.getRegistry().register(new ItemBlock(ModBlocks.sun_powder).setRegistryName(ModBlocks.sun_powder.getRegistryName()));
    	event.getRegistry().register(new PeacefulTool(TotallyOP.peacefulWoodMaterial, "peaceful_wood_tool", 2, 1));
    	event.getRegistry().register(new PeacefulTool(TotallyOP.peacefulIronMaterial, "peaceful_iron_tool", 4, 2));
    	event.getRegistry().register(new PeacefulTool(TotallyOP.peacefulDiamondMaterial, "peaceful_diamond_tool", 8, 3));
        event.getRegistry().register(new RoughTool());
    	event.getRegistry().register(new Alphorn());
        event.getRegistry().register(new Coin());
        event.getRegistry().register(new Euro());
        event.getRegistry().register(new RubberBoots());
        event.getRegistry().register(new ScubaHelmet());
        event.getRegistry().register(new SprintLeggings());
        event.getRegistry().register(new Rucksack());
        event.getRegistry().register(new Wings());
        event.getRegistry().register(new DiamondFragment());
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        int id = 1;
        //EntityRegistry.registerModEntity(new ResourceLocation(TotallyOP.MODID, "car"), Car.class, "car", id++, TotallyOP.instance, 64, 3, true, 0x996600, 0x00ff00);
    }

}
