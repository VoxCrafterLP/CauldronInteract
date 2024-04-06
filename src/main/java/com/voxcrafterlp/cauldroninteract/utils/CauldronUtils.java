package com.voxcrafterlp.cauldroninteract.utils;

import com.voxcrafterlp.cauldroninteract.CauldronInteract;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * This file was created by VoxCrafter_LP!
 * Date: 26.07.2021
 * Time: 22:09
 * Project: CauldronInteract
 */

public abstract class CauldronUtils {

    /**
     * Removes an item from the dispenser's inventory and
     * replaces it with another one. If the dispenser's inventory is full, the new item will be dropped below
     * the dispenser.
     * There has to be a delay of 1 tick, because otherwise the dispenser
     * wouldn't contain the dispensed ItemStack.
     *
     * @param block         Dispenser block
     * @param remove        ItemStack that should be removed from the dispenser's inventory
     * @param add           ItemStack that should be added to the dispenser's inventory
     * @param resetCauldron Determines if the cauldron should be reset to the default cauldron
     */
    protected void modifyDispenserInventory(Block block, ItemStack remove, ItemStack add, boolean resetCauldron) {
        final Dispenser dispenser = (Dispenser) block.getState();

        if (!CauldronInteract.getInstance().getBlockedInventories().contains(dispenser.getInventory()))
            CauldronInteract.getInstance().getBlockedInventories().add(dispenser.getInventory());

        Bukkit.getScheduler().scheduleSyncDelayedTask(CauldronInteract.getInstance(), () -> {
            final Inventory dispenserInventory = dispenser.getInventory();

            // Handle remove
            if (remove.getAmount() > 1) {
                final int removeIndex = dispenserInventory.first(remove);
                final ItemStack newItemStack = dispenserInventory.getItem(removeIndex);

                if (newItemStack != null) {
                    newItemStack.setAmount(newItemStack.getAmount() - 1);
                    dispenserInventory.setItem(removeIndex, newItemStack);
                }
            } else
                dispenserInventory.setItem(dispenserInventory.first(remove), new ItemStack(Material.AIR));

            // Handle add
            if (dispenserInventory.firstEmpty() == -1)
                dispenser.getWorld().dropItem(dispenser.getLocation().add(0.5, -0.75, 0.5), add);
            else
                dispenserInventory.addItem(add);

            CauldronInteract.getInstance().getBlockedInventories().remove(dispenserInventory);
        }, 1);

        //Updates the dispenser block
        dispenser.update(true);
        block.getState().update(true);

        if (resetCauldron)
            block.getRelative(this.getFacing(block)).setType(Material.CAULDRON);
    }

    /**
     * Updates the water level of a cauldron and
     * triggers a {@link CauldronLevelChangeEvent}.
     *
     * @param block    Cauldron block
     * @param newLevel New water level (Range: 1-3)
     * @param reason   Update reason
     * @throws IllegalArgumentException Thrown if the newLevel is incorrect
     */
    protected void updateCauldronWaterLevel(Block block, int newLevel, CauldronLevelChangeEvent.ChangeReason reason) throws IllegalArgumentException {
        if (!(newLevel >= 1 && newLevel <= 3))
            throw new IllegalArgumentException("The acceptable range is between 1 and 3.");

        final Levelled cauldronData = (Levelled) block.getBlockData();
        cauldronData.setLevel(newLevel);
        block.setBlockData(cauldronData);

        //Triggers a CauldronLevelChangeEvent
        Bukkit.getPluginManager().callEvent(new CauldronLevelChangeEvent(block, null, reason, block.getState()));
    }

    /**
     * Gets the water level of cauldron
     *
     * @param block Cauldron block
     * @return Returns the water level (Range: 1-3)
     */
    protected int getCauldronWaterLevel(Block block) {
        return ((Levelled) block.getBlockData()).getLevel();
    }

    /**
     * Gets the BlockFace of a {@link Directional} block
     *
     * @param block Directional block
     * @return Returns the BlockFace of the dispenser
     */
    protected BlockFace getFacing(final Block block) {
        return (block.getBlockData() instanceof Directional) ? ((Directional) block.getBlockData()).getFacing() : BlockFace.SELF;
    }

    /**
     * Method copied from the {@link org.bukkit.material.Dispenser} class
     * Source code can be found here: <a href="https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/material/Dispenser.java#L83">...</a>
     *
     * @param materialData MaterialData of the dispenser
     * @return Returns the BlockFace of the dispenser
     * @deprecated Use {@link #getFacing(Block)} instead
     */
    protected BlockFace getFacing(MaterialData materialData) {
        int data = materialData.getData() & 0x7;

        return switch (data) {
            case 0x0 -> BlockFace.DOWN;
            case 0x1 -> BlockFace.UP;
            case 0x2 -> BlockFace.NORTH;
            case 0x3 -> BlockFace.SOUTH;
            case 0x4 -> BlockFace.WEST;
            default -> BlockFace.EAST;
        };
    }

    /**
     * Builds a water bottle {@link ItemStack}
     * @deprecated Potion data is deprecated, remains in use to maintain 1.17 compatibility
     * @return Water bottle ItemStack
     */
    protected ItemStack getWaterBottleItemStack() {
        final ItemStack itemStack = new ItemStack(Material.POTION, 1);
        final PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
        itemStack.setItemMeta(potionMeta);

        return itemStack;
    }

    protected boolean isFull(Levelled levelled) {
        return levelled.getLevel() == levelled.getMaximumLevel();
    }

}
