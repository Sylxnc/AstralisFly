package net.astralis.flytime.commands;

import net.astralis.flytime.Main;
import net.astralis.flytime.models.PlayerModel;
import net.astralis.flytime.service.FlyTimeService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class FlyTimeVoteCommand implements CommandExecutor {

    private static final String PREFIX = "§x§2§2§D§3§E§Eʟ§x§2§B§B§D§F§0ᴜ§x§3§3§A§7§F§2ᴍ§x§3§B§8§2§F§6ᴀ§x§6§0§7§3§F§6x§x§8§4§6§4§F§6ɪ§x§A§8§5§5§F§7ᴀ §7✦ ";
    private final Main plugin;
    private final FlyTimeService flyTimeService;

    public FlyTimeVoteCommand(Main plugin, FlyTimeService flyTimeService) {
        this.plugin = plugin;
        this.flyTimeService = flyTimeService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("flytime.admin")) {
            sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴋᴇɪɴᴇ ʀᴇᴄʜᴛᴇ.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(PREFIX + "§7ʙᴇɴᴜᴛᴢᴜɴɢ §8➜ §x§A§8§5§5§F§7/ғʟʏᴛɪᴍᴇᴠᴏᴛᴇ <sᴘɪᴇʟᴇʀ>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4sᴘɪᴇʟᴇʀ ɴɪᴄʜᴛ ᴏɴʟɪɴᴇ.");
            return true;
        }

        ConfigurationSection groupsSection = plugin.getConfig().getConfigurationSection("vote-groups");
        if (groupsSection == null) {
            sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ғᴇʜʟᴇʀ ʙᴇɪ ᴅᴇʀ ᴋᴏɴғɪɢᴜʀᴀᴛɪᴏɴ.");
            return true;
        }

        long minutesToAdd = 0;
        String groupName = "keine";

        for (String key : groupsSection.getKeys(false)) {
            String permission = groupsSection.getString(key + ".permission");
            if (permission != null && target.hasPermission(permission)) {
                long minutes = groupsSection.getLong(key + ".minutes");
                if (minutes > minutesToAdd) {
                    minutesToAdd = minutes;
                    groupName = key;
                }
            }
        }

        if (minutesToAdd <= 0) {
            sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴅᴇʀ sᴘɪᴇʟᴇʀ ʜᴀᴛ ᴋᴇɪɴᴇ ʙᴇʀᴇᴄʜᴛɪɢᴜɴɢ.");
            return true;
        }

        PlayerModel model = flyTimeService.getModel(target.getUniqueId());
        if (model == null) {
            sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ғᴇʜʟᴇʀ ʙᴇɪᴍ ʟᴀᴅᴇɴ ᴅᴇs ᴍᴏᴅᴇʟʟs.");
            return true;
        }

        long secondsToAdd = minutesToAdd * 60;
        model.addFlyTime(secondsToAdd);
        flyTimeService.savePlayerAsync(target.getUniqueId());

        sender.sendMessage(PREFIX + "§x§3§B§8§2§F§6" + target.getName() + " §7ʜᴀᴛ §x§2§2§D§3§E§E" + minutesToAdd + "ᴍ §7ғʟʏᴛɪᴍᴇ ᴇʀʜᴀʟᴛᴇɴ.");
        target.sendMessage(PREFIX + "§7ᴅᴜ ʜᴀsᴛ §x§2§2§D§3§E§E" + minutesToAdd + "ᴍ §7ғʟʏᴛɪᴍᴇ ᴇʀʜᴀʟᴛᴇɴ.");

        return true;
    }
}
