package com.spawnerx.managers;

import com.spawnerx.SpawnerX;
import com.spawnerx.utils.SpawnerUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.block.Block;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Gerenciador de spawners
 * Responsável por criar, modificar e gerenciar spawners
 */
public class SpawnerManager {
    
    private final SpawnerX plugin;
    private final NamespacedKey entityKey;
    private final NamespacedKey ownerKey;
    private final NamespacedKey stackKey;
    private final NamespacedKey baseSpawnCountKey;
    private final NamespacedKey baseMaxNearbyKey;
    private final NamespacedKey hologramKey;

    private static final double HOLOGRAM_Y_OFFSET = 1.2;
    
    public SpawnerManager(SpawnerX plugin) {
        this.plugin = plugin;
        this.entityKey = new NamespacedKey(plugin, "spawner_entity");
        this.ownerKey = new NamespacedKey(plugin, "spawner_owner");
        this.stackKey = new NamespacedKey(plugin, "spawner_stack");
        this.baseSpawnCountKey = new NamespacedKey(plugin, "spawner_base_spawn_count");
        this.baseMaxNearbyKey = new NamespacedKey(plugin, "spawner_base_max_nearby");
        this.hologramKey = new NamespacedKey(plugin, "spawner_hologram_id");
    }
    
    /**
     * Cria um item de spawner
     * @param entityType Tipo da entidade
     * @return ItemStack do spawner
     */
    public ItemStack createSpawner(EntityType entityType) {
        return createSpawner(entityType, 1);
    }

    /**
     * Cria um item de spawner com quantidade específica no stack (NBT)
     * @param entityType Tipo da entidade
     * @param stackSize Tamanho do stack
     * @return ItemStack do spawner
     */
    public ItemStack createSpawner(EntityType entityType, int stackSize) {
        ItemStack spawner = new ItemStack(Material.SPAWNER);
        BlockStateMeta meta = (BlockStateMeta) spawner.getItemMeta();
        
        if (meta != null) {
            // Configurar o tipo de entidade no spawner
            CreatureSpawner spawnerState = (CreatureSpawner) meta.getBlockState();
            spawnerState.setSpawnedType(entityType);
            meta.setBlockState(spawnerState);
            
            // Salvar dados persistentes (NBT)
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(entityKey, PersistentDataType.STRING, entityType.name());
            container.set(stackKey, PersistentDataType.INTEGER, stackSize);
            
            // Configurar nome e lore usando placeholders
            String entityName = SpawnerUtils.getEntityDisplayName(entityType);
            String rarity = plugin.getConfigManager().getRarity(entityType.name());
            String rarityClean = rarity.replaceAll("&[0-9a-fk-or]", "");
            String rarityColor = extractColorCode(rarity);
            
            // Nome do item
            String baseName = plugin.getConfigManager().getSpawnerDisplayName()
                .replace("{type}", entityName)
                .replace("{rarity}", rarityClean)
                .replace("{rarity_color}", rarityColor)
                .replace("{stack_size}", String.valueOf(stackSize));
            String displayName = stackSize + "x " + baseName;

            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(displayName));
            
            // Lore do item
            List<String> loreTemplate = plugin.getConfigManager().getSpawnerLore();
            List<Component> lore = loreTemplate.stream()
                .map(line -> LegacyComponentSerializer.legacyAmpersand().deserialize(line
                    .replace("{type}", entityName)
                    .replace("{rarity}", rarityClean)
                    .replace("{rarity_color}", rarityColor)
                    .replace("{stack_size}", String.valueOf(stackSize))))
                .collect(Collectors.toList());
            
            meta.lore(lore);
            
            spawner.setItemMeta(meta);
        }
        
        return spawner;
    }

    /**
     * Aplica o stack no spawner do mundo, escalando spawnCount e maxNearbyEntities
     */
    public void applyStackToSpawner(CreatureSpawner spawner, int stackSize) {
        if (spawner == null) return;
        PersistentDataContainer container = spawner.getPersistentDataContainer();

        int baseSpawn = getOrStoreBase(container, baseSpawnCountKey, spawner.getSpawnCount());
        int baseMaxNearby = getOrStoreBase(container, baseMaxNearbyKey, spawner.getMaxNearbyEntities());

        int safeStack = Math.max(1, stackSize);
        int newSpawn = Math.max(1, baseSpawn * safeStack);
        int newMaxNearby = Math.max(1, baseMaxNearby * safeStack);

        spawner.setSpawnCount(newSpawn);
        spawner.setMaxNearbyEntities(newMaxNearby);
    }

    /**
     * Atualiza ou cria o holograma do spawner
     */
    public void updateSpawnerHologram(Block block, EntityType entityType, int stackSize) {
        if (block == null || block.getType() != Material.SPAWNER) return;

        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        PersistentDataContainer container = spawner.getPersistentDataContainer();

        Component text = LegacyComponentSerializer.legacyAmpersand()
            .deserialize(formatSpawnerDisplayName(entityType, stackSize));

        TextDisplay display = getExistingHologram(block.getWorld(), container);
        Location location = block.getLocation().add(0.5, HOLOGRAM_Y_OFFSET, 0.5);

        if (display == null || display.isDead()) {
            display = spawnHologram(block.getWorld(), location, text);
            container.set(hologramKey, PersistentDataType.STRING, display.getUniqueId().toString());
        } else {
            display.text(text);
            display.teleport(location);
        }

        spawner.update();
    }

    /**
     * Remove o holograma do spawner, se existir
     */
    public void removeSpawnerHologram(Block block) {
        if (block == null || block.getType() != Material.SPAWNER) return;

        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        TextDisplay display = getExistingHologram(block.getWorld(), container);

        if (display != null) {
            display.remove();
        }

        container.remove(hologramKey);
        spawner.update();
    }

    /**
     * Formata o nome do spawner com prefixo de stack
     */
    public String formatSpawnerDisplayName(EntityType entityType, int stackSize) {
        String entityName = SpawnerUtils.getEntityDisplayName(entityType);
        String rarity = plugin.getConfigManager().getRarity(entityType.name());
        String rarityClean = rarity.replaceAll("&[0-9a-fk-or]", "");
        String rarityColor = extractColorCode(rarity);

        String baseName = plugin.getConfigManager().getSpawnerDisplayName()
            .replace("{type}", entityName)
            .replace("{rarity}", rarityClean)
            .replace("{rarity_color}", rarityColor)
            .replace("{stack_size}", String.valueOf(stackSize));

        return stackSize + "x " + baseName;
    }
    
    /**
     * Obtém o tipo de entidade do spawner (via NBT ou BlockState)
     * @param item ItemStack do spawner
     * @return EntityType ou null
     */
    public EntityType getSpawnerEntity(ItemStack item) {
        if (item == null || item.getType() != Material.SPAWNER) {
            return null;
        }
        
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        if (meta == null) return null;

        // Tentar via NBT primeiro
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(entityKey, PersistentDataType.STRING)) {
            try {
                return EntityType.valueOf(container.get(entityKey, PersistentDataType.STRING));
            } catch (Exception ignored) {}
        }

        // Fallback para BlockState
        CreatureSpawner spawnerState = (CreatureSpawner) meta.getBlockState();
        return spawnerState.getSpawnedType();
    }
    
    /**
     * Verifica se um item é um spawner válido do plugin
     * @param item ItemStack
     * @return true se for um spawner válido
     */
    public boolean isValidSpawner(ItemStack item) {
        if (item == null || item.getType() != Material.SPAWNER) {
            return false;
        }
        
        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer container = meta.getPersistentDataContainer();
            return container.has(entityKey, PersistentDataType.STRING);
        }
        
        return false;
    }

    /**
     * Obtém o stack salvo no item do spawner (NBT)
     * @param item ItemStack do spawner
     * @return stack salvo ou 1
     */
    public int getSpawnerStack(ItemStack item) {
        if (item == null || item.getType() != Material.SPAWNER) {
            return 1;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 1;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.getOrDefault(stackKey, PersistentDataType.INTEGER, 1);
    }
    
    public NamespacedKey getEntityKey() {
        return entityKey;
    }

    public NamespacedKey getOwnerKey() {
        return ownerKey;
    }

    public NamespacedKey getStackKey() {
        return stackKey;
    }

    private int getOrStoreBase(PersistentDataContainer container, NamespacedKey key, int current) {
        if (!container.has(key, PersistentDataType.INTEGER)) {
            container.set(key, PersistentDataType.INTEGER, current);
            return current;
        }
        return container.getOrDefault(key, PersistentDataType.INTEGER, current);
    }

    private TextDisplay getExistingHologram(World world, PersistentDataContainer container) {
        String id = container.get(hologramKey, PersistentDataType.STRING);
        if (id == null || id.isBlank()) return null;
        try {
            UUID uuid = UUID.fromString(id);
            org.bukkit.entity.Entity entity = world.getEntity(uuid);
            if (entity instanceof TextDisplay) {
                return (TextDisplay) entity;
            }
        } catch (IllegalArgumentException ignored) {}
        return null;
    }

    private TextDisplay spawnHologram(World world, Location location, Component text) {
        return world.spawn(location, TextDisplay.class, display -> {
            display.text(text);
            display.setBillboard(Billboard.CENTER);
            display.setGravity(false);
        });
    }

    private String extractColorCode(String rarity) {
        int idx = rarity.indexOf("&");
        if (idx >= 0 && idx + 1 < rarity.length()) {
            return rarity.substring(idx, idx + 2);
        }
        return "&f";
    }
}
