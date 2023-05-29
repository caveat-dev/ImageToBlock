package dev.caveatemptor.imagetoblock;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public class Message {
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message));
    }

    public static void sendMessage(CommandSender sender, String message, TextColor color) {
        sender.sendMessage(Component.text(message).color(color));
    }

    public static boolean sendMessageAndReturn(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message));
        return true;
    }

    public static boolean sendMessageAndReturn(CommandSender sender, String message, TextColor color) {
        sender.sendMessage(Component.text(message).color(color));
        return true;
    } // TODO: remove redundant functions
}
