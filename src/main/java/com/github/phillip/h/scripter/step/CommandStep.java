package com.github.phillip.h.scripter.step;

import com.github.phillip.h.acutelib.util.Checks;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class CommandStep extends ContinuableStep {

    private final String commandLine;

    CommandStep(String commandLine) {
        Checks.requireNonEmpty(commandLine, "Empty commandLine");
        this.commandLine = commandLine;
    }

    @Override
    void doNext(CommandSender sender) {
        Bukkit.dispatchCommand(sender, commandLine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CommandStep that = (CommandStep) o;
        return Objects.equals(commandLine, that.commandLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), commandLine);
    }

    @Override
    public String toString() {
        return "CommandStep{" +
                "commandLine='" + commandLine + '\'' +
                '}';
    }
}
