package dev.lightdream.bountyhunter.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "users")
public class User {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    public int id;
    @DatabaseField(columnName = "uuid", unique = true)
    public UUID uuid;
    @DatabaseField(columnName = "name", unique = true)
    public String name;
    @DatabaseField(columnName = "kills")
    public int kills;
    @DatabaseField(columnName = "last_bounty")
    public long lastBounty;

    public User(UUID uuid, String name, int kills, long lastBounty){
        this.uuid = uuid;
        this.name = name;
        this.kills = kills;
        this.lastBounty = lastBounty;
    }

}
