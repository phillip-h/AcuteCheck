package com.github.phillip.h.acutecheck.step;

import org.bukkit.command.CommandSender;

import java.util.Optional;

public interface Step {

    Optional<Step> next(CommandSender sender);

    Step then(Step next);

    void input(Object input);

    boolean requiresInput();

    Step copy();

}
