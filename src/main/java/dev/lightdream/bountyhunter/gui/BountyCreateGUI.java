package dev.lightdream.bountyhunter.gui;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.Item;
import dev.lightdream.bountyhunter.dto.User;
import dev.lightdream.bountyhunter.utils.ItemStackUtils;
import dev.lightdream.bountyhunter.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BountyCreateGUI implements GUI {
    private final BountyHunter plugin;
    private final int page;
    private final User player;
    private User target;

    public BountyCreateGUI(BountyHunter plugin, User player, int page) {
        this.plugin = plugin;
        this.player = player;
        this.page = page;
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (item == null) {
            return;
        }
        if (item.getType().equals(Material.AIR)) {
            return;
        }

        Object guiProtect = Utils.getNBT(item, "gui_protect");
        Object guiUse = Utils.getNBT(item, "gui_use");

        if (guiProtect != null && (Boolean) guiProtect) {
            event.setCancelled(true);
        }

        if (guiUse != null) {
            switch ((String) guiUse) {
                case "select_player":
                    UUID uuid = UUID.fromString((String) Utils.getNBT(item, "player"));
                    target = plugin.getDatabaseManager().getUser(uuid);
                    player.openInventory(getInventory());
                    break;
                case "place_money_bounty":
                    double amount = (double) Utils.getNBT(item, "money");
                    plugin.getBountyManager().placeBounty(this.player, target, amount, "");
                    player.closeInventory();
                    break;
                case "place_item_bounty":
                    Bukkit.getPlayer(this.player.uuid).openInventory(new BountyPlaceGUI(plugin, this.player, target, "").getInventory());
                    player.closeInventory();
                    break;
            }
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    public Inventory getInventory() {
        if (target != null) {
            Inventory inventory = Bukkit.createInventory(this, 54, Utils.color(plugin.getMessages().bountyCreateGUITitle));
            Utils.fillInventory(inventory, ItemStackUtils.makeItem(plugin.getGuiConfig().fillItem), plugin.getGuiConfig().fillItemPositions);

            inventory.setItem(plugin.getGuiConfig().bountyTypeItems.slot, Utils.setNBT(Utils.setNBT(ItemStackUtils.makeItem(plugin.getGuiConfig().bountyTypeItems), "gui_use", "place_item_bounty"), "gui_protect", true));
            plugin.getGuiConfig().bountyTypeMoney.keySet().forEach(key -> {
                Item item = plugin.getGuiConfig().bountyTypeMoney.get(key);
                inventory.setItem(item.slot, Utils.setNBT(Utils.setNBT(Utils.setNBT(ItemStackUtils.makeItem(item), "gui_use", "place_money_bounty"), "gui_protect", true), "money", key));
            });

            return inventory;
        }

        Inventory inventory = Bukkit.createInventory(this, 54, Utils.color(plugin.getMessages().bountyCreateGUITitle));
        Utils.fillInventory(inventory, ItemStackUtils.makeItem(plugin.getGuiConfig().fillItem), plugin.getGuiConfig().fillItemPositions);

        List<Integer> availablePositions = new ArrayList<>();
        List<Player> players = new ArrayList<>();

        inventory.setItem(plugin.getGuiConfig().backItem.slot, Utils.setNBT(Utils.setNBT(ItemStackUtils.makeItem(plugin.getGuiConfig().backItem), "gui_use", "back"), "gui_protect", true));
        inventory.setItem(plugin.getGuiConfig().nextItem.slot, Utils.setNBT(Utils.setNBT(ItemStackUtils.makeItem(plugin.getGuiConfig().nextItem), "gui_use", "back"), "gui_protect", true));

        for (int i = 0; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                availablePositions.add(i);
            }
        }

        for (int i = availablePositions.size() * page; i < availablePositions.size() * (page + 1); i++) {
            if (Bukkit.getOnlinePlayers().size() > i) {
                players.add(new ArrayList<Player>(Bukkit.getOnlinePlayers()).get(0));
            }
        }

        if (page == 0) {
            inventory.setItem(plugin.getGuiConfig().backItem.slot, null);
        }

        if (page == plugin.getDatabaseManager().getBountyList().size() / availablePositions.size()) {
            inventory.setItem(plugin.getGuiConfig().nextItem.slot, null);
        }

        Item headTemplate = plugin.getGuiConfig().bountyCreateSelectItem;

        for (int i = 0; i < Math.min(availablePositions.size(), players.size()); i++) {
            Item head = headTemplate.clone();
            Player player = players.get(i);

            head.headOwner = player.getName();
            head.displayName = Utils.color(head.displayName.replace("%player%", player.getName()));
            head.lore = Utils.color(head.lore);

            ItemStack item = ItemStackUtils.makeItem(head);
            item = Utils.setNBT(item, "gui_protect", true);
            item = Utils.setNBT(item, "gui_use", "select_player");
            item = Utils.setNBT(item, "player", player.getUniqueId());

            inventory.setItem(availablePositions.get(i), item);
        }

        return inventory;
    }
}
