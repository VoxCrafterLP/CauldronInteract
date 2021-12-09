package com.voxcrafterlp.cauldroninteract;

import com.google.common.collect.Lists;
import com.lezurex.githubversionchecker.CheckResult;
import com.lezurex.githubversionchecker.GithubVersionChecker;
import com.lezurex.githubversionchecker.ReleaseVersion;
import com.voxcrafterlp.cauldroninteract.listener.BlockDispenseListener;
import com.voxcrafterlp.cauldroninteract.listener.InventoryMoveItemListener;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

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

    private static final String consolePrefix = "§7[§bCauldronInteract§7] ";

    private List<Inventory> blockedInventories;

    @Override
    public void onEnable() {
        instance = this;
        this.blockedInventories = Lists.newCopyOnWriteArrayList();

        this.registerListener();
        this.loadMetrics();
        //this.initializeLegacyMaterialSupport();
        Bukkit.getConsoleSender().sendMessage(consolePrefix + "§av" + this.getDescription().getVersion() + " by VoxCrafter_LP enabled!");
        Bukkit.getScheduler().runTaskAsynchronously(this, this::checkForUpdates);
    }

    private void registerListener() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockDispenseListener(), this);
        pluginManager.registerEvents(new InventoryMoveItemListener(), this);
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
        Bukkit.getConsoleSender().sendMessage(consolePrefix + "§7Initializing legacy material support. This can take a while!");

        try {
            final String serverVersion = this.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            final Class<?> craftLegacy = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".legacy.CraftLegacy");
            craftLegacy.getMethod("init").invoke(null);
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage("§4An error occurred while initializing the plugin! Please report this!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getConsoleSender().sendMessage(consolePrefix + "§aDone!");
    }

    /**
     * Checks for plugin updates on GitHub.
     */
    private void checkForUpdates() {
        Bukkit.getConsoleSender().sendMessage(consolePrefix + "§aChecking for updates...");

        try {
            final ReleaseVersion currentVersion = new ReleaseVersion(this.getDescription().getVersion());
            final GithubVersionChecker versionChecker = new GithubVersionChecker("VoxCrafterLP", "CauldronInteract", currentVersion, false);
            final CheckResult checkResult = versionChecker.check();

            switch (checkResult.getVersionState()) {
                case OUTDATED:
                    Bukkit.getConsoleSender().sendMessage(consolePrefix + "§7There is a §anewer §7version available§8: §2" + checkResult.getVersion().toString());
                    break;
                case NEWER:
                case UP_TO_DATE:
                    Bukkit.getConsoleSender().sendMessage(consolePrefix + "§aYou are up to date.");
                    break;
            }
        } catch (Exception exception) {
            Bukkit.getConsoleSender().sendMessage(consolePrefix + "§cAn error occurred while checking for updates...");
        }
    }

}
