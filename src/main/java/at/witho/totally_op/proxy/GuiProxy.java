package at.witho.totally_op.proxy;

import at.witho.totally_op.gui.RucksackGui;
import at.witho.totally_op.items.Rucksack;
import at.witho.totally_op.storage.RucksackStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiProxy implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Rucksack.GUI_ID) {
            return new ContainerChest(player.inventory, new RucksackStorage(player.getHeldItemMainhand()), player);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Rucksack.GUI_ID) {
            return new RucksackGui(new ContainerChest(player.inventory, new RucksackStorage(player.getHeldItemMainhand()), player));
        }
        return null;
    }
}