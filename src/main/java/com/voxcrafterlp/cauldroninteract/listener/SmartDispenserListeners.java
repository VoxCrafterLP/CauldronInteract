package com.voxcrafterlp.cauldroninteract.listener;

import com.voxcrafterlp.cauldroninteract.CauldronInteract;
import com.voxcrafterlp.cauldroninteract.utils.CauldronUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class SmartDispenserListeners extends CauldronUtils implements Listener {

    private final HashMap<Location, Location> dispenserCache = new HashMap<>();
    private final BlockFace[] neighbouringFaces = new BlockFace[]{BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN};

    @EventHandler
    public void onCauldronLevelChange(CauldronLevelChangeEvent event) {
        if (event.getReason() != CauldronLevelChangeEvent.ChangeReason.NATURAL_FILL) return;

        Bukkit.getScheduler().runTaskLater(CauldronInteract.getInstance(), () -> {
            if (event.isCancelled()) return;
            final Block cauldron = event.getBlock();

            if (cauldron.getType() == Material.LAVA_CAULDRON
                    || cauldron.getType() == Material.POWDER_SNOW_CAULDRON
                    || (cauldron.getType() == Material.WATER_CAULDRON && getCauldronWaterLevel(cauldron) == 3)) {


                final Dispenser dispenser = this.getDispenser(event.getBlock());
                if (dispenser == null) return;

                final Optional<ItemStack> bucket = this.getEmptyBucket(dispenser);
                if (bucket.isEmpty()) return;

                Bukkit.getPluginManager().callEvent(new BlockDispenseEvent(dispenser.getBlock(), bucket.get(), new Vector(0, 0, 0)));
            }
        }, 1);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        dispenserCache.values().removeIf(location -> location.equals(event.getBlock().getLocation()));
    }

    private Dispenser getDispenser(final Block cauldron) {
        if (dispenserCache.containsKey(cauldron.getLocation())) {
            if (dispenserCache.get(cauldron.getLocation()).getBlock().getType() == Material.DISPENSER)
                return (Dispenser) dispenserCache.get(cauldron.getLocation()).getBlock().getState();

            dispenserCache.remove(cauldron.getLocation());
        }

        final Optional<BlockFace> dispenserFace = Arrays.stream(neighbouringFaces)
                .filter(face -> cauldron.getRelative(face).getType() == Material.DISPENSER)
                .filter(face -> ((Dispenser) cauldron.getRelative(face).getState())
                        .getPersistentDataContainer().has(CauldronInteract.getInstance().getSmartDispenserKey()))
                .filter(face -> cauldron.getRelative(face).getRelative(getFacing(cauldron.getRelative(face)))
                        .getLocation().equals(cauldron.getLocation()))
                .findFirst();
        if (dispenserFace.isEmpty()) return null;

        final Dispenser dispenser = (Dispenser) cauldron.getRelative(dispenserFace.get()).getState();
        dispenserCache.put(cauldron.getLocation(), dispenser.getLocation());

        return dispenser;
    }

    private Optional<ItemStack> getEmptyBucket(Dispenser dispenser) {
        return Arrays.stream(dispenser.getInventory().getContents())
                .filter(item -> item != null && item.getType() == Material.BUCKET)
                .findFirst()
                .map(item -> {
                    ItemStack bucket = item.clone();
                    bucket.setAmount(1);
                    return bucket;
                });
    }

}
