package dev.lightdream.bountyhunter.commands;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.gui.BountyPlaceGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class CreateCommand extends Command {
    public CreateCommand(BountyHunter plugin) {
        super(plugin, Collections.singletonList("create"), "Create a new bounty", "bh.create", true, false, "/bh create [player] [item/amount] {message}");
    }

    @Override
    public void execute(Object sender, List<String> args) {
        if (args.size() < 2) {
            sendUsage(sender);
            return;
        }

        String type = args.get(1);
        OfflinePlayer player = (OfflinePlayer) sender;
        OfflinePlayer target = Bukkit.getOfflinePlayer(args.get(0));
        StringBuilder message = new StringBuilder();

        for (int i = 2; i < args.size(); i++) {
            message.append(args.get(i)).append(" ");
        }

        if (target == null) {
            plugin.getMessageManager().sendMessage(sender, plugin.getMessages().invalidPlayer);
            return;
        }

        if (type.equalsIgnoreCase("item")) {
            ((Player) player).openInventory(new BountyPlaceGUI(plugin, player, target, message.toString()).getInventory());
        } else {
            try {
                int amount = Integer.parseInt(type);
                if (plugin.getEconomy().getBalance(player) < amount) {
                    plugin.getMessageManager().sendMessage(player, plugin.getMessages().notEnoughMoney);
                    return;
                }
                if(amount<plugin.getConfiguration().lowestBounty){
                    plugin.getMessageManager().sendMessage(player, plugin.getMessages().invalidAmount);
                }
                plugin.getBountyManager().placeBounty(player, target, amount, message.toString());
            } catch (NumberFormatException e) {
                plugin.getMessageManager().sendMessage(sender, plugin.getMessages().invalidBountyType);
                sendUsage(sender);
            }
        }


    }

    @Override
    public List<String> onTabComplete(Object commandSender, List<String> args) {
        return new ArrayList<>();
    }
}
