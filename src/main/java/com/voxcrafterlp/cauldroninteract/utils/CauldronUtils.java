package com.voxcrafterlp.cauldroninteract.utils;

import com.voxcrafterlp.cauldroninteract.CauldronInteract;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This file was created by VoxCrafter_LP!
 * Date: 26.07.2021
 * Time: 22:09
 * Project: CauldronInteract
 */

public abstract class CauldronUtils {

    /**
     * Removes an item from the dispenser's inventory and
     * replaces it with another one.
     *
     * There has to be a delay of 1 tick, because otherwise the dispenser
     * wouldn't contain the dispensed ItemStack.
     *
     * @param block Dispenser block
     * @param remove ItemStack that should be removed from the dispenser's inventory
     * @param add ItemStack that should be added to the dispenser's inventory
     * @param resetCauldron Determines if the cauldron should be reset to the default cauldron
     */
    protected void modifyDispenserInventory(Block block, ItemStack remove, ItemStack add, boolean resetCauldron) {
        final Dispenser dispenser = (Dispenser) block.getState();

        if(!CauldronInteract.getInstance().getBlockedInventories().contains(dispenser.getInventory()))
            CauldronInteract.getInstance().getBlockedInventories().add(dispenser.getInventory());

        Bukkit.getScheduler().scheduleSyncDelayedTask(CauldronInteract.getInstance(), () -> {
            final Inventory dispenserInventory = dispenser.getInventory();

            if(remove.getAmount() > 1) {
                dispenserInventory.forEach(itemStack -> {
                    if(itemStack == null) return;
                    if(itemStack.equals(remove))
                        itemStack.setAmount(itemStack.getAmount() - 1);
                });
            } else
                dispenserInventory.remove(remove);

            dispenserInventory.addItem(add);
            CauldronInteract.getInstance().getBlockedInventories().remove(dispenserInventory);
        }, 1);

        //Updates the dispenser block
        dispenser.update(true);
        block.getState().update(true);

        if(resetCauldron)
            block.getRelative(this.getFacing(block)).setType(Material.CAULDRON);
    }

    /**
     * Updates the water level of a cauldron and
     * triggers a {@link CauldronLevelChangeEvent}.
     * @param block Cauldron block
     * @param newLevel New water level (Range: 1-3)
     * @param reason Update reason
     * @throws IllegalArgumentException Thrown if the newLevel is incorrect
     */
    protected void updateCauldronWaterLevel(Block block, int newLevel, CauldronLevelChangeEvent.ChangeReason reason) throws IllegalArgumentException {
        if(!(newLevel >= 1 && newLevel <= 3))
            throw new IllegalArgumentException("The acceptable range is between 1 and 3.");

        final Levelled cauldronData = (Levelled) block.getBlockData();
        cauldronData.setLevel(newLevel);
        block.setBlockData(cauldronData);

        //Triggers a CauldronLevelChangeEvent
        Bukkit.getPluginManager().callEvent(new CauldronLevelChangeEvent(block, null, reason, block.getState()));
    }

    /**
     * Gets the water level of cauldron
     * @param block Cauldron block
     * @return Returns the water level (Range: 1-3)
     */
    protected int getCauldronWaterLevel(Block block) {
        return ((Levelled) block.getBlockData()).getLevel();
    }

    /**
     * Gets the BlockFace of a {@link Directional} block
     * @param block Directional block
     * @return Returns the BlockFace of the dispenser
     */
    protected BlockFace getFacing(final Block block) {
        return (block.getBlockData() instanceof Directional) ? ((Directional) block.getBlockData()).getFacing() : BlockFace.SELF;
    }

    /**
     * Method copied from the {@link org.bukkit.material.Dispenser} class
     * Source code can be found here: https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/material/Dispenser.java#L83
     * @param materialData MaterialData of the dispenser
     * @return Returns the BlockFace of the dispenser
     * @deprecated Use {@link #getFacing(Block)} instead
     */
    protected BlockFace getFacing(MaterialData materialData) {
        int data = materialData.getData() & 0x7;

        switch (data) {
            case 0x0:
                return BlockFace.DOWN;

            case 0x1:
                return BlockFace.UP;

            case 0x2:
                return BlockFace.NORTH;

            case 0x3:
                return BlockFace.SOUTH;

            case 0x4:
                return BlockFace.WEST;

            case 0x5:
            default:
                return BlockFace.EAST;
        }
    }

    /**
     * Builds a water bottle {@link ItemStack}
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
