package net.daechler.armortier;

import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class ArmorTier extends JavaPlugin implements Listener {
    private HashMap<Player, Integer> highestTiers = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(ChatColor.GREEN + getName() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + getName() + " has been disabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerTier(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            updatePlayerTier(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        updatePlayerTier(player);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();

            // If players are in different tiers, cancel the event
            if (!highestTiers.get(damager).equals(highestTiers.get(damaged))) {
                damager.sendMessage(ChatColor.RED + "You can only attack players in your tier!");
                event.setCancelled(true);
            }
        }
    }

    private void updatePlayerTier(Player player) {
        int currentTier = highestTiers.getOrDefault(player, 1);
        int newTier = getTier(player);
        if (newTier > currentTier) {
            highestTiers.put(player, newTier);
        }
    }

    private int getTier(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        int highestTier = 1;  // Default tier

        for (ItemStack piece : armor) {
            if (piece != null) {
                Material type = piece.getType();
                if (type.name().contains("LEATHER") || type.name().contains("CHAINMAIL")) {
                    highestTier = Math.max(highestTier, 1);
                } else if (type.name().contains("IRON") || type.name().contains("DIAMOND")) {
                    highestTier = Math.max(highestTier, 2);
                } else if (type.name().contains("NETHERITE")) {
                    highestTier = Math.max(highestTier, 3);
                }
            }
        }

        return highestTier;
    }
}
