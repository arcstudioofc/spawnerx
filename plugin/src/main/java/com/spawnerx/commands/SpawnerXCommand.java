package com.spawnerx.commands;

import com.spawnerx.SpawnerX;
import com.spawnerx.utils.SpawnerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Executor de comandos do SpawnerX
 * Gerencia todos os subcomandos do plugin
 */
public class SpawnerXCommand implements CommandExecutor, TabCompleter {
    
    private final SpawnerX plugin;
    
    public SpawnerXCommand(SpawnerX plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Comando sem argumentos - mostrar ajuda
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "reload":
                return handleReload(sender);
                
            case "give":
                return handleGive(sender, args);

            case "shop":
                return handleShop(sender);
                
            case "help":
                sendHelp(sender);
                return true;
                
            default:
                sender.sendMessage(plugin.getLocaleManager().getMessage("general.invalid-usage", 
                    "usage", "/" + label + " [reload|give|shop|help]"));
                return true;
        }
    }
    
    /**
     * Gerencia o comando reload
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("spawnerx.admin")) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.no-permission"));
            return true;
        }
        
        try {
            plugin.reload();
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.reload.success"));
        } catch (Exception e) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.reload.error"));
            plugin.getLogger().severe("Erro ao recarregar configuração: " + e.getMessage());
        }
        
        return true;
    }
    
    /**
     * Gerencia o comando give
     */
    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("spawnerx.admin")) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.no-permission"));
            return true;
        }
        
        // Validar argumentos
        if (args.length < 3) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("general.invalid-usage",
                "usage", "/spawnerx give <player> <mob>"));
            return true;
        }
        
        // Obter jogador
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.give.player-not-found",
                "player", args[1]));
            return true;
        }
        
        // Obter tipo de mob
        EntityType entityType = SpawnerUtils.getEntityType(args[2]);
        if (entityType == null || !SpawnerUtils.isMobEntityType(entityType)) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.give.invalid-mob",
                "type", args[2]));
            return true;
        }
        
        // Obter quantidade (opcional)
        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
                if (amount < 1) amount = 1;
            } catch (NumberFormatException e) {
                amount = 1;
            }
        }
        
        // Criar e dar o spawner
        ItemStack spawner = plugin.getSpawnerManager().createSpawner(entityType);
        spawner.setAmount(amount);
        target.getInventory().addItem(spawner);
        
        // Mensagens
        String entityName = SpawnerUtils.getEntityDisplayName(entityType);
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.give.success",
            "amount", String.valueOf(amount),
            "type", entityName,
            "player", target.getName()));
        
        target.sendMessage(plugin.getLocaleManager().getMessage("commands.give.received",
            "amount", String.valueOf(amount),
            "type", entityName));
        
        return true;
    }

    /**
     * Gerencia o comando shop
     */
    private boolean handleShop(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("shop.only-players"));
            return true;
        }
        if (!sender.hasPermission("spawnerx.shop")) {
            sender.sendMessage(plugin.getLocaleManager().getMessage("commands.no-permission"));
            return true;
        }
        plugin.getSpawnerShopManager().openShop((Player) sender);
        return true;
    }
    
    /**
     * Envia a mensagem de ajuda
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.help.header"));
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.help.reload"));
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.help.give"));
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.help.shop"));
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.help.help"));
        sender.sendMessage(plugin.getLocaleManager().getMessage("commands.help.footer"));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Subcomandos
            completions.addAll(Arrays.asList("reload", "give", "shop", "help"));
            return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        
        if (args[0].equalsIgnoreCase("give")) {
            if (args.length == 2) {
                // Jogadores online
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
            
            if (args.length == 3) {
                // Tipos de mobs
                return Arrays.stream(EntityType.values())
                    .filter(SpawnerUtils::isMobEntityType)
                    .map(EntityType::name)
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
            }
            
            if (args.length == 4) {
                // Quantidade
                return Arrays.asList("1", "2", "5", "10", "32", "64");
            }
        }
        
        return completions;
    }
}
