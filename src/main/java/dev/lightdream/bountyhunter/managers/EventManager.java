package dev.lightdream.bountyhunter.managers;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.BountyCashBack;
import dev.lightdream.bountyhunter.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.List;

public class EventManager implements Listener {

    public final BountyHunter plugin;

    public EventManager(BountyHunter plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        System.out.println("Received kill event");
        if (event.getEntity().getKiller() == null) {
            return;
        }
        Player player = event.getEntity();
        Player killer = player.getKiller();

        plugin.getBountyManager().claimBounty(player.getUniqueId(), killer.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getDatabaseManager().getUser(event.getPlayer().getUniqueId());
        List<BountyCashBack> bountyCashBacks = plugin.getDatabaseManager().getBountyCashBacks(event.getPlayer().getUniqueId());
        System.out.println("Player joining. He has " + bountyCashBacks.size() + " cashback(s).");
        bountyCashBacks.forEach(cashBack -> {
            plugin.getMessageManager().sendMessage(cashBack.player, parse(plugin.getMessages().bountyCanceled, Bukkit.getOfflinePlayer(cashBack.player).getName(), Bukkit.getOfflinePlayer(cashBack.target).getName()));
            try {
                Utils.itemStackArrayFromBase64(cashBack.cashBack).forEach(item -> {
                    Bukkit.getPlayer(cashBack.player).getInventory().addItem(item);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            plugin.getDatabaseManager().removeBountyCashBack(cashBack);
        });
    }

    private String parse(String raw, String player, String target) {
        String parsed = raw;

        parsed = parsed.replace("%player%", player);
        parsed = parsed.replace("%target%", player);

        return parsed;
    }
}
