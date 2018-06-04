package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.blocks.FunctionFlower;
import at.witho.totally_op.blocks.TierableBlock;
import at.witho.totally_op.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TileFunctionFlower extends TileEntity implements ITickable {
    public static final int[] DEFAULT_FORTUNE_CONFIG = {1, 2, 4, 6, 8, 12, 16};
    public static final int[] DEFAULT_EFFICIENCY_CONFIG = {400, 200, 100, 50, 20, 10, 5};
    public static final int[] DEFAULT_RANGE_CONFIG = {1, 3, 5, 7, 9, 11, 15};

    protected int[] fortuneConfig = DEFAULT_FORTUNE_CONFIG;
    protected int[] efficiencyConfig = DEFAULT_EFFICIENCY_CONFIG;
    protected int[] rangeConfig = DEFAULT_RANGE_CONFIG;

    protected int fortuneTier = 0;
    protected int efficiencyTier = 0;
    protected int rangeTier = 0;

    protected int fortune = 1;
    protected int efficiency = 1;
    protected int range = 0;
    protected EnumFacing facing = EnumFacing.NORTH;

    protected int minX = 0;
	protected int maxX = 0;
	protected int minZ = 0;
	protected int maxZ = 0;

    protected BlockPos currentPos = null;
    protected int delay = 1;
    protected ItemStack filter = ItemStack.EMPTY;
    protected boolean filterIsWhitelist = true;

    private int checkCounter = 0;
    private int lastSlot = 0;
    private int lastInventory = 0;


	public TileFunctionFlower() {
	    super();
        fortuneConfig = Config.intArray(Config.fortuneMultiplier.getStringList());
        efficiencyConfig = Config.intArray(Config.efficiencyDelay.getStringList());
        rangeConfig = Config.intArray(Config.farmingRange.getStringList());
    }

    public int getFortune() {
        return fortune;
    }
    public int getEfficiency() {
        return efficiency;
    }
    public int getRange() {
        return range;
    }

    public ItemStack getFilter() { return filter; }
    public void setFilter(ItemStack block) { filter = block.copy(); markDirty(); }

    public boolean getFilterIsWhitelist() { return filterIsWhitelist; }
    public void setFilterIsWhitelist(boolean whitelist) { filterIsWhitelist = whitelist; markDirty(); }

    @Override
    public void update() {
        checkCounter += efficiency;
        if (checkCounter > 40) {
            checkCounter = 0;
            checkForModifiers();
        }
    }

    protected void initLimits(int range) {
		minX = maxX = pos.getX();
		minZ = maxZ = pos.getZ();
		if (world != null) facing = world.getBlockState(pos).getValue(FunctionFlower.FACING);
		switch (facing) {
		case EAST: // +x
			minX++;
			maxX += range;
			minZ -= range / 2;
			maxZ += range / 2;
			break;
		case WEST: // -x
			minX -= range;
			maxX--;
			minZ -= range / 2;
			maxZ += range / 2;
			break;
		case NORTH: // -z
			minX -= range / 2;
			maxX += range / 2;
			minZ -= range;
			maxZ--;
			break;
		case SOUTH: // +z
			minX -= range / 2;
			maxX += range / 2;
			minZ++;
			maxZ += range;
			break;
		default:
			break;
		}
	}

	protected List<IItemHandler> frontInventories() {
        ArrayList<IItemHandler> list = new ArrayList<>();
	    addInventory(list, pos.offset(facing));
	    return list;
    }

    protected List<IItemHandler> backInventories() {
        ArrayList<IItemHandler> list = new ArrayList<>();
        addInventory(list, pos.offset(facing, -1));
        addInventory(list, pos.up());
        addInventory(list, pos.offset(facing.rotateY()));
        addInventory(list, pos.down());
        addInventory(list, pos.offset(facing.rotateYCCW()));
        return list;
    }

    protected void addInventory(List<IItemHandler> list, BlockPos pos) {
        TileEntity e = world.getTileEntity(pos);
        if (e != null) {
            IItemHandler inventory = e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (inventory != null) list.add(inventory);
        }
    }

    protected boolean addToInventory(IItemHandler inventory, List<EntityItem> items) {
        for (int i = 0; i < inventory.getSlots(); ++i) {
            boolean allDone = true;
            for (EntityItem item : items) {
                ItemStack itemStack = item.getItem();
                if (itemStack.isEmpty()) continue;
                ItemStack remain = inventory.insertItem(i, itemStack.copy(), false);
                itemStack.setCount(remain.getCount());
                if (remain.getCount() != 0) allDone = false;
            }
            if (allDone) return true;
        }
        return false;
    }

    private boolean findInventory(List<EntityItem> items) {
        List<IItemHandler> inventories = backInventories();
        for (IItemHandler inventory : inventories) {
            if (addToInventory(inventory, items)) return true;
        }
        return false;
    }


    protected boolean matchesFilter(IBlockState state) {
        if (!filter.isEmpty()) {
            Block filterBlock = Block.getBlockFromItem(filter.getItem());
            IBlockState filterState = filterBlock.getStateFromMeta(filter.getMetadata());
            return !(state.equals(filterState) ^ filterIsWhitelist);
        }
        return true;
    }

    protected List<EntityItem> filteredInputItems() {
	    double y = pos.getY();
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(minX, y - rangeConfig[rangeTier], minZ, maxX + 1, y + rangeConfig[rangeTier] + 1, maxZ + 1));
        if (items.isEmpty()) return items;
        if (!filter.isEmpty()) {
            for (Iterator<EntityItem> iter = items.iterator(); iter.hasNext();) {
                EntityItem item = iter.next();
                boolean match = item.getItem().isItemEqual(filter);
                if (filterIsWhitelist != match) iter.remove();
            }
        }
	    return items;
    }

    interface Transform {
        ItemStack transform9(ItemStack from);
        ItemStack transform4(ItemStack from);
        int outputCount(int inputCount, int factor);
    }

    protected void transformItems(Transform transform) {
        List<IItemHandler> inventories = frontInventories();
        List<EntityItem> moveItems = new ArrayList<EntityItem>();
        BlockPos outputPos = pos.offset(facing, -1);
        if (inventories.isEmpty()) {
            for (EntityItem entity : filteredInputItems()) {
                ItemStack stack = entity.getItem();
                int count = 9;
                ItemStack output = transform.transform9(stack);
                if (output == null) {
                    output = transform.transform4(stack);
                    count = 4;
                }
                if (output != null) {
                    int outputCount = transform.outputCount(stack.getCount(), count);
                    if (outputCount > 0) {
                        output = output.copy();
                        output.setCount(outputCount);

                        if (outputCount < stack.getCount()) {
                            stack.setCount(stack.getCount() % count);
                            entity.setItem(stack);
                            EntityItem ei = new EntityItem(world, outputPos.getX() + 0.5, outputPos.getY(), outputPos.getZ() + 0.5, output);
                            ei.motionX = ei.motionY = ei.motionZ = 0;
                            moveItems.add(ei);
                        } else {
                            stack.setCount(0);
                        }
                    }
                }
            }
        } else {
            for (IItemHandler inventory : inventories) {
                ItemStack inputItem = null;
                ItemStack outputItem = null;
                int inputMultiple = 0;
                int inputAmount = 0;
                for (int i = lastSlot; i < inventory.getSlots(); ++i) {
                    ItemStack thisItem = inventory.getStackInSlot(i);
                    if (inputItem == null && thisItem != null && thisItem.getCount() > 0) {
                        inputItem = thisItem.copy();
                        inputMultiple = 9;
                        outputItem = transform.transform9(inputItem);
                        if (outputItem == null) {
                            outputItem = transform.transform4(inputItem);
                            inputMultiple = 4;
                            if (outputItem == null) inputItem = null;
                        }
                        if (inputItem != null) lastSlot = i;
                    }
                    if (outputItem != null && thisItem.isItemEqual(inputItem)) {
                        ItemStack move = inventory.extractItem(i, thisItem.getCount(), false);
                        inputAmount += move.getCount();
                    }
                }
                if (inputMultiple > 0 && inputAmount > 0) {
                    int c = inputAmount * inputMultiple;
                    outputItem = outputItem.copy();
                    outputItem.setCount(c);
                    EntityItem ei = new EntityItem(world, outputPos.getX() + 0.5, outputPos.getY(), outputPos.getZ() + 0.5, outputItem);
                    ei.motionX = ei.motionY = ei.motionZ = 0;
                    moveItems.add(ei);
                } else {
                    lastSlot = inventory.getSlots();

                    if (lastSlot >= inventory.getSlots()) {
                        lastSlot = 0;
                        lastInventory++;
                        if (lastInventory >= inventories.size()) lastInventory = 0;
                    }
                }

            }
        }
        if (!findInventory(moveItems)) {
            for (EntityItem item : moveItems) {
                world.spawnEntity(item);
            }
        }
    }

    protected void checkForModifiers() {
        BlockPos p = getPos();
        fortuneTier = 0;
        efficiencyTier = 0;
        rangeTier = 0;
        for (int i = 0; i < 4; ++i) {
            p = p.down();
            IBlockState state = world.getBlockState(p);
            Block block = state.getBlock();
            if (block instanceof TierableBlock) {
                TierableBlock e = (TierableBlock)block;
                int tier = e.getTier(state);
                if (block.getRegistryName().getResourcePath() == TierableBlock.FORTUNE)
                    fortuneTier = tier;
                else if (block.getRegistryName().getResourcePath() == TierableBlock.EFFICIENCY)
                    efficiencyTier = tier;
                else if (block.getRegistryName().getResourcePath() == TierableBlock.RANGE)
                    rangeTier = tier;
            }
        }
        int newRange = rangeConfig[rangeTier];
        if (range != newRange) {
            range = newRange;
            initLimits(range);
        }
        efficiency = efficiencyConfig[efficiencyTier];
        fortune = fortuneConfig[fortuneTier];
    }

    protected boolean shouldRun() {
        if (world.isRemote) return false;
        if (FunctionFlower.isPowered(getBlockMetadata())) return false;
        if (delay < efficiency) {
            ++delay;
            return false;
        }
        delay = 0;
        return true;
    }

    protected void resetPos() {
        currentPos = new BlockPos(minX, pos.getY(), minZ);
    }

    protected void nextBlock() {
        currentPos = currentPos.east();
        if (currentPos.getX() > maxX) {
            currentPos = currentPos.add(-range, 0, 1);
            if (currentPos.getZ() > maxZ) currentPos = null;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
	    super.readFromNBT(compound);
	    filterIsWhitelist = compound.getBoolean("filterIsWhitelist");
	    filter = new ItemStack(compound.getCompoundTag("filter"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (!filter.isEmpty()) {
            NBTTagCompound stack = new NBTTagCompound();
            filter.writeToNBT(stack);
            compound.setTag("filter", stack);
            compound.setBoolean("filterIsWhitelist", filterIsWhitelist);
        }
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

}
