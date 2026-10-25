package com.supamotd;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SupaMOTD extends JavaPlugin implements Listener {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.builder().character('&').hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private static final Pattern AMPERSAND_HEX = Pattern.compile("(?i)&#([0-9a-f]{6})");
    private static final Pattern AMPERSAND_CODE = Pattern.compile("(?i)&([0-9a-fk-or])");

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SupaMOTD v" + getPluginMeta().getVersion() + " enabled.");
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        String firstLine = getConfig().getString("motd.line-1", "<green>Welcome");
        String secondLine = getConfig().getString("motd.line-2", "<gray>Have fun!");
        String motd = firstLine + "\n" + secondLine;
        event.motd(parseText(motd));
        event.setMaxPlayers(Math.max(0, getConfig().getInt("max-player-count", 3000)));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("motd")) {
            return false;
        }
        if (!sender.hasPermission("motd.use")) {
            sender.sendMessage(parseText("<red>You don't have permission to execute this command!"));
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            sender.sendMessage(parseText("<green>You have successfully reloaded the MOTD!"));
            return true;
        }
        sender.sendMessage(parseText("<green>Please use /motd reload"));
        return true;
    }

    private Component parseText(String input) {
        String converted = input.replace("\\n", "\n");
        Matcher hexMatcher = AMPERSAND_HEX.matcher(converted);
        StringBuffer hexBuffer = new StringBuffer();
        while (hexMatcher.find()) {
            hexMatcher.appendReplacement(hexBuffer, "<#" + hexMatcher.group(1) + ">");
        }
        hexMatcher.appendTail(hexBuffer);
        converted = hexBuffer.toString();

        StringBuilder result = new StringBuilder();
        Matcher codeMatcher = AMPERSAND_CODE.matcher(converted);
        int cursor = 0;
        while (codeMatcher.find()) {
            result.append(converted, cursor, codeMatcher.start());
            result.append(switch (Character.toLowerCase(codeMatcher.group(1).charAt(0))) {
                case '0' -> "<black>"; case '1' -> "<dark_blue>"; case '2' -> "<dark_green>";
                case '3' -> "<dark_aqua>"; case '4' -> "<dark_red>"; case '5' -> "<dark_purple>";
                case '6' -> "<gold>"; case '7' -> "<gray>"; case '8' -> "<dark_gray>";
                case '9' -> "<blue>"; case 'a' -> "<green>"; case 'b' -> "<aqua>";
                case 'c' -> "<red>"; case 'd' -> "<light_purple>"; case 'e' -> "<yellow>";
                case 'f' -> "<white>"; case 'k' -> "<obfuscated>"; case 'l' -> "<bold>";
                case 'm' -> "<strikethrough>"; case 'n' -> "<underlined>"; case 'o' -> "<italic>";
                case 'r' -> "<reset>"; default -> codeMatcher.group();
            });
            cursor = codeMatcher.end();
        }
        result.append(converted.substring(cursor));
        try {
            return MINI_MESSAGE.deserialize(result.toString());
        } catch (Exception exception) {
            getLogger().warning("Invalid MOTD formatting in config.yml; displaying it as plain text.");
            return LEGACY.deserialize(input.replace('§', '&'));
        }
    }
}
