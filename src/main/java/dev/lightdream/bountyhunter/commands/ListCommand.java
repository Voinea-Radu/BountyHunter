package dev.lightdream.bountyhunter.commands;

import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.gui.BountyListGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListCommand extends Command {
    public ListCommand(BountyHunter plugin) {
        super(plugin, Collections.singletonList("list"), "Opens a GUI for you to pick your bounty", "bh.list", true, false, "/bh list");
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {
        ((Player) sender).openInventory(new BountyListGUI(plugin, 0).getInventory());
    }

    @Override
    public List<String> onTabComplete(Object commandSender, List<String> args) {
        return new ArrayList<>();
    }
}
