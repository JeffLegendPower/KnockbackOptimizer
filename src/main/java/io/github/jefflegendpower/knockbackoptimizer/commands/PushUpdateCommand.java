package io.github.jefflegendpower.knockbackoptimizer.commands;

import io.github.jefflegendpower.knockbackoptimizer.KnockbackOptimizer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PushUpdateCommand implements CommandExecutor {

    private KnockbackOptimizer plugin;

    public PushUpdateCommand(KnockbackOptimizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.isOp()) {
            commandSender.sendMessage("§cYou don't have permission to use this command.");
            return false;
        } else {
            commandSender.sendMessage("§aPushing update...");
            plugin.pushUpdate();
            return true;
        }
    }
}
