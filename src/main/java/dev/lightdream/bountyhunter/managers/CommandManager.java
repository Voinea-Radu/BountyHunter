package dev.lightdream.bountyhunter.managers;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.commands.Command;
import dev.lightdream.bountyhunter.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings({"unused", "MismatchedQueryAndUpdateOfStringBuilder", "DuplicatedCode"})
public class CommandManager implements CommandExecutor, TabCompleter {

    private final BountyHunter plugin;
    private final List<Command> commands = new ArrayList<>();

    public CommandManager(BountyHunter plugin, String command) {
        this.plugin = plugin;
        System.out.println("Registering command");
        plugin.getCommand(command).setExecutor(this);
        plugin.getCommand(command).setTabCompleter(this);
        registerCommands();
    }

    public void registerCommands() {
        plugin.getCommands().forEach(this::registerCommand);
        commands.sort(Comparator.comparing(command -> command.aliases.get(0)));
    }

    public void registerCommand(Command command) {
        commands.add(command);
    }

    public void unregisterCommand(Command command) {
        commands.remove(command);
    }

    public void sendUsage(CommandSender sender) {
        plugin.getMessages().helpCommand.forEach(line -> {
            sender.sendMessage(Utils.color(line));
        });
    }

    public void dispatchCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command bukkitCommand, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        for (Command command : commands) {
            if (!(command.aliases.contains(args[0]))) {
                continue;
            }

            if (command.onlyForPlayers && !(sender instanceof Player)) {
                plugin.getMessageManager().sendMessage(sender, plugin.getMessages().mustBeAPlayer);
                return true;
            }

            if (command.onlyForConsole && !(sender instanceof ConsoleCommandSender)) {
                plugin.getMessageManager().sendMessage(sender, plugin.getMessages().mustBeConsole);
                return true;
            }

            if (!((sender.hasPermission(command.permission) || command.permission.equalsIgnoreCase("")))) {
                plugin.getMessageManager().sendMessage(sender, plugin.getMessages().noPermission);
                return true;
            }

            command.execute(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            return true;
        }

        plugin.getMessageManager().sendMessage(sender, plugin.getMessages().unknownCommand);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command bukkitCommand, String bukkitAlias, String[] args) {
        if (args.length == 1) {
            ArrayList<String> result = new ArrayList<>();
            for (Command command : commands) {
                for (String alias : command.aliases) {
                    if (alias.toLowerCase().startsWith(args[0].toLowerCase()) && (sender.hasPermission(command.permission) || command.permission.equalsIgnoreCase(""))) {
                        result.add(alias);
                    }
                }
            }
            return result;
        }

        for (Command command : commands) {
            if (command.aliases.contains(args[0]) && (sender.hasPermission(command.permission) || command.permission.equalsIgnoreCase(""))) {
                return command.onTabComplete(sender, new ArrayList<>(Arrays.asList(args).subList(1, args.length)));
            }
        }

        return Collections.emptyList();

    }
}
