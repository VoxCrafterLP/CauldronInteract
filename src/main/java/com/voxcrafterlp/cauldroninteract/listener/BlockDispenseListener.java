package com.voxcrafterlp.cauldroninteract.listener;

import com.voxcrafterlp.cauldroninteract.CauldronInteract;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Cauldron;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

/**
 * This file was created by VoxCrafter_LP!
 * Date: 13.07.2021
 * Time: 11:02
 * Project: CauldronInteract
 */

public class BlockDispenseListener implements Listener {

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if(event.getBlock().getType() != Material.DISPENSER) return;

        final Dispenser dispenser = (Dispenser) event.getBlock().getState();
        final ItemStack item = event.getItem();

        if(item.getType() == Material.WATER_BUCKET ||
            item.getType() == Material.LAVA_BUCKET ||
            item.getType() == Material.POWDER_SNOW_BUCKET ||
            item.getType() == Material.BUCKET ||
            item.getType() == Material.GLASS_BOTTLE) {

            //Gets the block in front of the dispenser.
            final BlockFace blockFace = this.getFacing(dispenser.getData());
            final Block dispenseBlock = event.getBlock().getRelative(blockFace);

            event.setCancelled(true);

            switch (dispenseBlock.getType()) {
                case CAULDRON:
                    if(item.getType() == Material.BUCKET || item.getType() == Material.GLASS_BOTTLE) {
                        event.setCancelled(false);
                        return;
                    }

                    //Sets the correct cauldron type for every fluid.
                    switch (item.getType()) {
                        case WATER_BUCKET:
                            dispenseBlock.setType(Material.WATER_CAULDRON);
                            dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_FILL, 1, 1);
                            break;
                        case LAVA_BUCKET:
                            dispenseBlock.setType(Material.LAVA_CAULDRON);
                            dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_FILL_LAVA, 1, 1);
                            break;
                        case POWDER_SNOW_BUCKET:
                            dispenseBlock.setType(Material.POWDER_SNOW_CAULDRON);
                            dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_FILL_POWDER_SNOW, 1, 1);
                            break;
                    }

                    this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.BUCKET), false);

                    if(dispenseBlock.getType() == Material.WATER_CAULDRON || dispenseBlock.getType() == Material.POWDER_SNOW_CAULDRON) {
                        //Fills the cauldron to the maximum level (3).
                        this.updateCauldronWaterLevel(dispenseBlock, 3, CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY);
                    } else
                        //Triggers a CauldronLevelChangeEvent for the lava bucket
                        Bukkit.getPluginManager().callEvent(new CauldronLevelChangeEvent(dispenseBlock, null,
                                CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY, dispenseBlock.getState()));
                    break;
                case WATER_CAULDRON:
                case POWDER_SNOW_CAULDRON:
                    if(dispenseBlock.getType() == Material.WATER_CAULDRON) {
                        final Cauldron cauldron = (Cauldron) dispenseBlock.getState().getData();

                        if(!cauldron.isFull() && item.getType() == Material.BUCKET) {
                            event.setCancelled(false);
                            return;
                        }
                    }

                    if(item.getType() != Material.BUCKET && item.getType() != Material.GLASS_BOTTLE) {
                        event.setCancelled(false);
                        return;
                    }

                    switch (dispenseBlock.getType()) {
                        case WATER_CAULDRON:
                            if(item.getType() == Material.BUCKET) {
                                this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.WATER_BUCKET), true);
                                dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1, 1);
                            } else {
                                final int waterLevel = this.getCauldronWaterLevel(dispenseBlock);

                                if(waterLevel > 1)
                                    this.updateCauldronWaterLevel(dispenseBlock, waterLevel - 1,
                                            CauldronLevelChangeEvent.ChangeReason.BOTTLE_FILL);

                                this.modifyDispenserInventory(event.getBlock(), item, this.getWaterBottleItemStack(), (waterLevel < 1));
                                dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BOTTLE_FILL, 1, 1);
                                return;
                            }
                            break;
                        case POWDER_SNOW_CAULDRON:
                            if(item.getType() != Material.BUCKET) {
                                event.setCancelled(false);
                                return;
                            }

                            this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.POWDER_SNOW_BUCKET), true);
                            dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW, 1, 1);
                            break;
                    }

                    //Triggers a CauldronLevelChangeEvent
                    Bukkit.getPluginManager().callEvent(new CauldronLevelChangeEvent(dispenseBlock, null,
                            CauldronLevelChangeEvent.ChangeReason.BUCKET_FILL, dispenseBlock.getState()));
                    break;
                case LAVA_CAULDRON:
                    this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.LAVA_BUCKET), true);
                    dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_EMPTY_LAVA, 1, 1);

                    //Triggers a CauldronLevelChangeEvent
                    Bukkit.getPluginManager().callEvent(new CauldronLevelChangeEvent(dispenseBlock, null,
                            CauldronLevelChangeEvent.ChangeReason.BUCKET_FILL, dispenseBlock.getState()));
                    break;
            }
        }
    }

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
    private void modifyDispenserInventory(Block block, ItemStack remove, ItemStack add, boolean resetCauldron) {
        final Dispenser dispenser = (Dispenser) block.getState();

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
        }, 1);

        //Updates the dispenser block
        dispenser.update(true);
        block.getState().update(true);

        if(resetCauldron)
            block.getRelative(this.getFacing(dispenser.getData())).setType(Material.CAULDRON);
    }

    /**
     * Updates the water level of a cauldron and
     * triggers a {@link CauldronLevelChangeEvent}.
     * @param block Cauldron block
     * @param newLevel New water level (Range: 1-3)
     * @param reason Update reason
     * @throws IllegalArgumentException Thrown if the newLevel is incorrect
     */
    private void updateCauldronWaterLevel(Block block, int newLevel, CauldronLevelChangeEvent.ChangeReason reason) throws IllegalArgumentException {
        if(!(newLevel > 0 && newLevel < 4))
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
    private int getCauldronWaterLevel(Block block) {
        return ((Levelled) block.getBlockData()).getLevel();
    }

    /**
     * Method copied from the {@link org.bukkit.material.Dispenser} class
     * Source code can be found here: https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/material/Dispenser.java#L83
     * @param materialData MaterialData of the dispenser
     * @return Returns the BlockFace of the dispenser
     */
    private BlockFace getFacing(MaterialData materialData) {
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
    private ItemStack getWaterBottleItemStack() {
        final ItemStack itemStack = new ItemStack(Material.POTION, 1);
        final PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
        potionMeta.setBasePotionData(new PotionData(PotionType.WATER));
        itemStack.setItemMeta(potionMeta);

        return itemStack;
    }

}
