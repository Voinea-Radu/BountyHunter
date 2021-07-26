package dev.lightdream.bountyhunter.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@DatabaseTable(tableName = "bounty_cash_backs")
public class BountyCashBack {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    public int id;
    @DatabaseField(columnName = "player")
    public UUID player;
    @DatabaseField(columnName = "target")
    public UUID target;
    @DatabaseField(columnName = "cash_back")
    public String cashBack;

    public BountyCashBack(UUID player, UUID target, String cashBack) {
        //this.player = player;
        //this.target = target;
        this.player = player;
        this.target = target;
        this.cashBack = cashBack;
    }


}
