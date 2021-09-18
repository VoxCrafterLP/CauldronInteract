package com.voxcrafterlp.cauldroninteract.listener;

import com.voxcrafterlp.cauldroninteract.utils.CauldronUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Cauldron;

/**
 * This file was created by VoxCrafter_LP!
 * Date: 13.07.2021
 * Time: 11:02
 * Project: CauldronInteract
 */

public class BlockDispenseListener extends CauldronUtils implements Listener {

    @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) {
        if(event.getBlock().getType() != Material.DISPENSER) return;

        final Dispenser dispenser = (Dispenser) event.getBlock().getState();
        final ItemStack item = event.getItem();

        //Gets the block in front of the dispenser.
        final BlockFace blockFace = this.getFacing(dispenser.getData());
        final Block dispenseBlock = event.getBlock().getRelative(blockFace);

        if(dispenseBlock.getType() != Material.CAULDRON && dispenseBlock.getType() != Material.WATER_CAULDRON
            && dispenseBlock.getType() != Material.LAVA_CAULDRON && dispenseBlock.getType() != Material.POWDER_SNOW_CAULDRON) return;

        if(item.getType() == Material.WATER_BUCKET ||
            item.getType() == Material.LAVA_BUCKET ||
            item.getType() == Material.POWDER_SNOW_BUCKET ||
            item.getType() == Material.BUCKET ||
            item.getType() == Material.GLASS_BOTTLE) {

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
}
