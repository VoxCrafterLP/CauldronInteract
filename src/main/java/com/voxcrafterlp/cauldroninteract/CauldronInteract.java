package com.voxcrafterlp.cauldroninteract;

import com.lezurex.githubversionchecker.CheckResult;
import com.lezurex.githubversionchecker.GithubVersionChecker;
import com.lezurex.githubversionchecker.ReleaseVersion;
import com.voxcrafterlp.cauldroninteract.listener.BlockDispenseListener;
import com.voxcrafterlp.cauldroninteract.listener.InventoryMoveItemListener;
import com.voxcrafterlp.cauldroninteract.listener.PlayerInteractListener;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

@Getter
public class CauldronInteract extends JavaPlugin {

    @Getter
    private static CauldronInteract instance;
    private Metrics metrics;

    private static final String consolePrefix = "§7[§bCauldronInteract§7] ";

    private HashSet<Inventory> blockedInventories;

    @Override
    public void onEnable() {
        instance = this;
        this.blockedInventories = new HashSet<>();

        this.saveDefaultConfig();
        this.registerListener();

        if (this.getConfig().getBoolean("enable-metrics"))
            this.loadMetrics();

        this.printConsoleInformation();

        if (this.getConfig().getBoolean("enable-version-checker"))
            Bukkit.getScheduler().runTaskAsynchronously(this, this::checkForUpdates);
    }

    private void registerListener() {
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BlockDispenseListener(), this);
        pluginManager.registerEvents(new InventoryMoveItemListener(), this);

        if (CauldronInteract.getInstance().getConfig().getBoolean("enable-dispenser-upgrade"))
            pluginManager.registerEvents(new PlayerInteractListener(), this);
    }

    private void printConsoleInformation() {
        Bukkit.getConsoleSender().sendMessage(consolePrefix + (this.getConfig().getBoolean("enable-metrics") ?
                "§7Metrics are turned on. Disable metrics in the config to opt out of bStats metrics collection." :
                "§7Metrics are turned off."));
        Bukkit.getConsoleSender().sendMessage(consolePrefix + "§av" + this.getDescription().getVersion() + " by VoxCrafter_LP enabled!");
    }

    /**
     * Loads bStats
     */
    private void loadMetrics() {
        final int pluginId = 12031;
        this.metrics = new Metrics(this, pluginId);
        this.metrics.addCustomChart(new SimplePie("servers_using_upgraded_dispensers", () ->
                String.valueOf(getConfig().getBoolean("enable-dispenser-upgrade"))));
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
                    Bukkit.getConsoleSender().sendMessage(consolePrefix + "§7There is a §4newer §7version available§8: §c" + checkResult.getVersion().toString());
                    Bukkit.getConsoleSender().sendMessage(consolePrefix + "§7You can download the newest version here: §c" + checkResult.getPageLink());
                    Bukkit.getConsoleSender().sendMessage(consolePrefix + "§7Alternatively, you can download it on §2Modrinth§7: §chttps://modrinth.com/plugin/cauldroninteract/versions");
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

    public NamespacedKey getDispenserUpgradedKey() {
        return new NamespacedKey(CauldronInteract.getInstance(), "dispenser-upgraded");
    }

}
