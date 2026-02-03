package com.spawnerx.listeners;

import com.spawnerx.SpawnerX;
import com.spawnerx.utils.SpawnerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Listener para eventos de colocação de spawner
 * Gerencia o sistema de stacking de spawners
 */
public class SpawnerPlaceListener implements Listener {
    
    private final SpawnerX plugin;
    
    public SpawnerPlaceListener(SpawnerX plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        Block blockPlaced = event.getBlockPlaced();
        Block blockAgainst = event.getBlockAgainst();
        
        // Verificar se é um spawner
        if (blockPlaced.getType() != Material.SPAWNER) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        
        // Verificar se é um spawner válido do plugin
        if (!plugin.getSpawnerManager().isValidSpawner(item)) {
            return;
        }
        
        // Obter informações do spawner do item
        EntityType entityType = plugin.getSpawnerManager().getSpawnerEntity(item);
        if (entityType == null) return;
        int itemStackSize = plugin.getSpawnerManager().getSpawnerStack(item);
        int maxStack = plugin.getConfigManager().getMaxStackSize();

        // Lógica de Stacking
        if (plugin.getConfigManager().isStackingEnabled() && blockAgainst.getType() == Material.SPAWNER) {
            CreatureSpawner targetSpawner = (CreatureSpawner) blockAgainst.getState();
            if (targetSpawner.getSpawnedType() == entityType) {
                PersistentDataContainer targetContainer = targetSpawner.getPersistentDataContainer();
                int currentStack = targetContainer.getOrDefault(plugin.getSpawnerManager().getStackKey(), PersistentDataType.INTEGER, 1);

                if (currentStack < maxStack) {
                    // Cancelar a colocação do bloco físico e apenas aumentar o stack do existente
                    event.setCancelled(true);

                    // Atualizar stack
                    int newStack = currentStack + itemStackSize;
                    int appliedStack = Math.min(newStack, maxStack);
                    int remaining = newStack - appliedStack;
                    targetContainer.set(plugin.getSpawnerManager().getStackKey(), PersistentDataType.INTEGER, appliedStack);
                    plugin.getSpawnerManager().applyStackToSpawner(targetSpawner, appliedStack);
                    targetSpawner.update();
                    plugin.getSpawnerManager().updateSpawnerHologram(blockAgainst, entityType, appliedStack);

                    consumeItem(player, item);
                    if (remaining > 0) {
                        ItemStack remainingItem = plugin.getSpawnerManager().createSpawner(entityType, remaining);
                        giveOrDrop(player, remainingItem);
                    }

                    String entityName = SpawnerUtils.getEntityDisplayName(entityType);
                    player.sendMessage(plugin.getLocaleManager().getMessage("spawner.stacked", 
                        "type", entityName, "amount", String.valueOf(appliedStack)));
                    return;
                }
            }
        }
        
        // Se não stackou, configurar o novo spawner normalmente
        CreatureSpawner spawner = (CreatureSpawner) blockPlaced.getState();
        spawner.setSpawnedType(entityType);
        
        // Salvar o owner e o stack inicial (1) no bloco
        PersistentDataContainer container = spawner.getPersistentDataContainer();
        container.set(plugin.getSpawnerManager().getOwnerKey(), PersistentDataType.STRING, player.getName());
        int appliedStack = Math.min(itemStackSize, maxStack);
        int remaining = itemStackSize - appliedStack;
        container.set(plugin.getSpawnerManager().getStackKey(), PersistentDataType.INTEGER, appliedStack);

        plugin.getSpawnerManager().applyStackToSpawner(spawner, appliedStack);
        spawner.update();
        plugin.getSpawnerManager().updateSpawnerHologram(blockPlaced, entityType, appliedStack);
        if (remaining > 0) {
            ItemStack remainingItem = plugin.getSpawnerManager().createSpawner(entityType, remaining);
            giveOrDrop(player, remainingItem);
        }
        
        // Mensagem de colocação
        String entityName = SpawnerUtils.getEntityDisplayName(entityType);
        player.sendMessage(plugin.getLocaleManager().getMessage("spawner.placed",
            "type", entityName));
    }

    private void consumeItem(Player player, ItemStack item) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }

    private void giveOrDrop(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }
}
