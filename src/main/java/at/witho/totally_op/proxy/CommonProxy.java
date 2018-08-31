package at.witho.totally_op.proxy;

import java.io.File;

import at.witho.totally_op.*;
import at.witho.totally_op.blocks.*;
import at.witho.totally_op.blocks.tileentity.*;
import at.witho.totally_op.config.Config;
import at.witho.totally_op.entity.Car;
import at.witho.totally_op.items.*;
import at.witho.totally_op.net.PacketHandler;
import at.witho.totally_op.net.RoughToolChange;
import at.witho.totally_op.util.AnvilHelper;
import at.witho.totally_op.util.HarvestHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

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
        MinecraftForge.EVENT_BUS.register(AnvilHelper.class);
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
        event.getRegistry().register(new TierableBlock(TierableBlock.Type.Fortune));
        event.getRegistry().register(new TierableBlock(TierableBlock.Type.Efficency));
        event.getRegistry().register(new TierableBlock(TierableBlock.Type.Range));
        event.getRegistry().register(new SunPowder());
        event.getRegistry().register(new ThinAsAir());
        event.getRegistry().register(new WitherMesh());
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
    	event.getRegistry().register(new ItemBlock(ModBlocks.peaceful_flower).setRegistryName(ModBlocks.peaceful_flower.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.peaceful_double_flower).setRegistryName(ModBlocks.peaceful_double_flower.NAME));
    	event.getRegistry().register(new ItemBlock(ModBlocks.farming_flower).setRegistryName(ModBlocks.farming_flower.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.sucking_flower).setRegistryName(ModBlocks.sucking_flower.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.fluid_sucking_flower).setRegistryName(ModBlocks.fluid_sucking_flower.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.fishing_flower).setRegistryName(ModBlocks.fishing_flower.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.placing_flower).setRegistryName(ModBlocks.placing_flower.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.breaking_flower).setRegistryName(ModBlocks.breaking_flower.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.compressing_flower).setRegistryName(ModBlocks.compressing_flower.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.splitting_flower).setRegistryName(ModBlocks.splitting_flower.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.cutting_flower).setRegistryName(ModBlocks.cutting_flower.NAME));
        event.getRegistry().register(new TierableItem(ModBlocks.fortune).setRegistryName(ModBlocks.fortune.NAME));
        event.getRegistry().register(new TierableItem(ModBlocks.efficiency).setRegistryName(ModBlocks.efficiency.NAME));
        event.getRegistry().register(new TierableItem(ModBlocks.range).setRegistryName(ModBlocks.range.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.sun_powder).setRegistryName(ModBlocks.sun_powder.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.thin_as_air).setRegistryName(ModBlocks.thin_as_air.NAME));
        event.getRegistry().register(new ItemBlock(ModBlocks.wither_mesh).setRegistryName(ModBlocks.wither_mesh.NAME));
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
        String[] dyes =
                {
                        "Black",
                        "Red",
                        "Green",
                        "Brown",
                        "Blue",
                        "Purple",
                        "Cyan",
                        "LightGray",
                        "Gray",
                        "Pink",
                        "Lime",
                        "Yellow",
                        "LightBlue",
                        "Magenta",
                        "Orange",
                        "White"
                };

        ItemStack dye = new ItemStack(ModBlocks.peaceful_flower);
        for(int i = 0; i < 16; i++)
        {
            OreDictionary.registerOre("dye" + dyes[i], dye);
        }
    }

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        int id = 1;
        //EntityRegistry.registerModEntity(new ResourceLocation(TotallyOP.MODID, "car"), Car.class, "car", id++, TotallyOP.instance, 64, 3, true, 0x996600, 0x00ff00);
    }

}
