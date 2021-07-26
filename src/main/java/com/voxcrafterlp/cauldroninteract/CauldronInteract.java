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
    }

    private void registerListener() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        //pluginManager.registerEvents(new BlockDispenseListener(), this);
    }

    /**
     * Loads bStats
     */
    private void loadMetrics() {
        final int pluginId = 12031;
        this.metrics = new Metrics(this, pluginId);
    }

}
