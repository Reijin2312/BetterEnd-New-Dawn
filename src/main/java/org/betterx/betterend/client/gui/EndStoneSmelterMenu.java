package org.betterx.betterend.client.gui;

import net.minecraft.recipebook.ServerPlaceRecipe;
import org.betterx.bclib.recipes.AlloyingRecipe;
import org.betterx.bclib.recipes.AlloyingRecipeInput;
import org.betterx.betterend.blocks.entities.EndStoneSmelterBlockEntity;
import org.betterx.betterend.client.gui.slot.SmelterFuelSlot;
import org.betterx.betterend.client.gui.slot.SmelterOutputSlot;
import org.betterx.betterend.registry.EndMenuTypes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;


import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EndStoneSmelterMenu extends RecipeBookMenu {
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
        addStandardInventorySlots(playerInventory, 8, 84);
    }

    @Override
    public @NotNull MenuType<?> getType() {
        return EndMenuTypes.END_STONE_SMELTER;
    }

    public Slot getResultSlot() {
        return this.slots.get(RESULT_SLOT);
    }

    public Slot getInputSlotA() {
        return this.slots.get(INGREDIENT_SLOT_A);
    }

    public Slot getInputSlotB() {
        return this.slots.get(INGREDIENT_SLOT_B);
    }

    public Slot getFuelSlot() {
        return this.slots.get(FUEL_SLOT);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents finder) {
        if (inventory instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible) inventory).fillStackedContents(finder);
        }
    }

    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.BLAST_FURNACE;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public RecipeBookMenu.PostPlaceAction handlePlacement(
            boolean placeAll,
            boolean alreadyPopulated,
            RecipeHolder<?> recipe,
            ServerLevel serverLevel,
            Inventory playerInventory
    ) {
        final List<Slot> inputSlots = List.of(this.getInputSlotA(), this.getInputSlotB());
        final List<Slot> slotsToClear = List.of(this.getInputSlotA(), this.getInputSlotB(), this.getResultSlot());

        return ServerPlaceRecipe.placeRecipe(
                new ServerPlaceRecipe.CraftingMenuAccess() {
                    @Override
                    public void fillCraftSlotsStackedContents(StackedItemContents finder) {
                        EndStoneSmelterMenu.this.fillCraftSlotsStackedContents(finder);
                    }

                    @Override
                    public void clearCraftingContent() {
                        slotsToClear.forEach(slot -> slot.set(ItemStack.EMPTY));
                    }

                    @Override
                    public boolean recipeMatches(RecipeHolder holder) {
                        Object value = holder.value();
                        if (value instanceof AlloyingRecipe alloyingRecipe) {
                            AlloyingRecipeInput input = new AlloyingRecipeInput(
                                    EndStoneSmelterMenu.this.inventory.getItem(INGREDIENT_SLOT_A),
                                    EndStoneSmelterMenu.this.inventory.getItem(INGREDIENT_SLOT_B)
                            );
                            return alloyingRecipe.matches(input, serverLevel);
                        }

                        if (value instanceof BlastingRecipe blastingRecipe) {
                            AlloyingRecipeInput input = new AlloyingRecipeInput(
                                    EndStoneSmelterMenu.this.inventory.getItem(INGREDIENT_SLOT_A),
                                    EndStoneSmelterMenu.this.inventory.getItem(INGREDIENT_SLOT_B)
                            );
                            return blastingRecipe.matches(new SingleRecipeInput(input.any()), serverLevel);
                        }

                        return false;
                    }
                },
                2,
                1,
                inputSlots,
                slotsToClear,
                playerInventory,
                (RecipeHolder) recipe,
                placeAll,
                alreadyPopulated
        );
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }

    protected boolean isSmeltable(ItemStack itemStack) {
        if (!(world.recipeAccess() instanceof RecipeManager recipeManager)) return false;
        return recipeManager.getRecipeFor(AlloyingRecipe.TYPE, new AlloyingRecipeInput(itemStack), world).isPresent();
    }

    public boolean isFuel(ItemStack itemStack) {
        return EndStoneSmelterBlockEntity.canUseAsFuel(itemStack, world);
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
        } else if (((index == FUEL_SLOT) || (index == INGREDIENT_SLOT_A) || (index == INGREDIENT_SLOT_B))
                ? !this.moveItemStackTo(slotStack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)
                : (this.isSmeltable(slotStack)
                        ? !this.moveItemStackTo(slotStack, INGREDIENT_SLOT_A, FUEL_SLOT, false)
                        : (this.isFuel(slotStack)
                                ? !this.moveItemStackTo(slotStack, FUEL_SLOT, RESULT_SLOT, false)
                                : (((index >= INV_SLOT_START) && (index < INV_SLOT_END))
                                        ? !this.moveItemStackTo(slotStack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)
                                        : ((index >= USE_ROW_SLOT_START) && (index < USE_ROW_SLOT_END) && !this.moveItemStackTo(
                                                slotStack,
                                                INV_SLOT_START,
                                                INV_SLOT_END,
                                                false
                                        )))))) {
            return ItemStack.EMPTY;
        }

        if (slotStack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (slotStack.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, slotStack);

        return itemStack;
    }

    public int getSmeltProgress() {
        int time = propertyDelegate.get(2);
        int timeTotal = propertyDelegate.get(3);
        return timeTotal != 0 && time != 0 ? time * 24 / timeTotal : 0;
    }

    public int getFuelProgress() {
        int fuelTime = propertyDelegate.get(1);
        if (fuelTime == 0) {
            fuelTime = 200;
        }
        return propertyDelegate.get(0) * 13 / fuelTime;
    }

    public boolean isBurning() {
        return propertyDelegate.get(0) > 0;
    }
}
