package io.github.jefflegendpower.knockbackoptimizer.commands;

import io.github.jefflegendpower.knockbackoptimizer.KnockbackOptimizer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedbackCommand implements CommandExecutor {

    private KnockbackOptimizer plugin;

    public FeedbackCommand(KnockbackOptimizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player))
            return false;
        Player player = (Player) commandSender;
        if (args.length == 0) {
            player.sendMessage("§cUsage: /feedback <0.00-10.00>");
            return false;
        }
        double feedback = Double.parseDouble(args[0]);
        if (feedback < 0 || feedback > 10) {
            player.sendMessage("§cUsage: /feedback <0.00-10.00>");
            return false;
        }

        if (!plugin.needsFeedback()) {
            player.sendMessage("§cYou don't need to send feedback right now.");
            return false;
        }

        plugin.sendFeedback(feedback, player);
        player.sendMessage("§aFeedback sent!");
        return true;
    }
}
