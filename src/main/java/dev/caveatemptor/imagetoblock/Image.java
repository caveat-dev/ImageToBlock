package dev.caveatemptor.imagetoblock;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Array;
import java.util.*;
import java.util.List;

import static dev.caveatemptor.imagetoblock.LoadImageFromURL.getImage;
import static java.lang.Integer.parseInt;
import static org.bukkit.Material.*;

public class Image implements CommandExecutor {
    ImageToBlock plugin = ImageToBlock.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        BufferedImage img = getImage();

        getBlockClosestInColor(new Color(0, 0, 0));

        /*
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

                // plugin.getServer().getWorlds().get(0).getBlockAt(j, i, z).setType(block);

                imgX++;
            }
            imgY++;
            imgX = 0;
        }
        */

        return true;
    }

    private Material getBlockClosestInColor(Color pixelColor) {
        Map<String, Object> blockList = plugin.getConfig().getConfigurationSection("blocks").getValues(false);
        Object[] blockListValues = blockList.values().toArray();

        return null;
    }
}
