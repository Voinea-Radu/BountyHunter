package dev.lightdream.bountyhunter.utils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
public class ItemBuilder {

    private ItemStack stack;

    public ItemBuilder(Material mat) {
        stack = new ItemStack(mat);
    }

    public ItemBuilder(Material mat, short sh) {
        stack = new ItemStack(mat, 1, sh);
    }

    public ItemBuilder (ItemStack stack){
        this.stack = stack;
    }

    public ItemMeta getItemMeta() {
        return stack.getItemMeta();
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setColor(Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();
        meta.setColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            ItemMeta meta = getItemMeta();
            for (Enchantment enchantment : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchantment);
            }
        }
        return this;
    }

    /*
    Not for 1.8.8
    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = stack.getItemMeta();
        stack.setItemMeta(meta);
        return this;
    }
    */

    public ItemBuilder setBannerColor(DyeColor color) {
        BannerMeta meta = (BannerMeta) stack.getItemMeta();
        meta.setBaseColor(color);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setHead(String owner) {
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(owner);
        setItemMeta(meta);
        return this;
    }

    /*
    Not for 1.8.8
    public ItemBuilder setHead(OfflinePlayer owner) {
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwningPlayer(owner);
        setItemMeta(meta);
        return this;
    }
    */

    public ItemBuilder setDisplayName(String displayname) {
        ItemMeta meta = getItemMeta();
        if(meta==null){
            return this;
        }
        meta.setDisplayName(displayname);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setItemStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder setLore(ArrayList<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        if (lore.size() == 0) {
            return this;
        }
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String lore) {
        ArrayList<String> loreList = new ArrayList<>();
        loreList.add(lore);
        ItemMeta meta = getItemMeta();
        meta.setLore(loreList);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLore(String[] lore) {
        ArrayList<String> loreList = new ArrayList<>();
        Collections.addAll(loreList, lore);
        ItemMeta meta = getItemMeta();
        meta.setLore(loreList);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addNBTString(String attribute, String value) {
        stack = Utils.setNBT(stack, attribute, value);
        return this;
    }

    public ItemBuilder addNBTInt(String attribute, int value) {
        stack = Utils.setNBT(stack, attribute, value);
        return this;
    }

    public ItemBuilder addNBTStringList(String attribute, List<String> value) {
        stack = Utils.setNBT(stack, attribute, value);
        return this;
    }

    public ItemBuilder setHeadTexture(String value) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", value));
        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
        this.stack = head;
        return this;
    }

    public ItemStack build() {
        return stack;
    }

}
