package dev.lightdream.bountyhunter.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@DatabaseTable(tableName = "bounties")
public class Bounty {

    @DatabaseField(columnName = "id", generatedId = true, canBeNull = false)
    public int id;
    @DatabaseField(columnName = "player")
    public String player;
    @DatabaseField(columnName = "target")
    public String target;
    @DatabaseField(columnName = "message")
    public String message;
    @DatabaseField(columnName = "hunters")
    public String hunters;
    @DatabaseField(columnName = "reward_type")
    public String rewardType;
    @DatabaseField(columnName = "reward_items")
    public String rewardItems;
    @DatabaseField(columnName = "reward_money")
    public int rewardMoney;

    public Bounty(UUID player, UUID target, String rewardItems, String message, List<UUID> hunters) {
        commonConstructor(player, target, message, hunters);
        this.rewardType = "item";
        this.rewardItems = rewardItems;
        this.rewardMoney = 0;
    }

    public Bounty(UUID player, UUID target, int rewardMoney, String message, List<UUID> hunters) {
        commonConstructor(player, target, message, hunters);
        this.rewardType = "money";
        this.rewardItems = "";
        this.rewardMoney = rewardMoney;
    }

    public void commonConstructor(UUID player, UUID target, String message, List<UUID> hunters) {
        this.player = player.toString();
        this.target = target.toString();
        this.message = message;
        setHunters(hunters, true);
    }

    public List<String> getHunters() {
        return Arrays.asList(hunters.split("\\|"));
    }

    public void setHunters(List<String> hunters) {
        StringBuilder huntersString = new StringBuilder();
        for (String uuid : hunters) {
            huntersString.append(uuid).append("|");
        }
        this.hunters = huntersString.toString();
    }

    public void setHunters(List<UUID> hunters, boolean useUUID) {
        StringBuilder huntersString = new StringBuilder();
        for (UUID uuid : hunters) {
            huntersString.append(uuid.toString()).append("|");
        }
        this.hunters = huntersString.toString();
    }

}
