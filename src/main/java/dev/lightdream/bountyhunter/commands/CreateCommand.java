package dev.lightdream.bountyhunter.commands;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.User;
import dev.lightdream.bountyhunter.gui.BountyCreateGUI;
import dev.lightdream.bountyhunter.gui.BountyPlaceGUI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateCommand extends Command {
    public CreateCommand(BountyHunter plugin) {
        super(plugin, Collections.singletonList("create"), "Create a new bounty", "bh.create", true, false, "/bh create [player] [item/amount] {message}");
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        if (args.size() == 0) {
            Player p = (Player) sender;
            p.openInventory(new BountyCreateGUI(plugin, plugin.getDatabaseManager().getUser(p.getUniqueId()), 0).getInventory());
            return;
        }

        if (args.size() < 2) {
            sendUsage(sender);
            return;
        }

        String type = args.get(1);
        User player = plugin.getDatabaseManager().getUser(((OfflinePlayer) sender).getUniqueId());
        User target = plugin.getDatabaseManager().getUser(args.get(0));
        if (target == null) {
            plugin.getMessageManager().sendMessage(sender, plugin.getMessages().invalidPlayer);
            return;
        }

        StringBuilder message = new StringBuilder();

        for (int i = 2; i < args.size(); i++) {
            message.append(args.get(i)).append(" ");
        }

        if (type.equalsIgnoreCase("item")) {
            ((Player) sender).openInventory(new BountyPlaceGUI(plugin, player, target, message.toString()).getInventory());
            return;
        }
        try {
            int amount = Integer.parseInt(type);
            plugin.getBountyManager().placeBounty(player, target, amount, message.toString());
        } catch (NumberFormatException e) {
            plugin.getMessageManager().sendMessage(sender, plugin.getMessages().invalidBountyType);
            sendUsage(sender);
        }
    }

    @Override
    public List<String> onTabComplete(Object commandSender, List<String> args) {
        return new ArrayList<>();
    }
}
