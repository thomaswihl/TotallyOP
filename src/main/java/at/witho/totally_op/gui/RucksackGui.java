package at.witho.totally_op.gui;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.storage.RucksackContainer;
import at.witho.totally_op.storage.RucksackStorage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class RucksackGui extends GuiContainer {
    private static final ResourceLocation rucksackInventory = new ResourceLocation(TotallyOP.MODID, "textures/gui/rucksack.png");
    private static final ResourceLocation playerInventory = new ResourceLocation(TotallyOP.MODID, "textures/gui/player_inventory.png");
    public static final int spacer = 8;
    public static final int slotWidth = 18;
    public static final int slotHeight = 18;
    public static final int slotsX = 12;
    public static final int slotsY = 6;

    public static final int rucksackGuiWidth = 256;
    public static final int rucksackGuiHeight = 170;
    public static final int playerGuiWidth = 206;
    public static final int playerGuiHeight = 90;

    public static final int firstItemX = spacer;
    public static final int firstItemY = 56;
    public static final int trashX = firstItemX;
    public static final int trashY = firstItemY - slotHeight - spacer;
    public static final int compressX = firstItemX + slotsX * slotWidth + spacer;
    public static final int compressY = firstItemY;
    public static final int playerGuiOffsetX = (rucksackGuiWidth - playerGuiWidth) / 2;
    public static final int playerFirstItemX = playerGuiOffsetX + 8;
    public static final int playerFirstItemY = rucksackGuiHeight + 8;
    public static final int hotbarFirstItemY = rucksackGuiHeight + 66;
    public static final int offhandItemX = playerGuiOffsetX + 178;
    private static final int slotBorder = 1;
    private static final int whitelistBorder = 3;
    private static final int whitelistButtonDim = slotWidth / 2;
    private static final int whitelistTrashButtonX = trashX + slotsX * slotWidth - whitelistButtonDim;
    private static final int whitelistTrashButtonY = trashY - whitelistButtonDim - whitelistBorder;
    private static final int whitelistCompressButtonX = compressX + (slotWidth - whitelistButtonDim) / 2;
    private static final int whitelistCompressButtonY = compressY - whitelistButtonDim - whitelistBorder;
    private RucksackContainer container = null;
    private RucksackStorage storage = null;

    public RucksackGui(RucksackContainer container, RucksackStorage storage) {
        super(container);
        this.container = container;
        this.storage = storage;
        xSize = 256;
        ySize = 250;
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            if (isPointInRegion(
                whitelistTrashButtonX + guiLeft,
                whitelistTrashButtonY + guiTop,
                whitelistTrashButtonX + whitelistButtonDim + guiLeft,
                whitelistTrashButtonY + whitelistButtonDim + guiTop,
                mouseX, mouseY)) storage.whitelistTrash = !storage.whitelistTrash;
            if (isPointInRegion(
                    whitelistCompressButtonX + guiLeft,
                    whitelistCompressButtonY + guiTop,
                    whitelistCompressButtonX + whitelistButtonDim + guiLeft,
                    whitelistCompressButtonY + whitelistButtonDim + guiTop,
                    mouseX, mouseY)) storage.whitelistCompress = !storage.whitelistCompress;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(rucksackInventory);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, rucksackGuiWidth, rucksackGuiHeight);

        int x1 = guiLeft + trashX - whitelistBorder - slotBorder;
        int x2 = guiLeft + trashX + slotsX * slotWidth - 1;
        int y1 = guiTop + trashY - whitelistBorder - slotBorder;
        int y2 = guiTop + trashY + slotHeight - 1;
        int color = storage.whitelistTrash ? 0xffffffff : 0xff000000;
        drawTexturedModalRect(x2 - whitelistButtonDim + whitelistBorder, y1 - whitelistButtonDim, storage.whitelistTrash ? 0 : slotWidth, rucksackGuiHeight, whitelistButtonDim, whitelistButtonDim);
        drawRect(x1, y1, x2, y1 + whitelistBorder, color);
        drawRect(x1, y2, x2, y2 + whitelistBorder, color);
        drawRect(x1, y1, x1 + whitelistBorder, y2, color);
        drawRect(x2, y1, x2 + whitelistBorder, y2 + whitelistBorder, color);

        x1 = guiLeft + compressX - whitelistBorder - slotBorder;
        x2 = guiLeft + compressX + slotWidth - 1;
        y1 = guiTop + compressY - whitelistBorder - slotBorder;
        y2 = guiTop + compressY + slotsX * slotHeight - 1;
        color = storage.whitelistTrash ? 0xffffffff : 0xff000000;
        drawTexturedModalRect(x2 - whitelistButtonDim + whitelistBorder, y1 - whitelistButtonDim, storage.whitelistCompress ? 0 : slotWidth, rucksackGuiHeight, whitelistButtonDim, whitelistButtonDim);
        drawRect(x1, y1, x2, y1 + whitelistBorder, color);
        drawRect(x1, y2, x2, y2 + whitelistBorder, color);
        drawRect(x1, y1, x1 + whitelistBorder, y2, color);
        drawRect(x2, y1, x2 + whitelistBorder, y2 + whitelistBorder, color);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(playerInventory);
        drawTexturedModalRect(guiLeft + playerGuiOffsetX, guiTop + rucksackGuiHeight, 0, 0, playerGuiWidth, playerGuiHeight);
    }
}
