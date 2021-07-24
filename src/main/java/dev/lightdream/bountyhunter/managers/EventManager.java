package dev.lightdream.bountyhunter.managers;

import dev.lightdream.bountyhunter.BountyHunter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

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

}
