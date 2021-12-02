package com.voxcrafterlp.cauldroninteract;

import com.voxcrafterlp.cauldroninteract.listener.BlockDispenseListener;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This file was created by VoxCrafter_LP!
 * Date: 13.07.2021
 * Time: 11:01
 * Project: CauldronInteract
 */

@Getter
public class CauldronInteract extends JavaPlugin {

    @Getter
    private static CauldronInteract instance;
    private Metrics metrics;


    @Override
    public void onEnable() {
        instance = this;

        this.registerListener();
        this.loadMetrics();
        //this.initializeLegacyMaterialSupport();
        Bukkit.getConsoleSender().sendMessage("§aCauldronInteract v" + this.getDescription().getVersion() + " by VoxCrafter_LP enabled!");
    }

    private void registerListener() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockDispenseListener(), this);
    }

    /**
     * Loads bStats
     */
    private void loadMetrics() {
        final int pluginId = 12031;
        this.metrics = new Metrics(this, pluginId);
    }

    /**
     * Initializes legacy material support
     * @deprecated Isn't needed anymore
     */
    private void initializeLegacyMaterialSupport() {
        Bukkit.getConsoleSender().sendMessage("§7[§bCauldronInteract§7] §7Initializing legacy material support. This can take a while!");

        try {
            final String serverVersion = this.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            final Class<?> craftLegacy = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".legacy.CraftLegacy");
            craftLegacy.getMethod("init").invoke(null);
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage("§4An error occurred while initializing the plugin! Please report this!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getConsoleSender().sendMessage("§7[§bCauldronInteract§7] §aDone!");
    }

}
