package at.witho.totally_op.net;

import at.witho.totally_op.items.RoughTool;
import at.witho.totally_op.storage.RucksackContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WhitelistChange implements IMessage {

    public enum Which { Trash, Compress }
    private Which which;
    private boolean whitelist;

    public WhitelistChange() { }
    public WhitelistChange(Which which, boolean whitelist) { this.which = which; this.whitelist = whitelist; }

    @Override
    public void fromBytes(ByteBuf buf) {
        int v = buf.readInt();
        which = ((v & 1) == 0) ? Which.Trash : Which.Compress;
        whitelist = (v & 2) != 0;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        int v = ((which == Which.Trash) ? 0 : 1) | (whitelist ? 2 : 0);
        buf.writeInt(v);
    }

    public static class WhitelistChangeHandler implements IMessageHandler<WhitelistChange, IMessage> {
        @Override
        public IMessage onMessage(WhitelistChange message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                Container container = serverPlayer.openContainer;
                if (container instanceof RucksackContainer) {
                    RucksackContainer rucksack = (RucksackContainer)container;
                    if (message.which == Which.Trash) rucksack.rucksack.whitelistTrash = message.whitelist;
                    else if (message.which == Which.Compress) rucksack.rucksack.whitelistCompress = message.whitelist;
                    rucksack.rucksack.markDirty();
                }
            });
            return null;
        }
    }

}
