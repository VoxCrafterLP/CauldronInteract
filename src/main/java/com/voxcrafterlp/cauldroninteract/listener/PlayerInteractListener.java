package com.voxcrafterlp.cauldroninteract.listener;

import com.voxcrafterlp.cauldroninteract.CauldronInteract;
import org.bukkit.*;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerInteractListener implements Listener {

    final boolean useUpgradedDispensers = CauldronInteract.getInstance().getConfig().getBoolean("enable-dispenser-upgrade");
    final boolean useSmartDispensers = CauldronInteract.getInstance().getConfig().getBoolean("enable-smart-dispensers");

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.DISPENSER) return;
        if (!event.getPlayer().isSneaking()) return;

        final Dispenser dispenser = (Dispenser) event.getClickedBlock().getState();
        final NamespacedKey upgradeKey = CauldronInteract.getInstance().getDispenserUpgradedKey();
        final NamespacedKey smartKey = CauldronInteract.getInstance().getSmartDispenserKey();

        if (dispenser.getPersistentDataContainer().has(upgradeKey) || dispenser.getPersistentDataContainer().has(smartKey))
            return;
        if (!Tag.ITEMS_HOES.isTagged(event.getPlayer().getInventory().getItemInMainHand().getType())) return;

        if (useSmartDispensers && event.getPlayer().getInventory().getItemInOffHand().getType() == Material.OBSERVER) {
            event.getPlayer().getInventory().setItemInOffHand(
                    getConsumedItemStack(event.getPlayer().getInventory().getItemInOffHand()));

            dispenser.getPersistentDataContainer().set(smartKey, PersistentDataType.BOOLEAN, true);
            dispenser.update();
            finishUpgrade(event.getPlayer(), dispenser.getLocation());
        } else if (useUpgradedDispensers) {
            dispenser.getPersistentDataContainer().set(upgradeKey, PersistentDataType.BOOLEAN, true);
            dispenser.update();
            finishUpgrade(event.getPlayer(), dispenser.getLocation());
        }
    }

    private ItemStack getConsumedItemStack(final ItemStack itemStack) {
        if (itemStack.getAmount() == 1) return new ItemStack(Material.AIR);
        itemStack.setAmount(itemStack.getAmount() - 1);
        return itemStack;
    }

    private void finishUpgrade(final Player player, final Location dispenserLocation) {
        player.playSound(dispenserLocation, Sound.ITEM_HONEYCOMB_WAX_ON, 1, 1);
        player.spawnParticle(Particle.WAX_ON, dispenserLocation.add(0.5, 1.1, 0.5), 5);

        if (player.getGameMode() == GameMode.CREATIVE) return;

        final Damageable damageable = ((Damageable) Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()));
        damageable.setDamage(damageable.getDamage() + 1);
        player.getInventory().getItemInMainHand().setItemMeta(damageable);
    }

}
