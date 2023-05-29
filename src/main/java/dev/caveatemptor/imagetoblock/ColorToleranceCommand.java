package dev.caveatemptor.imagetoblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static dev.caveatemptor.imagetoblock.ImageToBlock.getColorTolerance;
import static dev.caveatemptor.imagetoblock.ImageToBlock.setColorTolerance;
import static dev.caveatemptor.imagetoblock.Message.sendMessageAndReturn;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ColorToleranceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 1)
            return sendMessageAndReturn(sender, "Too many arguments", RED);

        if (args.length == 0)
            return sendMessageAndReturn(sender, "Color tolerance: " + getColorTolerance());

        int value;
        try {
            value = Integer.parseInt(args[0]);
        } catch (Exception e) {
            return sendMessageAndReturn(sender, "Not a valid number", RED);
        }

        if (setColorTolerance(value))
            return sendMessageAndReturn(sender, "Color tolerance set", GREEN);
        return sendMessageAndReturn(sender, "Not a valid number", RED);
    }
}
