package at.witho.totally_op.util;

import at.witho.totally_op.TotallyOP;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftingUtils {
    public static HashMap<Item, Block> itemToBlock = null;

    public static void init() {
        TotallyOP.logger.log(Level.ERROR, "Looking for recipes");
        itemToBlock = new HashMap<Item, Block>();
        for (IRecipe irecipe : CraftingManager.REGISTRY)
        {
            NonNullList<Ingredient> ingredients = irecipe.getIngredients();
            if (ingredients.size() == 9)
            {
                ItemStack[] prev = ingredients.get(0).getMatchingStacks();
                boolean failed = false;
                for (int i = 1; i < 9; ++i) {
                    ItemStack[] stack = ingredients.get(i).getMatchingStacks();
                    if (stack.length != 1 || prev.length != 1 || !stack[0].isItemEqual(prev[0])) {
                        failed = true;
                        break;
                    }
                    prev = stack;
                }
                if (!failed) {
                    Block output = Block.getBlockFromItem(irecipe.getRecipeOutput().getItem());
                    Item input = prev[0].getItem();
                    TotallyOP.logger.log(Level.ERROR, "Found recipe:" + input.getRegistryName() + " -> " + output.getRegistryName());
                    if (output != Blocks.AIR) {
                        itemToBlock.put(input, output);
                    }
                }
            }
        }
    }


}
