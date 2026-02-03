package com.spawnerx;

import com.spawnerx.commands.SpawnerXCommand;
import com.spawnerx.listeners.*;
import com.spawnerx.managers.ConfigManager;
import com.spawnerx.managers.LocaleManager;
import com.spawnerx.managers.SpawnerManager;
import com.spawnerx.managers.SpawnerShopManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe principal do plugin SpawnerX
 * Gerencia o ciclo de vida do plugin e inicialização de componentes
 */
public class SpawnerX extends JavaPlugin {
    
    private static SpawnerX instance;
    private ConfigManager configManager;
    private LocaleManager localeManager;
    private SpawnerManager spawnerManager;
    private SpawnerShopManager spawnerShopManager;
    
    @Override
    public void onEnable() {
        instance = this;

        this.configManager = new ConfigManager(this);
        this.localeManager = new LocaleManager(this);
        this.spawnerManager = new SpawnerManager(this);
        this.spawnerShopManager = new SpawnerShopManager(this);

        configManager.loadConfig();
        localeManager.loadLocale();

        SpawnerXCommand commandExecutor = new SpawnerXCommand(this);
        getCommand("spawnerx").setExecutor(commandExecutor);
        getCommand("spawnerx").setTabCompleter(commandExecutor);

        getServer().getPluginManager().registerEvents(new SpawnerBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerExplosionListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerStackListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerDistanceListener(this), this);
        getServer().getPluginManager().registerEvents(new SpawnerShopListener(this), this);

        getLogger().info(" ");
        getLogger().info("   _____                                   __   __");
        getLogger().info("  / ____|                                  \\ \\ / /");
        getLogger().info(" | (___  _ __   __ ___      ___ __   ___ _ _\\ V / ");
        getLogger().info("  \\___ \\| '_ \\ / _` \\ \\ /\\ / / '_ \\ / _ \\ '__> <  ");
        getLogger().info("  ____) | |_) | (_| |\\ V  V /| | | |  __/ | / . \\ ");
        getLogger().info(" |_____/| .__/ \\__,_| \\_/\\_/ |_| |_|\\___|_|/_/ \\_\\");
        getLogger().info("        | |                                       ");
        getLogger().info("        |_|                                       ");
        getLogger().info(" ");
        getLogger().info(" SpawnerX v1.0.0");

    }

    
    @Override
    public void onDisable() {
        getLogger().info("SpawnerX desabilitado!");
    }
    
    /**
     * Obtém a instância do plugin
     * @return Instância do SpawnerX
     */
    public static SpawnerX getInstance() {
        return instance;
    }
    
    /**
     * Obtém o gerenciador de configuração
     * @return ConfigManager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * Obtém o gerenciador de locale
     * @return LocaleManager
     */
    public LocaleManager getLocaleManager() {
        return localeManager;
    }
    
    /**
     * Obtém o gerenciador de spawners
     * @return SpawnerManager
     */
    public SpawnerManager getSpawnerManager() {
        return spawnerManager;
    }

    /**
     * Obtém o gerenciador do shop de spawners
     */
    public SpawnerShopManager getSpawnerShopManager() {
        return spawnerShopManager;
    }
    
    /**
     * Recarrega todas as configurações do plugin
     */
    public void reload() {
        configManager.loadConfig();
        localeManager.loadLocale();
    }
}
