package dev.lightdream.bountyhunter.dto;

import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class Config {

    public boolean bountyPlacedBroadcast = true;
    public boolean bountyPlacedHunted = true;

    public boolean bountyTaken = true;
    public boolean bountyTakenHunted = true;
    public boolean bountyTakenBroadcast = true;

    public boolean bountyClaimed = true;
    public boolean bountyClaimedHunter = true;
    public boolean bountyClaimedBroadcast = true;

    public String README = "Highest level at the top";
    public List<Integer> levelMap = Arrays.asList(
            100,
            50,
            10,
            5,
            3,
            1,
            0
    );

    public int progressBanLength = 10;
    public int lowestBounty = 10000;

    public List<String> disallowedNames = Arrays.asList("/op", "/gmc");
    public List<String> getDisallowedLores = Arrays.asList("COMMON", "FREE");
}
