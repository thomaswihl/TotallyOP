package at.witho.totally_op.net;

import at.witho.totally_op.TotallyOP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(TotallyOP.MODID);

    public static void init() {
        int id = 0;
        INSTANCE.registerMessage(RoughToolChange.RoughToolChangeHandler.class, RoughToolChange.class, id++, Side.SERVER);
        INSTANCE.registerMessage(WhitelistChange.WhitelistChangeHandler.class, WhitelistChange.class, id++, Side.SERVER);
    }
}
