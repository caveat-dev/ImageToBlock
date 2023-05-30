package dev.caveatemptor.imagetoblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static dev.caveatemptor.imagetoblock.ImageToBlock.*;
import static dev.caveatemptor.imagetoblock.Message.sendMessage;
import static dev.caveatemptor.imagetoblock.Message.sendMessageAndReturn;
import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public class SizeLimitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, "Width limit: " + getWidthLimit());
            sendMessage(sender, "Height limit: " + getHeightLimit());
            return true;
        }
        else if (args.length < 2)
            return sendMessageAndReturn(sender, "Invalid arguments", RED);
        else if (args.length > 2)
            return sendMessageAndReturn(sender, "Too many arguments", RED);

        int newSizeLimit;
        try {
            newSizeLimit = parseInt(args[1]);
        } catch (Exception e) {
            return sendMessageAndReturn(sender, "Not a number", RED);
        }

        switch (args[0]) {
            case "width" -> {
                if (setWidthLimit(newSizeLimit)) {
                    sendMessage(sender, "Max width set", GREEN);
                    break;
                }

                sendMessageAndReturn(sender, "Invalid number", RED);
            }
            case "height" -> {
                if (setHeightLimit(newSizeLimit)) {
                    sendMessage(sender, "Max height set", GREEN);
                    break;
                }

                sendMessageAndReturn(sender, "Invalid number", RED);
            }
            case "reset" -> {
                int defaultSizeLimit = (int)  requireNonNull(config.get("defaultSizeLimit"));

                setWidthLimit(defaultSizeLimit);
                setHeightLimit(defaultSizeLimit);

                sendMessage(sender, "Width and Height limits set to " + defaultSizeLimit, GREEN);
            }
            default -> sendMessageAndReturn(sender, "Invalid arguments", RED);
        }

        return true;
    }
}
