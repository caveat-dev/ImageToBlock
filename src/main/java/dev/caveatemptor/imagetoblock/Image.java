package dev.caveatemptor.imagetoblock;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

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

        int imgX = 0;
        int imgY = img.getHeight() - 1;
        for (int y = originY; y < y + img.getHeight() - 1; y++) {
            for (int x = originX; x < originX + img.getWidth() - 1; x++) {
                Color pixelColor = new Color(img.getRGB(imgX, imgY));

                Material blockToPlace = getBlockClosestInColor(pixelColor);

                plugin.getServer().getWorlds().get(0).getBlockAt(x, y, originZ).setType(blockToPlace);

                imgX++;
            }
            sender.sendMessage(Component.text("Row " + (imgY+1) + " placed"));
            imgY--;
            imgX = 0;
        }

        sender.sendMessage(Component.text("Finished"));

        return true;
    }


    private BufferedImage getImage() {
        URL url = null;
        try {
            // url = new URL("https://img.freepik.com/premium-vector/color-spectrum-palette-hue-brightness-black-background-vector-illustration_522680-171.jpg?w=2000");
            url = new URL("https://cdn.discordapp.com/attachments/1083975218407690350/1108861326941495386/testimage.png");
        } catch (MalformedURLException ignored) {
            System.out.println("bad url");
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            System.out.println("some other error");
        }

        return img;
    }


    private Material getBlockClosestInColor(Color pixelColor) {
        Map<String, Object> blockNames = config.getConfigurationSection("blocks").getValues(false);

        float lowestAverageDifference = 999;
        Material closestBlockInColor = null;

        for (Material material : Material.values()) {

            String materialName = material.key().toString().replace("minecraft:", "").toUpperCase();
            if (!blockNames.containsKey(materialName))
                continue;

            Map<String, Object> blockColors;
            try {
                blockColors = config.getConfigurationSection("blocks." + materialName).getValues(false);
            }
            catch (IllegalArgumentException e) {
                System.out.println("BLOCK NOT FOUND: " + materialName);
                continue;
            }

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
