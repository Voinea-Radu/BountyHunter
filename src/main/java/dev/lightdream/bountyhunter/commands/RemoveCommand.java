package dev.lightdream.bountyhunter.commands;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.User;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoveCommand extends Command {
    public RemoveCommand(BountyHunter plugin) {
        super(plugin, Collections.singletonList("remove"), "Removes the bounty from the list", "bh.remove", false, false, "/bh remove [player] {target}");
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        User user;

        switch (args.size()) {
            case 1:
                user = plugin.getDatabaseManager().getUser(args.get(0));
                if (user == null) {
                    plugin.getMessageManager().sendMessage(sender, plugin.getMessages().invalidPlayer);
                    System.out.println("Something wrong has happened");
                    return;
                }
                plugin.getBountyManager().cancelBounty(user);
                plugin.getMessageManager().sendMessage(sender, plugin.getMessages().bountyCanceledByYou.replace("%player%", args.get(0)));
                break;
            case 2:
                user = plugin.getDatabaseManager().getUser(args.get(0));
                User target = plugin.getDatabaseManager().getUser(args.get(1));
                if (user == null || target == null) {
                    plugin.getMessageManager().sendMessage(sender, plugin.getMessages().invalidPlayer);
                    return;
                }
                plugin.getBountyManager().cancelBounty(user, target);
                plugin.getMessageManager().sendMessage(sender, plugin.getMessages().bountyCanceledByYou.replace("%player%", args.get(0)));
                break;
        }
    }

    @Override
    public List<String> onTabComplete(Object commandSender, List<String> args) {
        return new ArrayList<>();
    }
}
