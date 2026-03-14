package net.astralis.flytime.listeners;

import net.astralis.flytime.Main;
import net.astralis.flytime.models.PlayerModel;
import net.astralis.flytime.service.FlyTimeService;
import net.astralis.flytime.util.Chat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CouponListener implements Listener {

    private final FlyTimeService flyTimeService;
    private final NamespacedKey couponKey;

    public CouponListener(Main plugin, FlyTimeService flyTimeService) {
        this.flyTimeService = flyTimeService;
        this.couponKey = new NamespacedKey(plugin, "flytime_seconds");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;

        Long seconds = item.getItemMeta().getPersistentDataContainer().get(couponKey, PersistentDataType.LONG);
        if (seconds == null) return;

        Player player = event.getPlayer();
        PlayerModel model = flyTimeService.getOrLoadModel(player.getUniqueId());

        if (model == null) {
            player.sendMessage(Chat.PREFIX + "Could not load your profile.");
            return;
        }

        // Gutschein einlösen
        item.setAmount(item.getAmount() - 1);
        model.addFlyTime(seconds);
        flyTimeService.savePlayerAsync(player.getUniqueId());

        player.sendMessage(Chat.PREFIX + "Coupon redeemed: " + Chat.formatTime(seconds));
        event.setCancelled(true);
    }
}
