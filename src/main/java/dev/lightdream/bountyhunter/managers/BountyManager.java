package dev.lightdream.bountyhunter.managers;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.Bounty;
import dev.lightdream.bountyhunter.dto.BountyCashBack;
import dev.lightdream.bountyhunter.dto.User;
import dev.lightdream.bountyhunter.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BountyManager {

    private final BountyHunter plugin;

    public BountyManager(BountyHunter plugin) {
        this.plugin = plugin;
    }

    public void placeBounty(User player, User target, double reward, String message) {
        long coolDown = player.lastBounty + plugin.getConfiguration().bountyCoolDown - System.currentTimeMillis();
        if (coolDown > 0) {
            plugin.getMessageManager().sendMessage(player, plugin.getMessages().bountyCreateCoolDown.replace("%minutes%", String.valueOf((int) coolDown / 1000 / 60)));
            return;
        }
        if (player.uuid == target.uuid) {
            plugin.getMessageManager().sendMessage(player, plugin.getMessages().cannotPlaceBountyOnYourself);
            return;
        }
        if (plugin.getEconomy().getBalance(Bukkit.getOfflinePlayer(player.uuid)) < reward) {
            plugin.getMessageManager().sendMessage(player, plugin.getMessages().notEnoughMoney);
            return;
        }
        if (reward < plugin.getConfiguration().lowestBounty) {
            plugin.getMessageManager().sendMessage(player, plugin.getMessages().invalidAmount);
            return;
        }

        Bounty bounty = new Bounty(player.uuid, target.uuid, reward, message, new ArrayList<>());
        plugin.getEconomy().withdrawPlayer(Bukkit.getOfflinePlayer(player.uuid), reward);
        plugin.getDatabaseManager().getBountyList().add(bounty);
        player.lastBounty = System.currentTimeMillis();

        plugin.getMessageManager().sendMessage(player, parse(plugin.getMessages().bountyPlaced, player.getName(), target.getName(), "", "$" + reward));

        if (plugin.getConfiguration().bountyPlacedHunted) {
            if (Bukkit.getOfflinePlayer(target.uuid).isOnline()) {
                plugin.getMessageManager().sendMessage(target, parse(plugin.getMessages().bountyPlacedHunted, player.getName(), target.getName(), "", "$" + reward));
            }
        }
        if (plugin.getConfiguration().bountyPlacedBroadcast) {
            Bukkit.broadcastMessage(parse(plugin.getMessages().bountyPlacedBroadcast, player.getName(), target.getName(), "", "$" + reward));
        }

    }

    public void placeBounty(User player, User target, List<ItemStack> reward, String message) {
        long coolDown = player.lastBounty + plugin.getConfiguration().bountyCoolDown - System.currentTimeMillis();
        if (!(coolDown > 0)) {
            plugin.getMessageManager().sendMessage(player, plugin.getMessages().bountyCreateCoolDown.replace("%minutes%", String.valueOf((int) coolDown / 1000 / 60)));
            return;
        }
        if (player.uuid == target.uuid) {
            plugin.getMessageManager().sendMessage(player, plugin.getMessages().cannotPlaceBountyOnYourself);
            return;
        }

        Bounty bounty = new Bounty(player.uuid, target.uuid, Utils.itemStackArrayToBase64(reward), message, new ArrayList<>());
        plugin.getDatabaseManager().getBountyList().add(bounty);
        player.lastBounty = System.currentTimeMillis();

        plugin.getMessageManager().sendMessage(player, parse(plugin.getMessages().bountyPlaced, player.getName(), target.getName(), "", "item"));

        if (plugin.getConfiguration().bountyPlacedHunted) {
            if (Bukkit.getOfflinePlayer(target.uuid).isOnline()) {
                plugin.getMessageManager().sendMessage(target, parse(plugin.getMessages().bountyPlacedHunted, player.getName(), target.getName(), "", "item"));
            }
        }
        if (plugin.getConfiguration().bountyPlacedBroadcast) {
            Bukkit.broadcastMessage(parse(plugin.getMessages().bountyPlacedBroadcast, player.getName(), target.getName(), "", "item"));
        }
    }

    public String parse(String raw, String player, String target, String hunter, String reward) {
        String parsed = raw;

        parsed = parsed.replace("%player%", player);
        parsed = parsed.replace("%target%", target);
        parsed = parsed.replace("%hunter%", hunter);
        parsed = parsed.replace("%reward%", reward);

        return parsed;
    }

    public void takeBounty(int id, UUID hunter) {
        Bounty bounty = plugin.getDatabaseManager().getBounty(id);
        if (bounty == null) {
            return;
        }
        List<String> hunters = bounty.getHunters();
        System.out.println(hunters);
        System.out.println(hunter.toString());
        if (hunters.contains(hunter.toString())) {
            plugin.getMessageManager().sendMessage(hunter, plugin.getMessages().alreadyTakenBounty);
            return;
        }

        hunters.add(hunter.toString());
        bounty.setHunters(hunters);
        UUID target = bounty.player;

        String reward = bounty.rewardType.equalsIgnoreCase("item") ? "items" : "$" + bounty.rewardMoney;

        if (plugin.getConfiguration().bountyTakenHunted) {
            plugin.getMessageManager().sendMessage(target, parse(plugin.getMessages().bountyTakenHunted, "", Bukkit.getOfflinePlayer(target).getName(), Bukkit.getOfflinePlayer(hunter).getName(), reward));
        }
        if (plugin.getConfiguration().bountyTakenBroadcast) {
            Bukkit.broadcastMessage(parse(plugin.getMessages().bountyTakenBroadcast, "", Bukkit.getOfflinePlayer(target).getName(), Bukkit.getOfflinePlayer(hunter).getName(), reward));
        }
        if (plugin.getConfiguration().bountyTaken) {
            plugin.getMessageManager().sendMessage(hunter, parse(plugin.getMessages().bountyTaken, "", Bukkit.getOfflinePlayer(target).getName(), Bukkit.getOfflinePlayer(hunter).getName(), reward));
        }

    }

    public void claimBounty(UUID target, UUID hunter) {
        List<Bounty> bounties = plugin.getDatabaseManager().getBounty(target);

        bounties.forEach(bounty -> {
            if (bounty.getHunters().contains(hunter.toString())) {
                System.out.println("Giving items");
                switch (bounty.rewardType) {
                    case "item":
                        try {
                            Utils.itemStackArrayFromBase64(bounty.rewardItems).forEach(obj -> Bukkit.getPlayer(hunter).getInventory().addItem(obj));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "money":
                        plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(hunter), bounty.rewardMoney);
                        break;
                }

                String reward = bounty.rewardType.equalsIgnoreCase("item") ? "items" : "$" + bounty.rewardMoney;

                if (plugin.getConfiguration().bountyClaimedHunter) {
                    plugin.getMessageManager().sendMessage(target, parse(plugin.getMessages().bountyClaimedHunter, "", Bukkit.getOfflinePlayer(target).getName(), Bukkit.getOfflinePlayer(hunter).getName(), reward));
                }
                if (plugin.getConfiguration().bountyClaimedBroadcast) {
                    Bukkit.broadcastMessage(parse(plugin.getMessages().bountyClaimedBroadcast, "", Bukkit.getOfflinePlayer(target).getName(), Bukkit.getOfflinePlayer(hunter).getName(), reward));
                }
                if (plugin.getConfiguration().bountyClaimed) {
                    plugin.getMessageManager().sendMessage(target, parse(plugin.getMessages().bountyClaimed, "", Bukkit.getOfflinePlayer(target).getName(), Bukkit.getOfflinePlayer(hunter).getName(), reward));
                }
                plugin.getDatabaseManager().getBountyList().remove(bounty);
                User user = plugin.getDatabaseManager().getUser(hunter);
                int pastLevel = plugin.getLevelManager().getLevel(user);
                user.kills++;
                int currentLevel = plugin.getLevelManager().getLevel(user);
                if (pastLevel != currentLevel) {
                    plugin.getMessageManager().sendMessage(hunter, plugin.getMessages().leveledUp.replace("%level%", String.valueOf(currentLevel)));
                    Utils.spawnFireworks(Bukkit.getPlayer(hunter).getLocation(), plugin.getConfiguration().numberOfFireworks, Color.fromRGB(plugin.getConfiguration().fireWorksRed, plugin.getConfiguration().fireWorksGreen, plugin.getConfiguration().fireWorksBlue), true);
                }

            }

        });
    }

    public void cancelBounty(User player, User target) {
        plugin.getDatabaseManager().getBounty(player.uuid, target.uuid).forEach(bounty -> {
            switch (bounty.rewardType) {
                case "item":
                    if (Bukkit.getOfflinePlayer(player.uuid).isOnline()) {
                        try {
                            Utils.itemStackArrayFromBase64(bounty.rewardItems).forEach(item -> {
                                Bukkit.getPlayer(player.uuid).getInventory().addItem(item);
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        plugin.getDatabaseManager().getBountyCashBackList().add(new BountyCashBack(bounty.player, bounty.target, bounty.rewardItems));
                    }
                    break;
                case "money":
                    plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(player.uuid), bounty.rewardMoney);
                    if (Bukkit.getOfflinePlayer(player.uuid).isOnline()) {
                        plugin.getMessageManager().sendMessage(player, parse(plugin.getMessages().bountyCanceled, Bukkit.getOfflinePlayer(player.uuid).getName(), Bukkit.getOfflinePlayer(target.uuid).getName(), "", "$" + bounty.rewardMoney));
                    } else {
                        plugin.getDatabaseManager().getBountyCashBackList().add(new BountyCashBack(bounty.player, bounty.target, ""));
                    }
                    break;
            }
            plugin.getDatabaseManager().removeBounty(bounty);
        });

    }

    public void cancelBounty(User player) {
        plugin.getDatabaseManager().getBounty(player.uuid, true).forEach(bounty -> {
            cancelBounty(player, plugin.getDatabaseManager().getUser(bounty.target));
        });

    }

}
