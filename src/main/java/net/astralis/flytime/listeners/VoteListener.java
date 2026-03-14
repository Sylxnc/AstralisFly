package net.astralis.flytime.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.astralis.flytime.Main;
import net.astralis.flytime.service.FlyTimeService;
import net.astralis.flytime.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class VoteListener implements Listener {

    private final Main plugin;
    private final FlyTimeService flyTimeService;

    public VoteListener(Main plugin, FlyTimeService flyTimeService) {
        this.plugin = plugin;
        this.flyTimeService = flyTimeService;
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        Vote vote = event.getVote();
        if (vote == null || vote.getUsername() == null || vote.getUsername().isBlank()) {
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(vote.getUsername());
        UUID uuid = offlinePlayer.getUniqueId();
        long minutesToAdd = resolveVoteMinutes(offlinePlayer);
        if (minutesToAdd <= 0) {
            return;
        }

        long secondsToAdd = minutesToAdd * 60L;
        flyTimeService.addFlyTime(uuid, secondsToAdd);
        flyTimeService.savePlayerAsync(uuid);

        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null) {
            onlinePlayer.sendMessage(Chat.PREFIX + "Vote reward: +" + Chat.formatTime(secondsToAdd));
        }

        plugin.getLogger().info("Processed vote for " + vote.getUsername() + ": +" + secondsToAdd + " seconds");
    }

    private long resolveVoteMinutes(OfflinePlayer player) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("vote-groups");
        if (section == null) {
            return 0L;
        }

        long maxMinutes = 0L;
        for (String group : section.getKeys(false)) {
            String permission = section.getString(group + ".permission", "");
            long minutes = section.getLong(group + ".minutes", 0L);

            if (permission.isBlank()) {
                continue;
            }
            if (player.isOnline() && player.getPlayer() != null && player.getPlayer().hasPermission(permission) && minutes > maxMinutes) {
                maxMinutes = minutes;
            }
        }

        if (maxMinutes > 0) {
            return maxMinutes;
        }

        return section.getLong("default.minutes", 5L);
    }
}

