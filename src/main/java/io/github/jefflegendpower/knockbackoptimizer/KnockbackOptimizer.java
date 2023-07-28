package io.github.jefflegendpower.knockbackoptimizer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.jefflegendpower.knockbackoptimizer.commands.FeedbackCommand;
import io.github.jefflegendpower.knockbackoptimizer.commands.PushUpdateCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class KnockbackOptimizer extends JavaPlugin {

    private Map<Player, Double> feedbackMap = new HashMap<>();
    private KBUtils kbUtils;
    private PythonHandler pythonHandler;

    private boolean needsFeedback = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        kbUtils = new KBUtils();
        getCommand("feedback").setExecutor(new FeedbackCommand(this));
        getCommand("pushupdate").setExecutor(new PushUpdateCommand(this));
        try {
            pythonHandler = new PythonHandler(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // set initial kb settings
        setKB(() -> broadcast("Initial KB settings set!"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean needsFeedback() {
        return needsFeedback;
    }

    public void sendFeedback(double feedback, Player player) {
        feedbackMap.put(player, feedback);
    }

    public void pushUpdate() {
        // get average of all feedback
        double average = feedbackMap.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        JsonObject feedback = new JsonObject();
        feedback.addProperty("feedback", 10 - average);
        pythonHandler.sendMessage(feedback.toString());
        needsFeedback = false;
        feedbackMap.clear();

        setKB(() -> {
            broadcast("KB settings updated!");

            needsFeedback = true;
        });
    }

    private void setKB(Runnable onFinish) {
        new BukkitRunnable() {

            private boolean firstTime = true;

            @Override
            public void run() {
                if (firstTime) {
                    firstTime = false;
                    broadcast("Setting KB settings...");
                }
                String messageString = pythonHandler.getMessage();
                if (messageString.equals("")) return;
                System.out.println("a");
                JsonObject message = new Gson().fromJson(messageString, JsonObject.class);
                kbUtils.setKBSettings(
                        message.get("horizontal").getAsDouble(),
                        message.get("vertical").getAsDouble(),
                        message.get("inheritanceStrengthHorizontal").getAsDouble(),
                        message.get("groundHorizontal").getAsDouble(),
                        message.get("groundVertical").getAsDouble(),
                        message.get("sprintHorizontal").getAsDouble(),
                        message.get("verticalLimit").getAsDouble(),
                        message.get("sprintVertical").getAsDouble(),
                        message.get("attackerSlowdown").getAsDouble()
                );
                needsFeedback = true;
                onFinish.run();
                cancel();
            }
        }.runTaskTimer(this, 0, 10);
    }

    private void broadcast(String message) {
        getLogger().info(message);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
}
