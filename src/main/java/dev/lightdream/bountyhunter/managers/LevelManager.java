package dev.lightdream.bountyhunter.managers;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.User;

import java.util.UUID;

public class LevelManager {

    private final BountyHunter plugin;

    public LevelManager(BountyHunter plugin) {
        this.plugin = plugin;
    }

    public int getLevel(UUID player) {
        User user = plugin.getDatabaseManager().getUser(player);

        for (int i = 0; i < plugin.getConfiguration().levelMap.size(); i++) {
            if (plugin.getConfiguration().levelMap.get(i) <= user.kills) {
                return plugin.getConfiguration().levelMap.size() - i;
            }
        }
        return 0;
    }

    public int getLevel(User user) {
        for (int i = 0; i < plugin.getConfiguration().levelMap.size(); i++) {
            if (plugin.getConfiguration().levelMap.get(i) <= user.kills) {
                return plugin.getConfiguration().levelMap.size() - i;
            }
        }
        return 0;
    }

    public int getKills(int level) {
        return plugin.getConfiguration().levelMap.get(plugin.getConfiguration().levelMap.size() - level);
    }


    public int getProgressPercent(User user) {
        int level = getLevel(user);
        int passedLevel = getKills(level);
        int nextLevel = getKills(level + 1);
        int have = user.kills - passedLevel;
        int needed = nextLevel - passedLevel;

        System.out.println("level = " + level);
        System.out.println("passedLevel = " + passedLevel);
        System.out.println("nextLevel = " + nextLevel);
        System.out.println("have = " + have);
        System.out.println("needed = " + needed);
        System.out.println("progress = " + 100 * have / needed);
        return 100 * have / needed;
    }

    public String getProgressBar(User user) {
        int percent = getProgressPercent(user);
        int breakPoint = percent / plugin.getConfiguration().progressBanLength;

        String red = "&c";
        String green = "&a";
        String progress = "█";

        StringBuilder output = new StringBuilder();

        for (int i = 0; i < breakPoint; i++) {
            output.append(green).append(progress);
        }
        for (int i = breakPoint; i < plugin.getConfiguration().progressBanLength; i++) {
            output.append(red).append(progress);
        }
        return output.toString();
    }

}
