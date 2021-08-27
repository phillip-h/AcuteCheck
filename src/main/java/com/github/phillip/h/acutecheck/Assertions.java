package com.github.phillip.h.acutecheck;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Assertions {

    private Assertions() {}

    public static void holdingItem(final CommandSender sender) {
        if (!(sender instanceof Player)) throw new IllegalArgumentException("Sender must be a player");
        final ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
        if (item.getType().isAir()) throw new AssertionError("Sender not holding item");
    }

}
