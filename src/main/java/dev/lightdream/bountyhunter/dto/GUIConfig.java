package dev.lightdream.bountyhunter.dto;

import dev.lightdream.bountyhunter.utils.XMaterial;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class GUIConfig {

    public Item fillItem = new Item(XMaterial.BLUE_STAINED_GLASS_PANE, 1, "", new ArrayList<>());
    public Item backItem = new Item(XMaterial.ARROW, 1, 45, "Back", new ArrayList<>());
    public Item nextItem = new Item(XMaterial.ARROW, 1, 53, "Next", new ArrayList<>());
    public List<Integer> fillItemPositions = Arrays.asList(46, 47, 48, 49, 50, 51, 52);
    public Item bountyItem = new Item(XMaterial.PLAYER_HEAD, 1, "%player%", Arrays.asList("Kill this player to get: ", "- %reward-1%", "- %reward-2%", "- %reward-3%", "Description: %description%", "Number of hunters: %hunters%", "", "Click to get this bounty."));
}
