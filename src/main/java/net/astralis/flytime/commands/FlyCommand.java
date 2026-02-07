package net.astralis.flytime.commands;

import net.astralis.flytime.models.PlayerModel;
import net.astralis.flytime.service.FlyTimeService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FlyCommand implements CommandExecutor {

    private static final String PREFIX = "§x§2§2§D§3§E§Eʟ§x§2§B§B§D§F§0ᴜ§x§3§3§A§7§F§2ᴍ§x§3§B§8§2§F§6ᴀ§x§6§0§7§3§F§6x§x§8§4§6§4§F§6ɪ§x§A§8§5§5§F§7ᴀ §7✦ ";
    private final FlyTimeService flyTimeService;

    public FlyCommand(FlyTimeService flyTimeService) {
        this.flyTimeService = flyTimeService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(PREFIX + "§x§E§F§4§4§4§4ɴᴜʀ sᴘɪᴇʟᴇʀ ᴋöɴɴᴇɴ ᴅɪᴇsᴇɴ ʙᴇғᴇʜʟ ɴᴜᴛᴢᴇɴ.");
            return true;
        }

        PlayerModel model = flyTimeService.getModel(player.getUniqueId());

        if (model == null || !model.hasFlyTime()) {
            player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ᴅᴜ ʜᴀsᴛ ᴋᴇɪɴᴇ ғʟʏᴛɪᴍᴇ ᴍᴇʜʀ.");
            return true;
        }

        if (model.isEnabled()) {
            model.stopFlyTime();
            player.setFlying(false);
            player.setAllowFlight(false);
            player.sendMessage(PREFIX + "§x§E§F§4§4§4§4ғʟʏ ᴅᴇᴀᴋᴛɪᴠɪᴇʀᴛ.");
        } else {
            // Fly an
            model.startFlyTime();
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(PREFIX + "§x§2§2§D§3§E§Eғʟʏ ᴀᴋᴛɪᴠɪᴇʀᴛ §8➜ §x§3§B§8§2§F§6" + model.getFlyTime() + "s");
        }

        return true;
    }
}
