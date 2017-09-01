package at.witho.totally_op.proxy;

import at.witho.totally_op.ModBlocks;
import at.witho.totally_op.ModEntities;
import at.witho.totally_op.ModItems;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		ModBlocks.initModels();
		ModItems.initModels();
        ModEntities.initModels();
	}

}
