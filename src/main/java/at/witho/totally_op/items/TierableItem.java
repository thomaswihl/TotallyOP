package at.witho.totally_op.items;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.blocks.TierableBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.List;

public class TierableItem extends ItemBlock {

    public TierableItem(TierableBlock block) {
        super(block);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation[] models = new ModelResourceLocation[7];
        models[0] = new ModelResourceLocation(getRegistryName(), "inventory");
        for (int i = 1; i < models.length; ++i) {
            models[i] = new ModelResourceLocation(getRegistryName() + "_tier" + i, "inventory");
        }
        ModelBakery.registerItemVariants(this, models);
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                NBTTagCompound nbt = stack.getTagCompound();
                int tier = 0;
                if (nbt != null) {
                    tier = nbt.getInteger("Tier");
                }
                return models[tier];
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        int tier = 0;
        if (nbt != null) {
            tier = nbt.getInteger("Tier");
        }
        tooltip.add("Tier " + tier);
    }


}
