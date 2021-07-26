package dev.lightdream.bountyhunter.dto;

import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class Messages {

    public String prefix = "[BountyHunters] ";

    public String mustBeAPlayer = "You must be a player to use this command.";
    public String mustBeConsole = "You must be console to use this command.";
    public String noPermission = "You do not have the permission to use this command.";
    public String unknownCommand = "This is not a valid command.";
    public String invalidBountyType = "This is not a valid bounty type.";
    public String invalidPlayer = "This is not a valid player. Please check its name again";
    public String notEnoughMoney = "You do not have enough money";
    public String alreadyTakenBounty = "You have already taken this bounty";
    public String cannotPlaceBountyOnYourself = "You can not place a bounty on yourself";

    public String bountyPlaced = "You have placed a bounty on player %target% for %reward%";
    public String bountyPlacedHunted = "%player% have placed a bounty on you for %reward%";
    public String bountyPlacedBroadcast = "%player% have placed a bounty on %target% for %reward%";

    public String bountyTaken = "You have taken the bounty on player %target% for %reward%";
    public String bountyTakenHunted = "%hunter% have taken the bounty on you for %reward%";
    public String bountyTakenBroadcast = "%hunter% has taken a bounty on %target% for %reward%";

    public String bountyClaimed = "You have been killed by %hunter%. He got the bounty of %reward%";
    public String bountyClaimedHunter = "You have killed %target%. You got the bounty of %reward%";
    public String bountyClaimedBroadcast = "You have killed %target%. You got the bounty of %reward%";

    public String bountyPlaceGuiTitle = "Add add here all the items";
    public String bountyListGUITitle = "Bounties";
    public String bountyCreateGUITitle = "Bounty Create";

    public String invalidAmount = "You need to place at least 10k";
    public String invalidNameOrLore = "The selected items have invalid name or lore";

    public String bountyCreateCoolDown = "You have to wait %minutes% more minutes until you can create a new bounty";

    public List<String> levelMessage = Arrays.asList(
            "You have level %level%",
            "You progress is %kills%/%needed%",
            "Progress: %progress_bar% (%progress_percent%)"
    );

    public List<String> helpCommand = Arrays.asList(
            "/bh create [player] [item/amount] {message}",
            "/bh level",
            "/bh list"
    );

    public String active = "&aActive";
    public String inactive = "&cInactive";

    public String leveledUp = "You have leveled up to level %level%";

    public String bountyCanceled = "Your bounty for %target% has been canceled";
    public String bountyCanceledByYou = "Your have canceled the desired bounty(s) of %player%";

    public String topEntry = "#%place% %player% %level% %kills% %progress_bar% %progress_percent%";

}
