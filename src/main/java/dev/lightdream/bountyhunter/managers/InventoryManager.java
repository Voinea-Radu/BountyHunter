package dev.lightdream.bountyhunter.managers;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.gui.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class InventoryManager implements Listener {

    private final BountyHunter plugin;

    public InventoryManager(BountyHunter plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        System.out.println("Event listener registered");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            ((GUI) event.getInventory().getHolder()).onInventoryClick(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() != null && event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof GUI) {
            System.out.println("Forwarding the event");
            ((GUI) event.getInventory().getHolder()).onInventoryClose(event);
        }
    }


}
