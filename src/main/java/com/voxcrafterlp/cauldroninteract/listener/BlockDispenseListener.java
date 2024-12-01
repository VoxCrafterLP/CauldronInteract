package com.voxcrafterlp.cauldroninteract.listener;

import com.voxcrafterlp.cauldroninteract.CauldronInteract;
import com.voxcrafterlp.cauldroninteract.utils.CauldronUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class BlockDispenseListener extends CauldronUtils implements Listener {

    private final ItemStack waterBottleItemStack;

    public BlockDispenseListener() {
        this.waterBottleItemStack = super.getWaterBottleItemStack();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDispense(BlockDispenseEvent event) {
        if (event.getBlock().getType() != Material.DISPENSER) return;

        final ItemStack item = event.getItem();

        // Gets the block in front of the dispenser.
        final BlockFace blockFace = this.getFacing(event.getBlock());
        final Block dispenseBlock = event.getBlock().getRelative(blockFace);

        if (dispenseBlock.getType() != Material.CAULDRON && dispenseBlock.getType() != Material.WATER_CAULDRON
                && dispenseBlock.getType() != Material.LAVA_CAULDRON && dispenseBlock.getType() != Material.POWDER_SNOW_CAULDRON)
            return;

        if (item.getType() != Material.WATER_BUCKET &&
                item.getType() != Material.LAVA_BUCKET &&
                item.getType() != Material.POWDER_SNOW_BUCKET &&
                item.getType() != Material.BUCKET &&
                item.getType() != Material.GLASS_BOTTLE &&
                !item.equals(this.waterBottleItemStack))
            return;

        event.setCancelled(true);

        switch (dispenseBlock.getType()) {
            case CAULDRON:
                if (item.getType() == Material.BUCKET || item.getType() == Material.GLASS_BOTTLE) {
                    event.setCancelled(false);
                    return;
                }

                // Sets the correct cauldron type for every fluid.
                switch (item.getType()) {
                    case WATER_BUCKET:
                        if (!CauldronInteract.getInstance().getConfig().getBoolean("enable-water-cauldron")) {
                            event.setCancelled(false);
                            return;
                        }

                        dispenseBlock.setType(Material.WATER_CAULDRON);
                        dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_FILL, 1, 1);
                        break;
                    case LAVA_BUCKET:
                        if (!CauldronInteract.getInstance().getConfig().getBoolean("enable-lava-cauldron")) {
                            event.setCancelled(false);
                            return;
                        }

                        dispenseBlock.setType(Material.LAVA_CAULDRON);
                        dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_FILL_LAVA, 1, 1);
                        break;
                    case POWDER_SNOW_BUCKET:
                        if (!CauldronInteract.getInstance().getConfig().getBoolean("enable-powder-snow-cauldron")) {
                            event.setCancelled(false);
                            return;
                        }

                        dispenseBlock.setType(Material.POWDER_SNOW_CAULDRON);
                        dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_FILL_POWDER_SNOW, 1, 1);
                        break;
                    case POTION:
                        if (!CauldronInteract.getInstance().getConfig().getBoolean("enable-emptying-bottles")) {
                            event.setCancelled(false);
                            return;
                        }

                        dispenseBlock.setType(Material.WATER_CAULDRON);

                        this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.GLASS_BOTTLE), false);
                        dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BOTTLE_EMPTY, 1, 1);
                        return;
                }

                this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.BUCKET), false);

                if (dispenseBlock.getType() == Material.WATER_CAULDRON || dispenseBlock.getType() == Material.POWDER_SNOW_CAULDRON) {
                    // Fills the cauldron to the maximum level (3).
                    this.updateCauldronWaterLevel(dispenseBlock, 3, CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY);
                } else {
                    // Triggers a CauldronLevelChangeEvent for the lava bucket
                    Bukkit.getPluginManager().callEvent(new CauldronLevelChangeEvent(dispenseBlock, null,
                            CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY, dispenseBlock.getState()));
                }
                break;
            case WATER_CAULDRON:
                if (!CauldronInteract.getInstance().getConfig().getBoolean("enable-water-cauldron") && item.getType() == Material.WATER_BUCKET) {
                    event.setCancelled(false);
                    return;
                }

                if (!CauldronInteract.getInstance().getConfig().getBoolean("enable-filling-bottles") && item.getType() == Material.GLASS_BOTTLE) {
                    event.setCancelled(false);
                    return;
                }

                if (!CauldronInteract.getInstance().getConfig().getBoolean("enable-emptying-bottles") && item.getType() == Material.POTION) {
                    event.setCancelled(false);
                    return;
                }

                final Levelled cauldron = (Levelled) dispenseBlock.getBlockData();
                if (!this.isFull(cauldron) && item.getType() == Material.BUCKET) {
                    event.setCancelled(false);
                    return;
                }

                final int waterLevel = this.getCauldronWaterLevel(dispenseBlock);

                switch (item.getType()) {
                    case BUCKET:
                        this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.WATER_BUCKET), true);
                        dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1, 1);
                        break;
                    case GLASS_BOTTLE:
                        if (waterLevel > 1)
                            this.updateCauldronWaterLevel(dispenseBlock, waterLevel - 1,
                                    CauldronLevelChangeEvent.ChangeReason.BOTTLE_FILL);

                        this.modifyDispenserInventory(event.getBlock(), item, this.waterBottleItemStack.clone(), (waterLevel == 1));
                        dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BOTTLE_FILL, 1, 1);
                        return;
                    case POTION:
                        if (waterLevel == 3) {
                            event.setCancelled(false);
                            return;
                        }
                        this.updateCauldronWaterLevel(dispenseBlock, waterLevel + 1,
                                CauldronLevelChangeEvent.ChangeReason.BOTTLE_EMPTY);

                        this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.GLASS_BOTTLE), false);
                        dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BOTTLE_EMPTY, 1, 1);
                        return;
                    default:
                        event.setCancelled(false);
                        return;
                }

                // Triggers a CauldronLevelChangeEvent
                Bukkit.getPluginManager().callEvent(new CauldronLevelChangeEvent(dispenseBlock, null,
                        CauldronLevelChangeEvent.ChangeReason.BUCKET_FILL, dispenseBlock.getState()));
                break;
            case POWDER_SNOW_CAULDRON:
                if (!CauldronInteract.getInstance().getConfig().getBoolean("enable-powder-snow-cauldron")) {
                    event.setCancelled(false);
                    return;
                }

                if (item.getType() != Material.BUCKET && item.getType() != Material.GLASS_BOTTLE) {
                    event.setCancelled(false);
                    return;
                }

                if (item.getType() != Material.BUCKET) {
                    event.setCancelled(false);
                    return;
                }

                this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.POWDER_SNOW_BUCKET), true);
                dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW, 1, 1);

                // Triggers a CauldronLevelChangeEvent
                Bukkit.getPluginManager().callEvent(new CauldronLevelChangeEvent(dispenseBlock, null,
                        CauldronLevelChangeEvent.ChangeReason.BUCKET_FILL, dispenseBlock.getState()));
                break;
            case LAVA_CAULDRON:
                if (!CauldronInteract.getInstance().getConfig().getBoolean("enable-lava-cauldron")) {
                    event.setCancelled(false);
                    return;
                }

                if (item.getType() != Material.BUCKET) {
                    event.setCancelled(false);
                    return;
                }

                this.modifyDispenserInventory(event.getBlock(), item, new ItemStack(Material.LAVA_BUCKET), true);
                dispenseBlock.getWorld().playSound(dispenseBlock.getLocation(), Sound.ITEM_BUCKET_EMPTY_LAVA, 1, 1);

                // Triggers a CauldronLevelChangeEvent
                Bukkit.getPluginManager().callEvent(new CauldronLevelChangeEvent(dispenseBlock, null,
                        CauldronLevelChangeEvent.ChangeReason.BUCKET_FILL, dispenseBlock.getState()));
                break;
        }
    }

}
