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
import dev.lightdream.bountyhunter.dto.SQL;
import dev.lightdream.bountyhunter.dto.User;
import lombok.Getter;
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

    @Getter
    private final List<User> userList;
    @Getter
    private final List<Bounty> bountyList;


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

        this.userDao = DaoManager.createDao(connectionSource, User.class);
        this.bountyDao = DaoManager.createDao(connectionSource, Bounty.class);

        userDao.setAutoCommit(getDatabaseConnection(), false);
        bountyDao.setAutoCommit(getDatabaseConnection(), false);

        this.userList = getUsers();
        this.bountyList = getBounties();
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

    public @NotNull List<User> getUsers() {
        try {
            return userDao.queryForAll();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Collections.emptyList();
    }

    public @NotNull List<Bounty> getBounties() {
        try {
            return bountyDao.queryForAll();
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

    public @NotNull User getUser(@NotNull UUID uuid) {
        Optional<User> optionalUser = getUserList().stream().filter(user -> user.getUuid().equals(uuid)).findFirst();

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }

        User user = new User(uuid, 0);
        userList.add(user);
        return user;
    }

    public @Nullable Bounty getBounty(int id) {
        Optional<Bounty> optionalBounty = getBountyList().stream().filter(bounty -> bounty.id == id).findFirst();
        return optionalBounty.orElse(null);
    }

    public @Nullable List<Bounty> getBounty(UUID target) {
        return getBountyList().stream().filter(bounty -> bounty.target.equals(target.toString())).collect(Collectors.toList());
    }

}