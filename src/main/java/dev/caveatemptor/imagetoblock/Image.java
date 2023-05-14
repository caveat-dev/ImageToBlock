package dev.caveatemptor.imagetoblock;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.awt.image.BufferedImage;

import static org.codehaus.plexus.util.TypeFormat.parseInt;
import static org.bukkit.Material.*;

import static dev.caveatemptor.imagetoblock.LoadImageFromURL.*;

public class Image implements CommandExecutor {
    ImageToBlock plugin = ImageToBlock.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BufferedImage img = getImage();

        if (args.length < 3) {
            sender.sendMessage(Component.text("Too few arguments"));
            return true;
        }

        int x = 0;
        int y = 0;
        int z = 0;
        try {
            x = parseInt(args[0]);
            y = parseInt(args[1]);
            z = parseInt(args[2]);
        } catch (Exception e) {}

        for (int i = y; i < y + img.getHeight(); i++) {
            for (int o = x; o < x + img.getWidth(); o++) {
                plugin.getServer().getWorlds().get(0).getBlockAt(o, i, z).setType(BEDROCK);
            }
        }

        return true;
    }
}
