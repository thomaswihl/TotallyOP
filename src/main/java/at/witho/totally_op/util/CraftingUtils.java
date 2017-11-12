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

import java.util.*;

public class CraftingUtils {
    private static HashMap<ItemStack, ItemStack> toBlockList = new HashMap<ItemStack, ItemStack>();

    public static void init() {
        for (IRecipe irecipe : CraftingManager.REGISTRY)
        {
            NonNullList<Ingredient> ingredients = irecipe.getIngredients();
            if (ingredients.size() == 9)
            {
                ItemStack[] prev = ingredients.get(0).getMatchingStacks();
                boolean failed = false;
                for (int i = 1; i < 9; ++i) {
                    ItemStack[] stack = ingredients.get(i).getMatchingStacks();
                    if (stack.length == 0 || prev.length == 0 || !stack[0].isItemEqual(prev[0])) {
                        failed = true;
                        break;
                    }
                    prev = stack;
                }
                if (!failed) {
                    for (int i = 0; i < prev.length; ++i) {
                        ItemStack input = prev[i];
                        ItemStack output = irecipe.getRecipeOutput();
                        //TotallyOP.logger.log(Level.INFO, "Found BLOCK recipie: " + input + " -> " + output);
                        toBlockList.put(input, output);
                    }
                }
            }
        }
    }

    public static ItemStack toBlock(ItemStack input) {
        for (Map.Entry<ItemStack, ItemStack> set : toBlockList.entrySet()) {
            if (set.getKey().isItemEqual(input)) return set.getValue();
        }
        return null;
    }

}
