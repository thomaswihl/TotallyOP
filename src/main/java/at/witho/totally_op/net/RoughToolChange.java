package at.witho.totally_op.net;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.items.RoughTool;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.Level;

public class RoughToolChange implements IMessage {

    private int dimension;

    public RoughToolChange() { }
    public RoughToolChange(int dimension) { this.dimension = dimension; }

    @Override
    public void fromBytes(ByteBuf buf) {
        dimension = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimension);
    }

    public static class RoughToolChangeHandler implements IMessageHandler<RoughToolChange, IMessage> {
        @Override
        public IMessage onMessage(RoughToolChange message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            int amount = message.dimension;
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                ItemStack stack = serverPlayer.getHeldItemMainhand();
                if (!stack.isEmpty() && stack.getItem() instanceof RoughTool) {
                    RoughTool tool = (RoughTool)stack.getItem();
                    tool.changeDimension(stack, serverPlayer, amount);
                }
            });
            return null;
        }
    }

}
