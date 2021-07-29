package dev.lightdream.bountyhunter.gui;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.Bounty;
import dev.lightdream.bountyhunter.dto.Item;
import dev.lightdream.bountyhunter.dto.User;
import dev.lightdream.bountyhunter.utils.ItemStackUtils;
import dev.lightdream.bountyhunter.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class BountyListGUI implements GUI {

    private final BountyHunter plugin;
    private final int page;

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
                case "back":
                    player.openInventory(new BountyListGUI(plugin, page - 1).getInventory());
                    break;
                case "next":
                    player.openInventory(new BountyListGUI(plugin, page + 1).getInventory());
                    break;
                case "bounty":
                    int id = (int) ((double) Utils.getNBT(item, "bounty"));
                    Bounty bounty = plugin.getDatabaseManager().getBounty(id);
                    if (bounty == null) {
                        return;
                    }
                    if (event.getClick().isRightClick()) {
                        if (bounty.player == player.getUniqueId()) {
                            plugin.getDatabaseManager().getBountyList().remove(bounty);
                            player.openInventory(getInventory());
                            return;
                        }
                    }
                    plugin.getBountyManager().takeBounty(id, player.getUniqueId());
                    break;
            }
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {

    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, Utils.color(plugin.getMessages().bountyListGUITitle));
        Utils.fillInventory(inventory, ItemStackUtils.makeItem(plugin.getGuiConfig().fillItem), plugin.getGuiConfig().fillItemPositions);

        List<Integer> availablePositions = new ArrayList<>();
        List<Bounty> bounties = new ArrayList<>();

        inventory.setItem(plugin.getGuiConfig().backItem.slot, Utils.setNBT(Utils.setNBT(ItemStackUtils.makeItem(plugin.getGuiConfig().backItem), "gui_use", "back"), "gui_protect", true));
        inventory.setItem(plugin.getGuiConfig().nextItem.slot, Utils.setNBT(Utils.setNBT(ItemStackUtils.makeItem(plugin.getGuiConfig().nextItem), "gui_use", "next"), "gui_protect", true));

        for (int i = 0; i < 54; i++) {
            if (inventory.getItem(i) == null) {
                availablePositions.add(i);
            }
        }

        for (int i = availablePositions.size() * page; i < availablePositions.size() * (page + 1); i++) {
            if (plugin.getDatabaseManager().getBountyList().size() > i) {
                bounties.add(plugin.getDatabaseManager().getBountyList().get(i));
            }
        }

        if (page == 0) {
            inventory.setItem(plugin.getGuiConfig().backItem.slot, null);
        }

        if (page == plugin.getDatabaseManager().getBountyList().size() / availablePositions.size()) {
            inventory.setItem(plugin.getGuiConfig().nextItem.slot, null);
        }

        Item headTemplate = plugin.getGuiConfig().bountyItem;
        List<UUID> onlinePlayers = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> onlinePlayers.add(player.getUniqueId()));

        for (int i = 0; i < Math.min(availablePositions.size(), bounties.size()); i++) {
            Item head = headTemplate.clone();
            Bounty bounty = bounties.get(i);
            User user = plugin.getDatabaseManager().getUser(bounty.target);
            boolean status = onlinePlayers.contains(user.uuid);

            head.headOwner = user.name;
            head.displayName = parse(head.displayName, user.name, bounty, status);
            head.lore = parse(head.lore, user.name, bounty, status);

            ItemStack item = ItemStackUtils.makeItem(head);
            item = Utils.setNBT(item, "gui_protect", true);
            item = Utils.setNBT(item, "gui_use", "bounty");
            item = Utils.setNBT(item, "bounty", bounty.id);

            inventory.setItem(availablePositions.get(i), item);
        }

        return inventory;
    }

    private String parse(String raw, String player, Bounty bounty, boolean status) {
        String parsed = raw;

        parsed = parsed.replace("%player%", player);
        parsed = parsed.replace("%description%", bounty.message);
        parsed = parsed.replace("%hunters%", String.valueOf(bounty.getHunters().size()));
        parsed = parsed.replace("%status%", status ? plugin.getMessages().active : plugin.getMessages().inactive);

        switch (bounty.rewardType) {
            case "item":
                List<ItemStack> rewards = new ArrayList<>();
                try {
                    rewards = Utils.itemStackArrayFromBase64(bounty.rewardItems);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < rewards.size(); i++) {
                    parsed = parsed.replace("%reward-" + (i + 1) + "%", (!rewards.get(i).hasItemMeta() ? rewards.get(i).getType().toString().toLowerCase() : rewards.get(i).getItemMeta().getDisplayName()) + " x" + rewards.get(i).getAmount());
                }
                for (int i = rewards.size(); i < 100; i++) {
                    parsed = parsed.replace("%reward-" + (i + 1) + "%", "");
                }
                break;
            case "money":
                parsed = parsed.replace("%reward-1%", "$" + bounty.rewardMoney);
                for (int i = 1; i < 100; i++) {
                    parsed = parsed.replace("%reward-" + (i + 1) + "%", "");
                }
                break;
        }


        return parsed;
    }

    private List<String> parse(List<String> raw, String player, Bounty bounty, boolean status) {
        List<String> parsed = new ArrayList<>();
        raw.forEach(line -> {
            String newLine = parse(line, player, bounty, status);
            if (!newLine.equals("- ")) {
                parsed.add(newLine);
            }
        });
        return parsed;
    }
}
