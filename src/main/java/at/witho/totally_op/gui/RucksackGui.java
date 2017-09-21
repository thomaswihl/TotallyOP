package at.witho.totally_op.gui;

import at.witho.totally_op.TotallyOP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RucksackGui extends GuiContainer {
    private static final ResourceLocation rucksackInventory = new ResourceLocation(TotallyOP.MODID, "textures/gui/rucksack.png");
    private static final ResourceLocation playerInventory = new ResourceLocation(TotallyOP.MODID, "textures/gui/player_inventory.png");
    public static final int filterX = 8;
    public static final int filterY = 24;
    public static final int firstItemY = 48;
    public static final int firstItemX = filterX;
    public static final int compressX = 232;
    public static final int compressY = firstItemY;
    public static final int slotsX = 12;
    public static final int slotsY = 6;
    public static final int playerFirstItemX = 40 + 8;
    public static final int playerFirstItemY = 162 + 8;
    public static final int hotbarFirstItemY = 162 + 66;


    public RucksackGui(Container inventorySlotsIn) {
        super(inventorySlotsIn);
        xSize = 256;
        ySize = 250;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(rucksackInventory);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 256, 162);
        mc.getTextureManager().bindTexture(playerInventory);
        drawTexturedModalRect(guiLeft + 40, guiTop + 162, 0, 0, 176, 90);
    }
}
