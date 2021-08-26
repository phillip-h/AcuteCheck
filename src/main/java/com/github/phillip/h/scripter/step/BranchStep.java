package com.github.phillip.h.scripter.step;

import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

class BranchStep implements Step {

    private final Map<String, Step> branches = new HashMap<>();
    private final String primaryBranch;
    private String selectedBranch;

    BranchStep(String primaryBranch) {
        Objects.requireNonNull(primaryBranch, "Null primaryBranch");
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

    @Override
    public Step copy() {
        final BranchStep copy = new BranchStep(primaryBranch);
        for (Map.Entry<String, Step> branch : branches.entrySet()) {
            copy.branches.put(branch.getKey(), branch.getValue().copy());
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BranchStep that = (BranchStep) o;
        return Objects.equals(branches, that.branches) &&
                Objects.equals(primaryBranch, that.primaryBranch) &&
                Objects.equals(selectedBranch, that.selectedBranch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branches, primaryBranch, selectedBranch);
    }
}
