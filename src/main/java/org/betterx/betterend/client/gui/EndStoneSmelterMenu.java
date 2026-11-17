package org.betterx.betterend.client.gui;

import org.betterx.bclib.recipes.AlloyingRecipe;
import org.betterx.bclib.recipes.AlloyingRecipeInput;
import org.betterx.betterend.blocks.entities.EndStoneSmelterBlockEntity;
import org.betterx.betterend.client.gui.slot.SmelterFuelSlot;
import org.betterx.betterend.client.gui.slot.SmelterOutputSlot;
import org.betterx.betterend.registry.EndMenuTypes;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;

public class EndStoneSmelterMenu extends RecipeBookMenu<AlloyingRecipeInput, AlloyingRecipe> {
    public static final int INGREDIENT_SLOT_A = 0;
    public static final int INGREDIENT_SLOT_B = 1;
    public static final int FUEL_SLOT = 2;
    public static final int RESULT_SLOT = 3;

    public static final int SLOT_COUNT = 4;
    private static final int INV_SLOT_START = SLOT_COUNT;
    private static final int INV_SLOT_END = INV_SLOT_START + 3 * 9;
    private static final int USE_ROW_SLOT_START = INV_SLOT_END;
    private static final int USE_ROW_SLOT_END = USE_ROW_SLOT_START + 9;

    private final Container inventory;
    private final ContainerData propertyDelegate;
    protected final Level world;

    public EndStoneSmelterMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(4));
    }

    public EndStoneSmelterMenu(
            int syncId,
            Inventory playerInventory,
            Container inventory,
            ContainerData propertyDelegate
    ) {
        super(EndMenuTypes.END_STONE_SMELTER, syncId);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.level();

        addDataSlots(propertyDelegate);
        addSlot(new Slot(inventory, INGREDIENT_SLOT_A, 45, 17));
        addSlot(new Slot(inventory, INGREDIENT_SLOT_B, 67, 17));
        addSlot(new SmelterFuelSlot(this, inventory, FUEL_SLOT, 56, 53));
        addSlot(new SmelterOutputSlot(playerInventory.player, inventory, RESULT_SLOT, 129, 35));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return EndMenuTypes.END_STONE_SMELTER;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents finder) {
        if (inventory instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible) inventory).fillStackedContents(finder);
        }
    }

    @Override
    public void clearCraftingContent() {
        this.getSlot(INGREDIENT_SLOT_A).set(ItemStack.EMPTY);
        this.getSlot(INGREDIENT_SLOT_B).set(ItemStack.EMPTY);
        this.getSlot(RESULT_SLOT).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(RecipeHolder<AlloyingRecipe> recipeHolder) {
        return recipeHolder
                .value()
                .matches(new AlloyingRecipeInput(this.inventory.getItem(INGREDIENT_SLOT_A), this.inventory.getItem(INGREDIENT_SLOT_B)), this.world);
    }

    @Override
    public int getResultSlotIndex() {
        return RESULT_SLOT;
    }

    @Override
    public int getGridWidth() {
        return 2;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return SLOT_COUNT;
    }

    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.BLAST_FURNACE;
    }

    @Override
    public boolean shouldMoveToInventory(int i) {
        return i != FUEL_SLOT;
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }

    protected boolean isSmeltable(ItemStack itemStack) {
        return world.getRecipeManager().getRecipeFor(RecipeType.BLASTING, new SingleRecipeInput(itemStack), world).isPresent()
                || world.getRecipeManager().getRecipeFor(AlloyingRecipe.TYPE, new AlloyingRecipeInput(itemStack), world).isPresent();
    }

    public boolean isFuel(ItemStack itemStack) {
        return EndStoneSmelterBlockEntity.canUseAsFuel(itemStack, this.world);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack itemStack = slotStack.copy();
        if (index == RESULT_SLOT) {
            if (!moveItemStackTo(slotStack, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(slotStack, itemStack);
        } else if (index == INGREDIENT_SLOT_A || index == INGREDIENT_SLOT_B || index == FUEL_SLOT) {
            if (!moveItemStackTo(slotStack, INV_SLOT_START, USE_ROW_SLOT_END, false)) return ItemStack.EMPTY;
        } else if (isSmeltable(slotStack)) {
            if (!moveItemStackTo(slotStack, INGREDIENT_SLOT_A, FUEL_SLOT, false)) return ItemStack.EMPTY;
        } else if (isFuel(slotStack)) {
            if (!moveItemStackTo(slotStack, FUEL_SLOT, RESULT_SLOT, false)) return ItemStack.EMPTY;
        } else if (index < INV_SLOT_END) {
            if (!moveItemStackTo(slotStack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(slotStack, INV_SLOT_START, INV_SLOT_END, false)) {
            return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (slotStack.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, slotStack);

        return itemStack;
    }

    @OnlyIn(Dist.CLIENT)
    public int getSmeltProgress() {
        int time = propertyDelegate.get(2);
        int timeTotal = propertyDelegate.get(3);
        return timeTotal != 0 && time != 0 ? time * 24 / timeTotal : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getFuelProgress() {
        int fuelTime = propertyDelegate.get(1);
        if (fuelTime == 0) {
            fuelTime = 200;
        }
        return propertyDelegate.get(0) * 13 / fuelTime;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isBurning() {
        return propertyDelegate.get(0) > 0;
    }
}
