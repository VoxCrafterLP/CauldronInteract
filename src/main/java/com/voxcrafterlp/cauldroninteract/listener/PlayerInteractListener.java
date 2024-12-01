package com.voxcrafterlp.cauldroninteract.listener;

import com.voxcrafterlp.cauldroninteract.CauldronInteract;
import org.bukkit.*;
import org.bukkit.block.Dispenser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.DISPENSER) return;
        if (!event.getPlayer().isSneaking()) return;

        final Dispenser dispenser = (Dispenser) event.getClickedBlock().getState();
        final NamespacedKey key = CauldronInteract.getInstance().getDispenserUpgradedKey();

        if (dispenser.getPersistentDataContainer().has(key)) return;
        if (!Tag.ITEMS_HOES.isTagged(event.getPlayer().getInventory().getItemInMainHand().getType())) return;

        dispenser.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
        dispenser.update();

        event.getPlayer().playSound(dispenser.getLocation(), Sound.ITEM_HONEYCOMB_WAX_ON, 1, 1);
        event.getPlayer().spawnParticle(Particle.WAX_ON, event.getClickedBlock().getLocation().add(0.5, 1.1, 0.5), 5);

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        final Damageable damageable = ((Damageable) Objects.requireNonNull(event.getPlayer().getInventory().getItemInMainHand().getItemMeta()));
        damageable.setDamage(damageable.getDamage() + 1);
        event.getPlayer().getInventory().getItemInMainHand().setItemMeta(damageable);
    }

}
