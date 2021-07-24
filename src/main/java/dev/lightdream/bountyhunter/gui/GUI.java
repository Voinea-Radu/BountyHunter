package dev.lightdream.bountyhunter.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public interface GUI extends InventoryHolder {

    void onInventoryClick(InventoryClickEvent event);

    void onInventoryClose(InventoryCloseEvent event);
}
