package net.astralis.flytime.listeners;

import net.astralis.flytime.Main;
import net.astralis.flytime.models.PlayerModel;
import net.astralis.flytime.service.FlyTimeService;
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

    private static final String PREFIX = "§x§2§2§D§3§E§Eʟ§x§2§B§B§D§F§0ᴜ§x§3§3§A§7§F§2ᴍ§x§3§B§8§2§F§6ᴀ§x§6§0§7§3§F§6x§x§8§4§6§4§F§6ɪ§x§A§8§5§5§F§7ᴀ §7✦ ";
    private final Main plugin;
    private final FlyTimeService flyTimeService;
    private final NamespacedKey couponKey;

    public CouponListener(Main plugin, FlyTimeService flyTimeService) {
        this.plugin = plugin;
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
        PlayerModel model = flyTimeService.getModel(player.getUniqueId());

        if (model == null) {
            player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ғᴇʜʟᴇʀ ʙᴇɪᴍ ʟᴀᴅᴇɴ ᴅᴇɪɴᴇʀ ᴅᴀᴛᴇɴ.");
            return;
        }

        // Gutschein einlösen
        item.setAmount(item.getAmount() - 1);
        model.addFlyTime(seconds);
        flyTimeService.savePlayerAsync(player.getUniqueId());

        player.sendMessage(PREFIX + "§7ɢᴜᴛsᴄʜᴇɪɴ ᴇɪɴɢᴇʟösᴛ §8➜ §x§2§2§D§3§E§E" + formatTime(seconds));
        event.setCancelled(true);
    }

    private String formatTime(long seconds) {
        if (seconds < 60) return seconds + "s";
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        if (minutes < 60) return minutes + "ᴍ " + (remainingSeconds > 0 ? remainingSeconds + "s" : "");
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        return hours + "ʜ " + (remainingMinutes > 0 ? remainingMinutes + "ᴍ " : "") + (remainingSeconds > 0 ? remainingSeconds + "s" : "");
    }
}
