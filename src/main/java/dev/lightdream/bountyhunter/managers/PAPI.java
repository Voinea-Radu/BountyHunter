package dev.lightdream.bountyhunter.managers;

import dev.lightdream.bountyhunter.BountyHunter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPI extends PlaceholderExpansion {

    private final BountyHunter spigot;

    public PAPI(BountyHunter spigot) {
        this.spigot = spigot;
    }


    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "_LightDream";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bountyhunter";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {

        if (player == null) {
            return null;
        }

        return null;
    }
}
