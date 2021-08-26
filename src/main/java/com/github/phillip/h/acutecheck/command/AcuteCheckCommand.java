package com.github.phillip.h.acutecheck.command;

import com.github.phillip.h.acutelib.commands.CommandHandler;
import com.github.phillip.h.acutelib.util.Checks;
import org.bukkit.command.CommandSender;

public abstract class AcuteCheckCommand<T extends CommandSender> implements CommandHandler<T> {

    private final String permission;

    public AcuteCheckCommand(String permission) {
        this.permission = Checks.requireNonEmpty(permission, "empty permission string");
    }

    @Override
    public final void handle(final T sender, final String[] args) {
        if (permissibleFor(sender)) {
            doHandle(sender, args);
        } else {
            sender.sendMessage("You don't have permission!");
        }
    }

    abstract void doHandle(T t, String[] args);

    @Override
    public final boolean permissibleFor(final CommandSender t) {
        return t.hasPermission(permission);
    }
}
