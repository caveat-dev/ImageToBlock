package dev.caveatemptor.imagetoblock;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.Map;

import static dev.caveatemptor.imagetoblock.LoadImageFromURL.getImage;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static org.bukkit.Material.*;

public class Image implements CommandExecutor {
    ImageToBlock plugin = ImageToBlock.getInstance();
    FileConfiguration config = plugin.getConfig();
    Server server = plugin.getServer();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 3)
            return true;

        BufferedImage img = getImage();
        ArrayList<BufferedImage> imgChunks = new ArrayList<>();

        int imgChunkX = 0;
        int imgChunkY = 0;

        while (true) {
            try {
                imgChunks.add(img.getSubimage(imgChunkX, imgChunkY, 96, 96));
                imgChunkX += 96;
                imgChunkY += 96;
            }
            catch (RasterFormatException e) {
                imgChunkX++;
                imgChunkY++;
                imgChunks.add(img.getSubimage(imgChunkX, imgChunkY,img.getWidth() - imgChunkX, img.getHeight() - imgChunkY));
                break;
            }
        }

        int originX;
        int originY;
        int originZ;
        try {
            originX = parseInt(args[0]);
            originY = parseInt(args[1]);
            originZ = parseInt(args[2]);
        } catch (Exception ignored) {
            return true;
        }

        int placeAtX = originX;

        int placeAtY = originY;

        int imgX = 0;
        int imgY = 0;
        for (BufferedImage imgChunk : imgChunks) {

            for (int i = placeAtY; i < placeAtY + imgChunk.getHeight(); i++) {
                for (int j = placeAtX; j < placeAtX + imgChunk.getWidth(); j++) {
                    Color pixelColor = new Color(img.getRGB(imgX, imgY));

                    Material blockToPlace = getBlockClosestInColor(pixelColor);

                    server.getWorlds().get(0).getBlockAt(j, i, originZ).setType(blockToPlace);

                    imgX++;
                }
                imgY++;
                imgX = 0;
            }
            sender.sendMessage(Component.text("Chunk placed!"));
            placeAtX += imgChunk.getWidth() + 1;
            if (placeAtX - originX >= img.getWidth()) {
                placeAtX = originX;
                placeAtY += imgChunk.getHeight();
            }
        }

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

            int redDifference;
            int greenDifference;
            int blueDifference;
            try {
                redDifference = abs((int) blockColors.get("r") - pixelColor.getRed());
                greenDifference = abs((int) blockColors.get("g") - pixelColor.getGreen());
                blueDifference = abs((int) blockColors.get("b") - pixelColor.getBlue());
            }
            catch (IllegalArgumentException e) {
                System.out.println("Block name " + materialName + " invalid");
                continue;
            }

            float averageDifference = (redDifference + greenDifference + blueDifference) / 3.0f;

            if (averageDifference < lowestAverageDifference) {
                closestBlockInColor = material;
                lowestAverageDifference = averageDifference;
            }
        }

        return closestBlockInColor;
    }
}
