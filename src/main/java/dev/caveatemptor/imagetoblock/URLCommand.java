package dev.caveatemptor.imagetoblock;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static dev.caveatemptor.imagetoblock.ImageToBlock.*;
import static dev.caveatemptor.imagetoblock.Message.*;
import static java.nio.charset.StandardCharsets.UTF_16;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class URLCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length < 1)
            return sendMessageAndReturn(sender, "No URL provided", RED);
        else if (args.length != 1)
            return sendMessageAndReturn(sender, "Too many arguments", RED);

        // get url, return if invalid
        try {
            url = new URL(args[0]);
        } catch (MalformedURLException ignored) {
            url = null;
            return sendMessageAndReturn(sender, "Not a URL", RED);
        }

        // try url, return if image isn't gettable
        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            url = null;
            return sendMessageAndReturn(sender, "Cannot get image, invalid URL", RED);
        }

        // set the url in the config
        config.set("url", url.toString());
        plugin.saveConfig();

        return sendMessageAndReturn(sender, "Image URL set!", GREEN);
    }
}