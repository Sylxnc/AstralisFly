package net.astralis.flytime.listeners;

import net.astralis.flytime.service.FlyTimeService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
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
}
