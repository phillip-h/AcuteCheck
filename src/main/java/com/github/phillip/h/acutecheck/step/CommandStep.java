package com.github.phillip.h.acutecheck.step;

import com.github.phillip.h.acutelib.util.Checks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

class CommandStep extends ContinuableStep {

    private final String commandLine;
    private final Plugin plugin;

    CommandStep(final String commandLine, final Plugin plugin) {
        Checks.requireNonEmpty(commandLine, "Empty commandLine");
        this.plugin = Objects.requireNonNull(plugin);
        this.commandLine = commandLine;
    }

    @Override
    void doNext(CommandSender sender) {
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(sender, commandLine));
    }

    @Override
    Step copySelf() {
        return new CommandStep(commandLine, plugin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommandStep that = (CommandStep) o;
        return Objects.equals(commandLine, that.commandLine) &&
                Objects.equals(plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commandLine, plugin);
    }

    @Override
    public String toString() {
        return "CommandStep{" +
                "commandLine='" + commandLine + '\'' +
                ", plugin=" + plugin +
                '}';
    }
}
