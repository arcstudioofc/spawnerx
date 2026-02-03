package com.spawnerx.managers;

import com.spawnerx.SpawnerX;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerenciador de configuração do plugin
 * Responsável por carregar e fornecer acesso às configurações
 */
public class ConfigManager {
    
    private final SpawnerX plugin;
    private FileConfiguration config;
    private FileConfiguration shopConfig;
    private java.io.File shopFile;
    
    public ConfigManager(SpawnerX plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Carrega a configuração do plugin
     */
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        loadShopConfig();
        migrateConfig();
    }

    private void loadShopConfig() {
        if (shopFile == null) {
            shopFile = new java.io.File(plugin.getDataFolder(), "shop.yml");
        }
        if (!shopFile.exists()) {
            plugin.saveResource("shop.yml", false);
        }
        shopConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(shopFile);
        java.io.InputStream defaultStream = plugin.getResource("shop.yml");
        if (defaultStream != null) {
            org.bukkit.configuration.file.YamlConfiguration defaults = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(
                new java.io.InputStreamReader(defaultStream, java.nio.charset.StandardCharsets.UTF_8));
            shopConfig.setDefaults(defaults);
            shopConfig.options().copyDefaults(true);
            saveShopConfig();
        }
    }

    private void saveShopConfig() {
        try {
            if (shopConfig != null && shopFile != null) {
                shopConfig.save(shopFile);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao salvar shop.yml: " + e.getMessage());
        }
    }

    /**
     * Migra configurações antigas para o novo formato
     */
    private void migrateConfig() {
        boolean changed = false;
        boolean shopChanged = false;

        // Migração: required-tool -> tools
        if (config.contains("break.required-tool")) {
            String oldTool = config.getString("break.required-tool");
            List<String> tools = config.getStringList("break.tools");
            if (tools.isEmpty()) {
                tools = new ArrayList<>();
                tools.add(oldTool);
                config.set("break.tools", tools);
            }
            config.set("break.required-tool", null);
            changed = true;
        }

        // Migração: require-silk-touch -> silk-touch
        if (config.contains("break.require-silk-touch")) {
            config.set("break.silk-touch", config.getBoolean("break.require-silk-touch"));
            config.set("break.require-silk-touch", null);
            changed = true;
        }

        // Migração: spawner.rarities -> rarities (correção de indentação)
        if (config.contains("spawner.rarities") && !config.contains("rarities")) {
            Object raritiesSection = config.get("spawner.rarities");
            if (raritiesSection != null) {
                config.set("rarities", raritiesSection);
                config.set("spawner.rarities", null);
                changed = true;
            }
        }

        // Migração: se shop ainda está em config.yml, copiar para shop.yml
        if (config.contains("spawner.shop") && (shopConfig == null || !shopConfig.contains("spawner.shop"))) {
            Object shopSection = config.get("spawner.shop");
            if (shopSection != null) {
                if (shopConfig != null) {
                    shopConfig.set("spawner.shop", shopSection);
                    shopChanged = true;
                }
            }
        }

        // Migração: spawner.shop.items -> spawner.shop.categories.DEFAULT.items (shop.yml)
        if (shopConfig != null && shopConfig.contains("spawner.shop.items")
            && !shopConfig.contains("spawner.shop.categories")) {
            org.bukkit.configuration.ConfigurationSection oldItems = shopConfig.getConfigurationSection("spawner.shop.items");
            if (oldItems != null) {
                String basePath = "spawner.shop.categories.DEFAULT";
                if (!shopConfig.contains(basePath + ".title")) {
                    shopConfig.set(basePath + ".title", "&eDefault Spawners");
                }
                if (!shopConfig.contains(basePath + ".icon-head")) {
                    shopConfig.set(basePath + ".icon-head", "");
                }
                for (String key : oldItems.getKeys(false)) {
                    int price = oldItems.getInt(key, -1);
                    if (price > 0) {
                        shopConfig.set(basePath + ".items." + key + ".price", price);
                    }
                }
                shopConfig.set("spawner.shop.items", null);
                shopChanged = true;
            }
        }

        if (changed) {
            plugin.saveConfig();
            plugin.getLogger().info("Configurações migradas para o novo formato (prompt.md)");
        }
        if (shopChanged) {
            saveShopConfig();
        }
    }
    
    /**
     * Obtém o locale configurado
     * @return String do locale (ex: pt_BR)
     */
    public String getLocale() {
        String locale = config.getString("locale", "pt_BR");
        return locale.replace("-", "_");
    }
    
    /**
     * Obtém a lista de ferramentas válidas para quebrar spawners
     * @return Lista de Materials
     */
    public List<Material> getValidTools() {
        List<String> toolNames = config.getStringList("break.tools");
        if (toolNames.isEmpty()) {
            List<Material> defaultTools = new ArrayList<>();
            defaultTools.add(Material.DIAMOND_PICKAXE);
            return defaultTools;
        }
        return toolNames.stream().map(name -> {
            try {
                return Material.valueOf(name);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).filter(java.util.Objects::nonNull).collect(Collectors.toList());
    }
    
    /**
     * Verifica se Toque Suave é necessário para quebrar spawners
     * @return true se necessário
     */
    public boolean requiresSilkTouch() {
        return config.getBoolean("break.silk-touch", true);
    }

    /**
     * Verifica se o item deve ir direto para o inventário ao quebrar
     * @return true se deve ir para o inventário
     */
    public boolean isBreakDropToInventory() {
        return config.getBoolean("break.drop-to-inventory", true);
    }

    /**
     * Obtém a chance de drop ao quebrar
     * @return Porcentagem de chance (0-100)
     */
    public double getBreakDropChance() {
        return config.getDouble("break.drop-chance", 50.0);
    }
    
    /**
     * Verifica se spawners podem dropar ao explodir
     * @return true se permitido
     */
    public boolean isExplosionDropAllowed() {
        return config.getBoolean("explosion.allow-drop", true);
    }

    /**
     * Verifica se o item deve ir direto para o inventário na explosão
     * @return true se deve ir para o inventário
     */
    public boolean isExplosionDropToInventory() {
        return config.getBoolean("explosion.drop-to-inventory", true);
    }
    
    /**
     * Obtém a chance de drop em explosão
     * @return Porcentagem de chance (0-100)
     */
    public double getExplosionDropChance() {
        return config.getDouble("explosion.drop-chance", 50.0);
    }
    
    /**
     * Verifica se permite quebrar sem requisitos
     * @return true se permitido
     */
    public boolean isAllowBreakWithoutRequirements() {
        return config.getBoolean("break.allow-break-without-requirements", true);
    }

    /**
     * Verifica se o empilhamento está ativado
     * @return true se ativado
     */
    public boolean isStackingEnabled() {
        return config.getBoolean("spawner.stacking-enabled", true);
    }

    /**
     * Obtém o tamanho máximo do stack
     * @return Tamanho máximo
     */
    public int getMaxStackSize() {
        return config.getInt("spawner.max-stack-size", 64);
    }
    
    /**
     * Obtém o formato do nome de exibição do spawner
     * @return String do formato
     */
    public String getSpawnerDisplayName() {
        return config.getString("spawner.display-name", "&8[{rarity_color}&l{rarity}&8] &e{type} Spawner");
    }
    
    /**
     * Obtém a lore do spawner
     * @return Lista de strings da lore
     */
    public java.util.List<String> getSpawnerLore() {
        return config.getStringList("spawner.lore");
    }

    /**
     * Distância para considerar spawner ativo (player próximo)
     * @return Distância em blocos
     */
    public int getSpawnerActiveDistance() {
        return config.getInt("spawner.distance-active", 64);
    }

    /**
     * Verifica se o shop de spawners está ativado
     */
    public boolean isShopEnabled() {
        return shopConfig != null && shopConfig.getBoolean("spawner.shop.enabled", false);
    }

    /**
     * Obtém a moeda do shop
     */
    public String getShopCurrency() {
        return shopConfig != null ? shopConfig.getString("spawner.shop.currency", "XP") : "XP";
    }

    /**
     * Obtém o material primário do filler do shop
     */
    public String getShopFillerPrimary() {
        if (shopConfig == null) return "BLUE_STAINED_GLASS_PANE";
        return shopConfig.getString("spawner.shop.navigation.filler-primary",
            shopConfig.getString("spawner.shop.navigation.filler", "BLUE_STAINED_GLASS_PANE"));
    }

    /**
     * Obtém o material secundário do filler do shop
     */
    public String getShopFillerSecondary() {
        if (shopConfig == null) return "LIGHT_BLUE_STAINED_GLASS_PANE";
        return shopConfig.getString("spawner.shop.navigation.filler-secondary",
            shopConfig.getString("spawner.shop.navigation.filler", "LIGHT_BLUE_STAINED_GLASS_PANE"));
    }

    /**
     * Obtém o material de destaque do filler do shop
     */
    public String getShopFillerAccent() {
        return shopConfig != null ? shopConfig.getString("spawner.shop.navigation.filler-accent", "SEA_LANTERN")
            : "SEA_LANTERN";
    }

    /**
     * Obtém o item do topo do shop
     */
    public String getShopTitleItem() {
        return shopConfig != null ? shopConfig.getString("spawner.shop.navigation.title-item", "SPAWNER") : "SPAWNER";
    }

    /**
     * Obtém uma cabeça de navegação do shop
     */
    public String getShopNavHead(String key) {
        return shopConfig != null ? shopConfig.getString("spawner.shop.navigation." + key, "") : "";
    }

    /**
     * Obtém a seção de categorias do shop
     */
    public org.bukkit.configuration.ConfigurationSection getShopCategoriesSection() {
        return shopConfig != null ? shopConfig.getConfigurationSection("spawner.shop.categories") : null;
    }
    
    /**
     * Obtém a raridade de um tipo de entidade
     * @param entityType Tipo da entidade
     * @return String formatada da raridade
     */
    public String getRarity(String entityType) {
        return config.getString("rarities." + entityType, config.getString("rarities.DEFAULT", "&fComum"));
    }
    
    /**
     * Obtém a configuração bruta
     * @return FileConfiguration
     */
    public FileConfiguration getConfig() {
        return config;
    }
}
