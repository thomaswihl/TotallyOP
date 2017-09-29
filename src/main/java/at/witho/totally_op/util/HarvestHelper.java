package at.witho.totally_op.util;

import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public class HarvestHelper {
    @SubscribeEvent
    static void onHarvest(BlockEvent.HarvestDropsEvent event) {
        IBlockState state = event.getState();
        boolean drop = false;
        if (state.getBlock() instanceof BlockBush) drop = true;
        if (!drop && state.getBlock() != Blocks.AIR) {
            int[] ids = OreDictionary.getOreIDs(new ItemStack(state.getBlock()));
            for (int id : ids) {
                String name = OreDictionary.getOreName(id);
                if (name == "logWood") {
                    drop = true;
                    break;
                }
            }
        }
        if (drop)
            event.getWorld().spawnEntity(new EntityXPOrb(event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), 1));
    }
}
