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
    private static HashMap<ItemStack, ItemStack> toBlock9 = new HashMap<ItemStack, ItemStack>();
    private static HashMap<ItemStack, ItemStack> toBlock4 = new HashMap<ItemStack, ItemStack>();

    public static void init() {
        for (IRecipe irecipe : CraftingManager.REGISTRY)
        {
            NonNullList<Ingredient> ingredients = irecipe.getIngredients();
            ItemStack[] item = null;
            int count = 0;
            boolean failed = false;
            for (int i = 0; i < ingredients.size(); ++i) {
                ItemStack[] stack = ingredients.get(i).getMatchingStacks();
                if (stack.length != 0) {
                    if (item == null) item = stack;
                    else if (!stack[0].isItemEqual(item[0])) failed = true;
                    count++;
                }
            }
            if (!failed && count == 4) {
                if (!irecipe.canFit(2, 2) || irecipe.getRecipeOutput().getCount() != 1) {
                    failed = true;
                }
            }
            if (!failed && item != null && (count == 4 || count == 9)) {
                for (int i = 0; i < item.length; ++i) {
                    ItemStack input = item[i];
                    ItemStack output = irecipe.getRecipeOutput();
                    //TotallyOP.logger.log(Level.INFO, "Found BLOCK recipie: " + count + "x" + input + " -> " + output);
                    if (count == 9) toBlock9.put(input, output);
                    else if (count == 4) toBlock4.put(input, output);
                }
            }
        }
    }

    private static ItemStack reduce(ItemStack input, ItemStack output, int factor) {
        output.setCount(input.getCount() / factor);
        input.setCount(input.getCount() % factor);
        return output;
    }

    private static ItemStack multiply(ItemStack input, ItemStack output, int factor) {
        output.setCount(input.getCount() * factor);
        input.setCount(0);
        return output;
    }

    public static ItemStack toBlock(ItemStack input) {
        for (Map.Entry<ItemStack, ItemStack> set : toBlock9.entrySet()) {
            if (set.getKey().isItemEqual(input)) return reduce(input, set.getValue().copy(), 9);
        }
        for (Map.Entry<ItemStack, ItemStack> set : toBlock4.entrySet()) {
            if (set.getKey().isItemEqual(input)) return reduce(input, set.getValue().copy(), 4);
        }
        return null;
    }

    public static ItemStack toItem(ItemStack input) {
        for (Map.Entry<ItemStack, ItemStack> set : toBlock9.entrySet()) {
            if (set.getValue().isItemEqual(input)) return multiply(input, set.getKey().copy(), 9);
        }
        for (Map.Entry<ItemStack, ItemStack> set : toBlock4.entrySet()) {
            if (set.getValue().isItemEqual(input)) return multiply(input, set.getKey().copy(), 4);
        }
        return null;
    }

    public static boolean canToBlock(ItemStack input) {
        for (ItemStack stack : toBlock9.keySet()) {
            if (stack.isItemEqual(input)) return true;
        }
        for (ItemStack stack : toBlock4.keySet()) {
            if (stack.isItemEqual(input)) return true;
        }
        return false;
    }

    public static boolean canToItem(ItemStack input) {
        for (ItemStack stack : toBlock9.values()) {
            if (stack.isItemEqual(input)) return true;
        }
        for (ItemStack stack : toBlock4.values()) {
            if (stack.isItemEqual(input)) return true;
        }
        return false;
    }

}
