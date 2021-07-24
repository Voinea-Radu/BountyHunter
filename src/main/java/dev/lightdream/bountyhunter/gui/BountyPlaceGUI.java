package dev.lightdream.bountyhunter.gui;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BountyPlaceGUI implements GUI {

    private final BountyHunter plugin;
    private final OfflinePlayer player;
    private final OfflinePlayer target;
    private final String message;

    @Override
    public void onInventoryClick(InventoryClickEvent event) {

    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
        System.out.println("Event triggered");
        List<ItemStack> reward = new ArrayList<>();

        for (ItemStack item : event.getInventory().getContents()) {
            if (item != null) {
                reward.add(item);
            }
        }

        if (!(checkItems(reward))) {
            plugin.getMessageManager().sendMessage(player, plugin.getMessages().invalidNameOrLore);
            return;
        }

        plugin.getBountyManager().placeBounty(player, target, reward, message);
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, Utils.color(plugin.getMessages().bountyPlaceGuiTitle));
        return inventory;
    }

    public boolean checkItems(List<ItemStack> items) {
        for (ItemStack item : items) {
            String itemName = !item.hasItemMeta() ? item.getType().toString().toLowerCase() : item.getItemMeta().getDisplayName();
            List<String> itemLore = !item.hasItemMeta() ? new ArrayList<>() : item.getItemMeta().getLore();

            for (String check : plugin.getConfiguration().disallowedNames) {
                if (itemName.contains(check)) {
                    return false;
                }
            }
            for (String check : plugin.getConfiguration().getDisallowedLores) {
                for (String line : itemLore) {
                    if (line.contains(check)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
