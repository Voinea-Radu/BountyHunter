package dev.lightdream.bountyhunter.managers;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.Bounty;
import dev.lightdream.bountyhunter.dto.User;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScheduleManager {

    private final BountyHunter plugin;
    @Getter
    private List<User> top = new ArrayList<>();

    public ScheduleManager(BountyHunter plugin) {
        this.plugin = plugin;
        registerTopCalculator();
    }

    public void registerTopCalculator(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            List<User> users = plugin.getDatabaseManager().getUserList();
            users.sort(new sortUserTop());
            top = users;
        }, 0, plugin.getConfiguration().topUpdatePeriod);
    }

    public static class sortUserTop implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return Integer.compare(u1.kills, u2.kills) * -1;
        }
    }
}
