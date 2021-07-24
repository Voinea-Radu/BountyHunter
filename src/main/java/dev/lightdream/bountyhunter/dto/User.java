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
    @DatabaseField(columnName = "kills")
    public int kills;

    public User(UUID uuid, int kills){
        this.uuid = uuid;
        this.kills = kills;
    }

}
