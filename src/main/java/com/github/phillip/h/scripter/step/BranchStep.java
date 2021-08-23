package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class BranchStep implements Step {

    private final Map<String, Step> branches = new HashMap<>();
    private final String primaryBranch;
    private String selectedBranch;

    BranchStep(String primaryBranch) {
        if (primaryBranch == null) throw new IllegalArgumentException("Null primaryBranch");
        this.primaryBranch = primaryBranch;
    }

    public void addBranch(final String branch, final Step step) {
        branches.put(branch, step);
    }

    @Override
    public final Optional<Step> next(CommandSender sender) {
        if (selectedBranch == null) throw new IllegalStateException("No branch selected");
        return Optional.ofNullable(branches.get(selectedBranch));
    }

    @Override
    public final Step then(Step next) {
        addBranch(primaryBranch, next);
        return next;
    }

    @Override
    public final void input(Object input) {
        if (!(input instanceof String)) throw new IllegalArgumentException("Input is not a string");
        if (!primaryBranch.equals(input) && !branches.containsKey(input)) throw new IllegalArgumentException("Input is not a known branch");
        selectedBranch = (String) input;
    }

    @Override
    public final boolean requiresInput() {
        return selectedBranch == null;
    }
}
