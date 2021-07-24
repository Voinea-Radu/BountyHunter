package dev.lightdream.bountyhunter.gui;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.Bounty;
import dev.lightdream.bountyhunter.dto.Item;
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
                    System.out.println(Utils.getNBT(item, "bounty"));
                    int id = (int) ((double) Utils.getNBT(item, "bounty"));
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

        for (int i = 0; i < 54; i++) {
            if (!plugin.getGuiConfig().fillItemPositions.contains(i)) {
                availablePositions.add(i);
            }
        }

        for (int i = 10 * page; i < 10 * (page + 1); i++) {
            if (plugin.getDatabaseManager().getBountyList().size() > i) {
                bounties.add(plugin.getDatabaseManager().getBountyList().get(i));
            }
        }


        if (page != 0) {
            inventory.setItem(plugin.getGuiConfig().backItem.slot, Utils.setNBT(Utils.setNBT(ItemStackUtils.makeItem(plugin.getGuiConfig().backItem), "gui_use", "back"), "gui_protect", true));
        }

        if (page != plugin.getDatabaseManager().getBountyList().size() / 10) {
            inventory.setItem(plugin.getGuiConfig().nextItem.slot, Utils.setNBT(Utils.setNBT(ItemStackUtils.makeItem(plugin.getGuiConfig().nextItem), "gui_use", "back"), "gui_protect", true));
        }

        Item headTemplate = plugin.getGuiConfig().bountyItem;

        for (int i = 0; i < Math.min(availablePositions.size(), bounties.size()); i++) {
            Item head = headTemplate.clone();
            Bounty bounty = bounties.get(i);
            String playerName = Bukkit.getOfflinePlayer(bounty.player).getName();

            head.headOwner = playerName;
            head.displayName = parse(head.displayName, playerName, bounty);
            head.lore = parse(head.lore, playerName, bounty);

            ItemStack item = ItemStackUtils.makeItem(head);
            item = Utils.setNBT(item, "gui_protect", true);
            item = Utils.setNBT(item, "gui_use", "bounty");
            item = Utils.setNBT(item, "bounty", bounty.id);

            inventory.setItem(availablePositions.get(i), item);
        }

        return inventory;
    }

    private String parse(String raw, String player, Bounty bounty) {
        String parsed = raw;

        parsed = parsed.replace("%player%", player);
        parsed = parsed.replace("%description%", bounty.message);
        parsed = parsed.replace("%hunters%", String.valueOf(bounty.getHunters().size()));

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

    private List<String> parse(List<String> raw, String player, Bounty bounty) {
        List<String> parsed = new ArrayList<>();
        raw.forEach(line -> {
            String newLine = parse(line, player, bounty);
            if (!newLine.equals("- ")) {
                parsed.add(newLine);
            }
        });
        return parsed;
    }
}
