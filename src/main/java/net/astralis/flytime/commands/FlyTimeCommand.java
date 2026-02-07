package net.astralis.flytime.commands;

import net.astralis.flytime.Main;
import net.astralis.flytime.models.PlayerModel;
import net.astralis.flytime.service.FlyTimeService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class FlyTimeCommand implements CommandExecutor {

    private static final String PREFIX = "§x§2§2§D§3§E§Eʟ§x§2§B§B§D§F§0ᴜ§x§3§3§A§7§F§2ᴍ§x§3§B§8§2§F§6ᴀ§x§6§0§7§3§F§6x§x§8§4§6§4§F§6ɪ§x§A§8§5§5§F§7ᴀ §7✦ ";
    private final FlyTimeService flyTimeService;
    private final Main plugin;

    public FlyTimeCommand(Main plugin, FlyTimeService flyTimeService) {
        this.plugin = plugin;
        this.flyTimeService = flyTimeService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ɴᴜʀ sᴘɪᴇʟᴇʀ ᴋöɴɴᴇɴ ɪʜʀᴇ ғʟʏᴛɪᴍᴇ ᴇɪɴsᴇʜᴇɴ.");
                return true;
            }
            PlayerModel model = flyTimeService.getModel(player.getUniqueId());
            if (model == null) {
                player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ғᴇʜʟᴇʀ ʙᴇɪᴍ ʟᴀᴅᴇɴ ᴅᴇɪɴᴇʀ ᴅᴀᴛᴇɴ.");
                return true;
            }
            player.sendMessage(PREFIX + "§7ᴅᴇɪɴᴇ ғʟʏᴛɪᴍᴇ §8➜ §x§F§5§9§E§0§B" + formatTime(model.getFlyTime()));
            return true;
        }

        if (args[0].equalsIgnoreCase("pay")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ɴᴜʀ sᴘɪᴇʟᴇʀ ᴋöɴɴᴇɴ ғʟʏᴛɪᴍᴇ üʙᴇʀᴛʀᴀɢᴇɴ.");
                return true;
            }
            if (args.length < 3) {
                player.sendMessage(PREFIX + "§7ʙᴇɴᴜᴛᴢᴜɴɢ §8➜ §x§A§8§5§5§F§7/ғʟʏᴛɪᴍᴇ ᴘᴀʏ <sᴘɪᴇʟᴇʀ> <ᴢᴇɪᴛ>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(PREFIX + "§x§E§F§4§4§4§4sᴘɪᴇʟᴇʀ ɴɪᴄʜᴛ ᴏɴʟɪɴᴇ.");
                return true;
            }

            if (target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴅᴜ ᴋᴀɴɴsᴛ ᴅɪʀ ɴɪᴄʜᴛ sᴇʟʙsᴛ ᴢᴇɪᴛ ɢᴇʙᴇɴ.");
                return true;
            }

            try {
                long seconds = Long.parseLong(args[2]);
                if (seconds <= 0) {
                    player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴅɪᴇ ᴢᴇɪᴛ ᴍᴜss ᴘᴏsɪᴛɪᴠ sᴇɪɴ.");
                    return true;
                }

                PlayerModel senderModel = flyTimeService.getModel(player.getUniqueId());
                PlayerModel targetModel = flyTimeService.getModel(target.getUniqueId());

                if (senderModel == null || targetModel == null) {
                    player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ғᴇʜʟᴇʀ ʙᴇɪᴍ ᴢᴜɢʀɪғғ ᴀᴜғ ᴅɪᴇ ᴅᴀᴛᴇɴ.");
                    return true;
                }

                if (senderModel.getFlyTime() < seconds) {
                    player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴅᴜ ʜᴀsᴛ ɴɪᴄʜᴛ ɢᴇɴᴜɢ ғʟʏᴛɪᴍᴇ.");
                    return true;
                }

                senderModel.removeFlyTime(seconds);
                targetModel.addFlyTime(seconds);

                flyTimeService.savePlayerAsync(player.getUniqueId());
                flyTimeService.savePlayerAsync(target.getUniqueId());

                player.sendMessage(PREFIX + "§7ᴅᴜ ʜᴀsᴛ §x§F§5§9§E§0§B" + formatTime(seconds) + " §7ғʟʏᴛɪᴍᴇ ᴀɴ §x§3§B§8§2§F§6" + target.getName() + " §7ɢᴇɢᴇʙᴇɴ.");
                target.sendMessage(PREFIX + "§7ᴅᴜ ʜᴀsᴛ §x§F§5§9§E§0§B" + formatTime(seconds) + " §7ғʟʏᴛɪᴍᴇ ᴠᴏɴ §x§3§B§8§2§F§6" + player.getName() + " §7ᴇʀʜᴀʟᴛᴇɴ.");

            } catch (NumberFormatException e) {
                player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴜɴɢüʟᴛɪɢᴇ ᴢᴇɪᴛ-ᴀɴɢᴀʙᴇ.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("flytime.admin")) {
                sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴋᴇɪɴᴇ ʀᴇᴄʜᴛᴇ.");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(PREFIX + "§7ʙᴇɴᴜᴛᴢᴜɴɢ §8➜ §x§A§8§5§5§F§7/ғʟʏᴛɪᴍᴇ ᴀᴅᴅ <sᴘɪᴇʟᴇʀ> <ᴢᴇɪᴛ>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4sᴘɪᴇʟᴇʀ ɴɪᴄʜᴛ ᴏɴʟɪɴᴇ.");
                return true;
            }

            try {
                long seconds = Long.parseLong(args[2]);
                PlayerModel model = flyTimeService.getModel(target.getUniqueId());
                if (model == null) {
                    sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ғᴇʜʟᴇʀ ʙᴇɪᴍ ʟᴀᴅᴇɴ ᴅᴇs sᴘɪᴇʟᴇʀ-ᴍᴏᴅᴇʟʟs.");
                    return true;
                }
                model.addFlyTime(seconds);
                flyTimeService.savePlayerAsync(target.getUniqueId());

                sender.sendMessage(PREFIX + "§x§2§2§D§3§E§E" + formatTime(seconds) + " §7ғʟʏᴛɪᴍᴇ ᴢᴜ §x§3§B§8§2§F§6" + target.getName() + " §7ʜɪɴᴢᴜɢᴇғüɢᴛ.");
                target.sendMessage(PREFIX + "§7ᴅɪʀ ᴡᴜʀᴅᴇɴ §x§2§2§D§3§E§E" + formatTime(seconds) + " §7ғʟʏᴛɪᴍᴇ ʜɪɴᴢᴜɢᴇғüɢᴛ.");
            } catch (NumberFormatException e) {
                sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴜɴɢüʟᴛɪɢᴇ ᴢᴇɪᴛ.");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("coupon")) {
            if (!sender.hasPermission("flytime.admin")) {
                sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴋᴇɪɴᴇ ʀᴇᴄʜᴛᴇ.");
                return true;
            }

            if (!(sender instanceof Player player)) {
                sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ɴᴜʀ sᴘɪᴇʟᴇʀ ᴋöɴɴᴇɴ ɢᴜᴛsᴄʜᴇɪɴᴇ ᴇʀsᴛᴇʟʟᴇɴ.");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(PREFIX + "§7ʙᴇɴᴜᴛᴢᴜɴɢ §8➜ §x§A§8§5§5§F§7/ғʟʏᴛɪᴍᴇ ᴄᴏᴜᴘᴏɴ <ᴀɴᴢᴀʜʟ> <s|ᴍ|ʜ>");
                return true;
            }

            try {
                long amount = Long.parseLong(args[1]);
                String unit = args[2].toLowerCase();
                long seconds;

                switch (unit) {
                    case "s", "seconds", "sekunden" -> seconds = amount;
                    case "m", "minutes", "minuten", "ᴍ" -> seconds = amount * 60;
                    case "h", "hours", "stunden", "ʜ" -> seconds = amount * 3600;
                    default -> {
                        sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴜɴɢüʟᴛɪɢᴇ ᴇɪɴʜᴇɪᴛ §8(§7s|ᴍ|ʜ§8).");
                        return true;
                    }
                }

                if (seconds <= 0) {
                    sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴅɪᴇ ᴢᴇɪᴛ ᴍᴜss ᴘᴏsɪᴛɪᴠ sᴇɪɴ.");
                    return true;
                }

                ItemStack coupon = createCoupon(seconds);
                player.getInventory().addItem(coupon);
                sender.sendMessage(PREFIX + "§x§2§2§D§3§E§Eɢᴜᴛsᴄʜᴇɪɴ ᴇʀsᴛᴇʟʟᴛ §8➜ §x§3§B§8§2§F§6" + formatTime(seconds));

            } catch (NumberFormatException e) {
                sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴜɴɢüʟᴛɪɢᴇ ᴀɴᴢᴀʜʟ.");
            }
            return true;
        }

        sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴜɴʙᴇᴋᴀɴɴᴛᴇʀ ʙᴇғᴇʜʟ.");
        return true;
    }

    private ItemStack createCoupon(long seconds) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§x§2§2§D§3§E§Eғʟʏᴛɪᴍᴇ ɢᴜᴛsᴄʜᴇɪɴ");
            List<String> lore = new ArrayList<>();
            lore.add("§7ᴡᴇʀᴛ §8➜ §x§3§B§8§2§F§6" + formatTime(seconds));
            lore.add("");
            lore.add("§7ʀᴇᴄʜᴛsᴋʟɪᴄᴋ ᴢᴜᴍ ᴇɪɴʟösᴇɴ");
            meta.setLore(lore);

            NamespacedKey key = new NamespacedKey(plugin, "flytime_seconds");
            meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, seconds);

            item.setItemMeta(meta);
        }
        return item;
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
