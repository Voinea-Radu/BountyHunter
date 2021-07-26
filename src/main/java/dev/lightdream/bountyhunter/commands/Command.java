package dev.lightdream.bountyhunter.commands;

import dev.lightdream.bountyhunter.BountyHunter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class Command {

    public final @NotNull List<String> aliases;
    public final @NotNull String description;
    public final @NotNull String permission;
    public final boolean onlyForPlayers;
    public final boolean onlyForConsole;
    public final String usage;
    public final BountyHunter plugin;

    public Command(BountyHunter plugin, @NotNull List<String> aliases, @NotNull String description, @NotNull String permission, boolean onlyForPlayers, boolean onlyForConsole, String usage) {
        this.plugin = plugin;
        this.aliases = aliases;
        this.description = description;
        this.permission = permission;
        this.onlyForPlayers = onlyForPlayers;
        this.onlyForConsole = onlyForConsole;
        this.usage = usage;
    }

    public abstract void execute(CommandSender sender, List<String> args);

    public abstract List<String> onTabComplete(Object commandSender, List<String> args);

    public void sendUsage(CommandSender sender){
        plugin.getMessageManager().sendMessage(sender, usage);
    }
}
