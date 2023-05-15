package dev.caveatemptor.imagetoblock;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.codehaus.plexus.util.TypeFormat.parseInt;
import static org.bukkit.Material.*;

import static dev.caveatemptor.imagetoblock.LoadImageFromURL.*;

public class Image implements CommandExecutor {
    ImageToBlock plugin = ImageToBlock.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BufferedImage img = getImage();

        int x = 0;
        int y = 0;
        int z = 0;
        try {
            x = parseInt(args[0]);
            y = parseInt(args[1]);
            z = parseInt(args[2]);
        } catch (Exception ignored) {}

        int imgX = 0;
        int imgY = 0;
        for (int i = y; i < y + img.getHeight() / 8; i++) {
            for (int j = x; j < x + img.getWidth() / 8; j++) {
                Material block = WHITE_CONCRETE;
                Color pixelColor = new Color(img.getRGB(imgX, imgY));

                if (pixelColor.getRed() >= pixelColor.getBlue() && pixelColor.getRed() >= pixelColor.getGreen())
                    block = RED_CONCRETE;
                if (pixelColor.getGreen() >= pixelColor.getBlue() && pixelColor.getRed() >= pixelColor.getRed())
                    block = GREEN_CONCRETE;
                if (pixelColor.getBlue() >= pixelColor.getRed() && pixelColor.getBlue() >= pixelColor.getGreen())
                    block = BLUE_CONCRETE;

                plugin.getServer().getWorlds().get(0).getBlockAt(j, i, z).setType(block);

                imgX++;
            }
            imgY++;
            imgX = 0;
        }

        return true;
    }
}
