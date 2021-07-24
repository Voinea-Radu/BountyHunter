package dev.lightdream.bountyhunter.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SQL {

    public Driver driver = Driver.SQLITE;
    public String host = "localhost";
    public String database = "IridiumSkyblock";
    public String username = "";
    public String password = "";
    public int port = 3306;
    public boolean useSSL = false;

    public enum Driver {
        MYSQL,
        MARIADB,
        SQLSERVER,
        POSTGRESQL,
        H2,
        SQLITE
    }

}
