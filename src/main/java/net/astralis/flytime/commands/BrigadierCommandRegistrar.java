package net.astralis.flytime.commands;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.astralis.flytime.Main;
import net.astralis.flytime.models.PlayerModel;
import net.astralis.flytime.service.FlyTimeService;
import net.astralis.flytime.util.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BrigadierCommandRegistrar {

    private final Main plugin;
    private final FlyTimeService flyTimeService;
    private final NamespacedKey couponKey;

    public BrigadierCommandRegistrar(Main plugin, FlyTimeService flyTimeService) {
        this.plugin = plugin;
        this.flyTimeService = flyTimeService;
        this.couponKey = new NamespacedKey(plugin, "flytime_seconds");
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            registrar.register(buildFlyCommand(), "Toggle fly mode");
            registrar.register(buildFlyTimeCommand(), "Manage fly time");
        });
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildFlyCommand() {
        return Commands.literal("fly")
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Chat.PREFIX + "Only players can use this command.");
                        return 0;
                    }

                    UUID uuid = player.getUniqueId();
                    PlayerModel model = flyTimeService.getOrLoadModel(uuid);
                    if (!model.hasFlyTime()) {
                        sender.sendMessage(Chat.PREFIX + "You do not have any fly time left.");
                        return 0;
                    }

                    if (model.isEnabled()) {
                        model.stopFlyTime();
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        sender.sendMessage(Chat.PREFIX + "Fly mode disabled.");
                    } else {
                        model.startFlyTime();
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        sender.sendMessage(Chat.PREFIX + "Fly mode enabled. Remaining: " + Chat.formatTime(model.getFlyTime()));
                    }
                    return 1;
                });
    }

    private LiteralArgumentBuilder<CommandSourceStack> buildFlyTimeCommand() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("flytime")
                .executes(context -> {
                    CommandSender sender = context.getSource().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Chat.PREFIX + "Only players can check fly time.");
                        return 0;
                    }

                    PlayerModel model = flyTimeService.getOrLoadModel(player.getUniqueId());
                    player.sendMessage(Chat.PREFIX + "Your fly time: " + Chat.formatTime(model.getFlyTime()));
                    return 1;
                });

        root.then(Commands.literal("pay")
                .then(Commands.argument("target", StringArgumentType.word())
                        .then(Commands.argument("seconds", LongArgumentType.longArg(1L))
                                .executes(context -> {
                                    CommandSender sender = context.getSource().getSender();
                                    if (!(sender instanceof Player player)) {
                                        sender.sendMessage(Chat.PREFIX + "Only players can transfer fly time.");
                                        return 0;
                                    }

                                    String targetName = StringArgumentType.getString(context, "target");
                                    long seconds = LongArgumentType.getLong(context, "seconds");
                                    Player target = Bukkit.getPlayerExact(targetName);
                                    if (target == null) {
                                        sender.sendMessage(Chat.PREFIX + "Target player is not online.");
                                        return 0;
                                    }
                                    if (target.getUniqueId().equals(player.getUniqueId())) {
                                        sender.sendMessage(Chat.PREFIX + "You cannot transfer fly time to yourself.");
                                        return 0;
                                    }

                                    PlayerModel senderModel = flyTimeService.getOrLoadModel(player.getUniqueId());
                                    PlayerModel targetModel = flyTimeService.getOrLoadModel(target.getUniqueId());
                                    if (senderModel.getFlyTime() < seconds) {
                                        sender.sendMessage(Chat.PREFIX + "Not enough fly time.");
                                        return 0;
                                    }

                                    senderModel.removeFlyTime(seconds);
                                    targetModel.addFlyTime(seconds);
                                    flyTimeService.savePlayerAsync(player.getUniqueId());
                                    flyTimeService.savePlayerAsync(target.getUniqueId());

                                    sender.sendMessage(Chat.PREFIX + "Transferred " + Chat.formatTime(seconds) + " to " + target.getName() + ".");
                                    target.sendMessage(Chat.PREFIX + "Received " + Chat.formatTime(seconds) + " from " + player.getName() + ".");
                                    return 1;
                                }))));

        root.then(Commands.literal("add")
                .requires(source -> source.getSender().hasPermission("flytime.admin"))
                .then(Commands.argument("target", StringArgumentType.word())
                        .then(Commands.argument("seconds", LongArgumentType.longArg(1L))
                                .executes(context -> {
                                    String targetName = StringArgumentType.getString(context, "target");
                                    long seconds = LongArgumentType.getLong(context, "seconds");
                                    Player target = Bukkit.getPlayerExact(targetName);
                                    if (target == null) {
                                        context.getSource().getSender().sendMessage(Chat.PREFIX + "Target player is not online.");
                                        return 0;
                                    }

                                    PlayerModel targetModel = flyTimeService.getOrLoadModel(target.getUniqueId());
                                    targetModel.addFlyTime(seconds);
                                    flyTimeService.savePlayerAsync(target.getUniqueId());

                                    context.getSource().getSender().sendMessage(Chat.PREFIX + "Added " + Chat.formatTime(seconds) + " to " + target.getName() + ".");
                                    target.sendMessage(Chat.PREFIX + "You received " + Chat.formatTime(seconds) + " fly time.");
                                    return 1;
                                }))));

        root.then(Commands.literal("coupon")
                .requires(source -> source.getSender().hasPermission("flytime.admin"))
                .then(Commands.argument("amount", LongArgumentType.longArg(1L))
                        .then(Commands.argument("unit", StringArgumentType.word())
                                .executes(context -> {
                                    CommandSender sender = context.getSource().getSender();
                                    if (!(sender instanceof Player player)) {
                                        sender.sendMessage(Chat.PREFIX + "Only players can create coupons.");
                                        return 0;
                                    }

                                    long amount = LongArgumentType.getLong(context, "amount");
                                    String unit = StringArgumentType.getString(context, "unit").toLowerCase();
                                    long seconds = switch (unit) {
                                        case "s", "sec", "seconds" -> amount;
                                        case "m", "min", "minutes" -> amount * 60;
                                        case "h", "hr", "hours" -> amount * 3600;
                                        default -> -1;
                                    };
                                    if (seconds <= 0) {
                                        sender.sendMessage(Chat.PREFIX + "Unit must be one of s/m/h.");
                                        return 0;
                                    }

                                    player.getInventory().addItem(createCoupon(seconds));
                                    sender.sendMessage(Chat.PREFIX + "Created coupon for " + Chat.formatTime(seconds) + ".");
                                    return 1;
                                }))));

        return root;
    }

    private ItemStack createCoupon(long seconds) {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName("FlyTime Coupon");
        List<String> lore = new ArrayList<>();
        lore.add("Value: " + Chat.formatTime(seconds));
        lore.add("Right click to redeem");
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(couponKey, PersistentDataType.LONG, seconds);
        item.setItemMeta(meta);
        return item;
    }
}

