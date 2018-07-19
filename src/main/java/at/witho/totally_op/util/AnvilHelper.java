package at.witho.totally_op.util;

import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AnvilHelper {
    @SubscribeEvent
    static void onAnvilRepairEvent(AnvilRepairEvent ev) {
        ev.setBreakChance(0);
    }
}
