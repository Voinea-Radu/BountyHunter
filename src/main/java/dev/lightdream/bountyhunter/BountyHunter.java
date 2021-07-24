package dev.lightdream.bountyhunter;

import dev.lightdream.bountyhunter.commands.Command;
import dev.lightdream.bountyhunter.commands.CreateCommand;
import dev.lightdream.bountyhunter.commands.LevelCommand;
import dev.lightdream.bountyhunter.commands.ListCommand;
import dev.lightdream.bountyhunter.dto.*;
import dev.lightdream.bountyhunter.managers.*;
import dev.lightdream.bountyhunter.utils.Persist;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class BountyHunter extends JavaPlugin {

    private final List<Command> commands = new ArrayList<>();

    private CommandManager commandManager;
    private MessageManager messageManager;
    private BountyManager bountyManager;
    private InventoryManager inventoryManager;
    private EventManager eventManager;
    private DatabaseManager databaseManager;
    private LevelManager levelManager;

    private Persist persist;
    private Config configuration;
    private Messages messages;
    private GUIConfig guiConfig;
    private SQL sql;

    private Economy economy = null;

    @Override
    public void onEnable() {

        persist = new Persist(this, Persist.PersistType.YAML);
        configuration = persist.load(Config.class);
        messages = persist.load(Messages.class);
        guiConfig = persist.load(GUIConfig.class);
        sql = persist.load(SQL.class);

        commands.add(new CreateCommand(this));
        commands.add(new ListCommand(this));
        commands.add(new LevelCommand(this));

        messageManager = new MessageManager(this);
        commandManager = new CommandManager(this, "bh");
        bountyManager = new BountyManager(this);
        inventoryManager = new InventoryManager(this);
        eventManager = new EventManager(this);
        levelManager = new LevelManager(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPI(this).register();
        } else {
            System.out.println("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (!setupEconomy()) {
            System.out.printf("[%s] - Disabled due to no Vault dependency found!%n", getDescription().getName());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            databaseManager = new DatabaseManager(this);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        persist.save(configuration);
        persist.save(messages);
        persist.save(guiConfig);
        persist.save(sql);

        databaseManager.saveUsers();
        databaseManager.saveBounties();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}
