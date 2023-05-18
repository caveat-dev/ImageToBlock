package dev.caveatemptor.imagetoblock;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.Array;
import java.util.*;
import java.util.List;

import static dev.caveatemptor.imagetoblock.LoadImageFromURL.getImage;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static org.bukkit.Material.*;

public class Image implements CommandExecutor {
    ImageToBlock plugin = ImageToBlock.getInstance();
    FileConfiguration config = plugin.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 3)
            return true;

        BufferedImage img = getImage();

        int x = 0;
        int y = 0;
        int z = 0;
        try {
            x = parseInt(args[0]);
            y = parseInt(args[1]);
            z = parseInt(args[2]);
        } catch (Exception ignored) {
            return true;
        }

        int imgX = 0;
        int imgY = 0;
        for (int i = y; i < y + img.getHeight() / 8; i++) {
            for (int j = x; j < x + img.getWidth() / 8; j++) {
                Color pixelColor = new Color(img.getRGB(imgX, imgY));

                Material blockToPlace = getBlockClosestInColor(pixelColor);

                plugin.getServer().getWorlds().get(0).getBlockAt(j, i, z).setType(blockToPlace);

                imgX++;
            }
            imgY++;
            imgX = 0;
        }

        sender.sendMessage(Component.text("Width: " + img.getWidth() / 8 + " Height: " + img.getWidth() / 8));

        return true;
    }

    private Material getBlockClosestInColor(Color pixelColor) {
        Map<String, Object> blockNames = config.getConfigurationSection("blocks").getValues(false);

        float lowestAverageDifference = 999;
        Material closestBlockInColor = null;

        for (Material material : Material.values()) {

            String materialName = material.key().toString().replace("minecraft:", "").toUpperCase();
            if (!blockNames.containsKey(materialName))
                continue;

            Map<String, Object> blockColors = config.getConfigurationSection("blocks." + materialName).getValues(false);

            int redDifference = abs((int) blockColors.get("r") - pixelColor.getRed());
            int greenDifference = abs((int) blockColors.get("g") - pixelColor.getGreen());
            int blueDifference = abs((int) blockColors.get("b") - pixelColor.getBlue());

            float averageDifference = (redDifference + greenDifference + blueDifference) / 3.0f;

            if (averageDifference < lowestAverageDifference) {
                closestBlockInColor = material;
                lowestAverageDifference = averageDifference;
            }
        }

        return closestBlockInColor;
    }
}
