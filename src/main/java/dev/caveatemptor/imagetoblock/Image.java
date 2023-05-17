package dev.caveatemptor.imagetoblock;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.codehaus.plexus.util.TypeFormat.parseInt;
import static org.bukkit.Material.*;

import static dev.caveatemptor.imagetoblock.LoadImageFromURL.*;

public class Image implements CommandExecutor {
    ImageToBlock plugin = ImageToBlock.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        File dir = new File("D:\\MC\\MinecraftBlockColors\\src\\culledBlocks");
        File[] directoryListing = dir.listFiles();
        BufferedImage img = null;

        for (File file : directoryListing) {
            String path = file.getAbsolutePath();

            try {
                img = ImageIO.read(new File(path));
            } catch (IOException ignored) {}

            int averageRed = 0;
            int averageGreen = 0;
            int averageBlue = 0;
            int totalPixels = 0;
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 1; x < img.getWidth(); x++) {
                    Color colorAtPixel = new Color(img.getRGB(x, y));

                    averageRed += colorAtPixel.getRed();
                    averageGreen += colorAtPixel.getGreen();
                    averageBlue += colorAtPixel.getBlue();

                    totalPixels++;
                }
            }
            Color averageColor = new Color(averageRed / totalPixels, averageGreen / totalPixels, averageBlue / totalPixels);

            List<Integer> averageColorList = new ArrayList<>();
            averageColorList.add(averageColor.getRed());
            averageColorList.add(averageColor.getGreen());
            averageColorList.add(averageColor.getBlue());

            String fileName = file.getName();
            fileName = fileName.replace("side", "");
            fileName = fileName.replace(".png", "");
            fileName = fileName.toUpperCase();

            plugin.getConfig().getConfigurationSection("blocks").set(fileName, averageColorList);
            plugin.saveConfig();

            System.out.println(file + ": " + averageColor);
        }

        /* BufferedImage img = getImage();

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
        */

        return true;
    }
}
