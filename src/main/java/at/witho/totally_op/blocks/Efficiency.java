package at.witho.totally_op.blocks;

import at.witho.totally_op.TotallyOP;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Efficiency extends Block {
    protected int tier = 0;
    public Efficiency(int tier) {
        super(Material.ROCK, Material.ROCK.getMaterialMapColor());
        this.tier = tier;
        this.setDefaultState(blockState.getBaseState());
        setUnlocalizedName(TotallyOP.MODID + ".efficiency_tier" + tier);
        setRegistryName("efficiency_tier" + tier);
        setTickRandomly(false);
        this.setSoundType(SoundType.STONE);
        setHardness(2.0f);
        setResistance(30);
    }

    public int getTier() {
        return tier;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
