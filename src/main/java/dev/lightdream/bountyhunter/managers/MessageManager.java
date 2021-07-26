package dev.lightdream.bountyhunter.managers;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.User;
import dev.lightdream.bountyhunter.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MessageManager {

    private final BountyHunter plugin;

    public MessageManager(BountyHunter plugin) {
        this.plugin = plugin;
    }

    public void sendMessage(CommandSender target, String message) {
        target.sendMessage(Utils.color(plugin.getMessages().prefix + message));
    }

    public void sendMessage(UUID target, String message) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(target);
        if (player != null) {
            if (player.isOnline()) {
                ((Player) player).sendMessage(Utils.color(plugin.getMessages().prefix + message));
            }
        }
    }

    public void sendMessage(String target, String message) {
        User user = plugin.getDatabaseManager().getUser(target);
        sendMessage(user, message);
    }

    public void sendMessage(User target, String message) {
        sendMessage(target.uuid, message);
    }
}
