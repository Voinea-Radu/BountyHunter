package dev.lightdream.bountyhunter.commands;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelCommand extends Command {
    public LevelCommand(BountyHunter plugin) {
        super(plugin, Collections.singletonList("level"), "Show your level stats", "bh.level", true, false, "/bh level");
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        User user = plugin.getDatabaseManager().getUser(player.getUniqueId());

        parse(plugin.getMessages().levelMessage, user).forEach(line->{
            plugin.getMessageManager().sendMessage(sender, line);
        });

    }

    @Override
    public List<String> onTabComplete(Object commandSender, List<String> args) {
        return new ArrayList<>();
    }

    private String parse(String raw, User user) {
        String parsed = raw;

        int level = plugin.getLevelManager().getLevel(user);

        parsed = parsed.replace("%level%", String.valueOf(level));
        parsed = parsed.replace("%kills%", String.valueOf(user.kills));
        parsed = parsed.replace("%needed%", String.valueOf(plugin.getConfiguration().levelMap.get(level)));
        parsed = parsed.replace("%progress_bar%", plugin.getLevelManager().getProgressBar(user));
        parsed = parsed.replace("%progress_percent%", String.valueOf(plugin.getLevelManager().getProgressPercent(user)));

        return parsed;
    }

    private List<String> parse(List<String> raw, User user) {
        List<String> parsed = new ArrayList<>();
        raw.forEach(line -> parsed.add(parse(line, user)));

        return parsed;
    }

}
