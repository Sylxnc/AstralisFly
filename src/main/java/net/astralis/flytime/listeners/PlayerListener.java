package net.astralis.flytime.listeners;

import net.astralis.flytime.service.FlyTimeService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final FlyTimeService flyTimeService;

    public PlayerListener(FlyTimeService flyTimeService) {
        this.flyTimeService = flyTimeService;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        flyTimeService.loadPlayer(event.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        flyTimeService.saveAndUnloadPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandOverride(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] args = message.split(" ");
        String cmd = args[0].toLowerCase();

        if (cmd.equals("/fly") || cmd.equals("/essentials:fly") || cmd.equals("/lumaxiaessentials:fly")) {
            event.setCancelled(true);

            StringBuilder commandBuilder = new StringBuilder("astralisfly:fly");
            for (int i = 1; i < args.length; i++) {
                commandBuilder.append(" ").append(args[i]);
            }

            event.getPlayer().performCommand(commandBuilder.toString());
        }
    }
}
