package com.voxcrafterlp.cauldroninteract;

import com.voxcrafterlp.cauldroninteract.listener.BlockDispenseListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.Buffer;

/**
 * This file was created by VoxCrafter_LP!
 * Date: 13.07.2021
 * Time: 11:01
 * Project: CauldronInteract
 */

public class CauldronInteract extends JavaPlugin {

    @Getter
    private static CauldronInteract instance;

    @Override
    public void onEnable() {
        instance = this;
        this.registerListener();
    }

    private void registerListener() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockDispenseListener(), this);
    }

}
