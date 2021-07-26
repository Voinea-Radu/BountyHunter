package dev.lightdream.bountyhunter.commands;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.User;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TopCommand extends Command {
    public TopCommand(BountyHunter plugin) {
        super(plugin, Collections.singletonList("top"), "Shows the top hunters", "", false, false, "/bh top");
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        List<User> top = plugin.getScheduleManager().getTop();
        StringBuilder message = new StringBuilder();
        message.append("\n");
        String topEntryRaw = plugin.getMessages().topEntry;
        for (int i = 0; i < Math.min(plugin.getConfiguration().topEntries, top.size()); i++) {
            message.append(parse(topEntryRaw, i + 1, top.get(i).name, top.get(i).kills, plugin.getLevelManager().getLevel(top.get(i)), plugin.getLevelManager().getProgressBar(top.get(i)), plugin.getLevelManager().getProgressPercent(top.get(i))));
            message.append("\n");
        }

        plugin.getMessageManager().sendMessage(sender, message.toString());
    }

    @Override
    public List<String> onTabComplete(Object commandSender, List<String> args) {
        return new ArrayList<>();
    }

    private String parse(String raw, int place, String player, int kills, int level, String progressBar, int progressPercent) {
        String parsed = raw;

        parsed = parsed.replace("%place%", String.valueOf(place));
        parsed = parsed.replace("%player%", String.valueOf(player));
        parsed = parsed.replace("%level%", String.valueOf(level));
        parsed = parsed.replace("%kills%", String.valueOf(kills));
        parsed = parsed.replace("%progress_bar%", String.valueOf(progressBar));
        parsed = parsed.replace("%progress_percent%", String.valueOf(progressPercent) + "%");

        return parsed;
    }

}
