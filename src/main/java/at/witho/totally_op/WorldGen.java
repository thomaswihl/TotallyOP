package at.witho.totally_op;

import at.witho.totally_op.config.Config;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldGen {
	private static float flowerGen = 0.0f;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onWorldDecoration(DecorateBiomeEvent.Decorate event) {
		if (event.getType() != EventType.FLOWERS) return;
		if (event.getResult() != Result.ALLOW && event.getResult() != Result.DEFAULT) return;
		flowerGen += Config.flowersPerChunk;
		int tries = (int)(flowerGen * 2 + 0.9f);
		while (flowerGen >= 1 && tries > 0) {
			/* see http://www.minecraftforge.net/forum/topic/60721-1121-how-would-i-prevent-cascading-worldgen-lag-during-worldgen/
			 * if you are wondering about the +8
			 */
			int x = event.getPos().getX() + event.getRand().nextInt(16) + 8;
			int y = event.getPos().getY();
			int z = event.getPos().getZ() + event.getRand().nextInt(16) + 8;
			BlockPos pos = event.getWorld().getTopSolidOrLiquidBlock(new BlockPos(x, y, z));

			if(event.getWorld().isAirBlock(pos)) {
				event.getWorld().setBlockState(pos, ModBlocks.peaceful_flower.getDefaultState());
				flowerGen -= 1;
			}
			--tries;
		}
	}
	
}
