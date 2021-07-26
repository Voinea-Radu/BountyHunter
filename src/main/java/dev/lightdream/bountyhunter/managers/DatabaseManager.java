package dev.lightdream.bountyhunter.managers;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import dev.lightdream.bountyhunter.BountyHunter;
import dev.lightdream.bountyhunter.dto.Bounty;
import dev.lightdream.bountyhunter.dto.BountyCashBack;
import dev.lightdream.bountyhunter.dto.SQL;
import dev.lightdream.bountyhunter.dto.User;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class DatabaseManager {

    private final BountyHunter plugin;

    private final SQL sqlConfig;
    private final ConnectionSource connectionSource;

    private final Dao<User, UUID> userDao;
    private final Dao<Bounty, Integer> bountyDao;
    private final Dao<BountyCashBack, Integer> bountyCashBackDao;

    @Getter
    private final List<User> userList;
    @Getter
    private final List<Bounty> bountyList;
    @Getter
    private final List<BountyCashBack> bountyCashBackList;

    public DatabaseManager(BountyHunter plugin) throws SQLException {
        this.plugin = plugin;
        this.sqlConfig = plugin.getSql();
        String databaseURL = getDatabaseURL();

        connectionSource = new JdbcConnectionSource(
                databaseURL,
                sqlConfig.username,
                sqlConfig.password,
                DatabaseTypeUtils.createDatabaseType(databaseURL)
        );

        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, Bounty.class);
        TableUtils.createTableIfNotExists(connectionSource, BountyCashBack.class);

        this.userDao = DaoManager.createDao(connectionSource, User.class);
        this.bountyDao = DaoManager.createDao(connectionSource, Bounty.class);
        this.bountyCashBackDao = DaoManager.createDao(connectionSource, BountyCashBack.class);

        userDao.setAutoCommit(getDatabaseConnection(), false);
        bountyDao.setAutoCommit(getDatabaseConnection(), false);
        bountyCashBackDao.setAutoCommit(getDatabaseConnection(), false);

        this.userList = getUsers();
        this.bountyList = getBounties();
        this.bountyCashBackList = getBountyCashBacks();
    }

    private @NotNull String getDatabaseURL() {
        switch (sqlConfig.driver) {
            case MYSQL:
            case MARIADB:
            case POSTGRESQL:
                return "jdbc:" + sqlConfig.driver.toString().toLowerCase() + "://" + sqlConfig.host + ":" + sqlConfig.port + "/" + sqlConfig.database + "?useSSL=" + sqlConfig.useSSL + "&autoReconnect=true";
            case SQLSERVER:
                return "jdbc:sqlserver://" + sqlConfig.host + ":" + sqlConfig.port + ";databaseName=" + sqlConfig.database;
            case H2:
                return "jdbc:h2:file:" + sqlConfig.database;
            case SQLITE:
                return "jdbc:sqlite:" + new File(plugin.getDataFolder(), sqlConfig.database + ".db");
            default:
                throw new UnsupportedOperationException("Unsupported driver (how did we get here?): " + sqlConfig.driver.name());
        }
    }

    private DatabaseConnection getDatabaseConnection() throws SQLException {
        return connectionSource.getReadWriteConnection(null);
    }

    private @NotNull List<User> getUsers() {
        try {
            return userDao.queryForAll();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Collections.emptyList();
    }

    private @NotNull List<Bounty> getBounties() {
        try {
            return bountyDao.queryForAll();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Collections.emptyList();
    }

    private @NotNull List<BountyCashBack> getBountyCashBacks() {
        try {
            return bountyCashBackDao.queryForAll();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Collections.emptyList();
    }

    public void saveUsers() {
        try {
            for (User user : userList) {
                userDao.createOrUpdate(user);
            }
            userDao.commit(getDatabaseConnection());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void saveBounties() {
        try {
            for (Bounty bounty : bountyList) {
                bountyDao.createOrUpdate(bounty);
            }
            bountyDao.commit(getDatabaseConnection());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void saveBountyCashBacks() {
        try {
            for (BountyCashBack cashBack : bountyCashBackList) {
                bountyCashBackDao.createOrUpdate(cashBack);
            }
            bountyCashBackDao.commit(getDatabaseConnection());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public @NotNull User getUser(@NotNull UUID uuid) {
        Optional<User> optionalUser = userList.stream().filter(user -> user.getUuid().equals(uuid)).findFirst();

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        User user = new User(uuid, Bukkit.getOfflinePlayer(uuid).getName(), 0, 0L);
        userList.add(user);
        return user;
    }

    public @Nullable User getUser(@NotNull String name) {
        Optional<User> optionalUser = userList.stream().filter(user -> user.name.equals(name)).findFirst();
        return optionalUser.orElse(null);
    }

    public @Nullable Bounty getBounty(int id) {
        Optional<Bounty> optionalBounty = bountyList.stream().filter(bounty -> bounty.id == id).findFirst();
        return optionalBounty.orElse(null);
    }

    public @NotNull List<Bounty> getBounty(UUID target) {
        return bountyList.stream().filter(bounty -> bounty.target.equals(target)).collect(Collectors.toList());
    }

    public @NotNull List<Bounty> getBounty(UUID player, boolean usePlayer) {
        return bountyList.stream().filter(bounty -> bounty.player.equals(player)).collect(Collectors.toList());
    }

    public @NotNull List<Bounty> getBounty(UUID player, UUID target) {
        return bountyList.stream().filter(bounty -> bounty.target.equals(target) && bounty.player.equals(player)).collect(Collectors.toList());
    }

    public @NotNull List<BountyCashBack> getBountyCashBacks(UUID player) {
        return bountyCashBackList.stream().filter(cashBack -> cashBack.player.equals(player)).collect(Collectors.toList());
    }

    public void removeBounty(Bounty bounty){
        bountyList.remove(bounty);
        try {
            bountyDao.delete(bounty);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeBountyCashBack(BountyCashBack cashBack){
        bountyCashBackList.remove(cashBack);
        try {
            bountyCashBackDao.delete(cashBack);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}