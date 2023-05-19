package dev.caveatemptor.imagetoblock;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class ImageCommand implements CommandExecutor {
    ImageToBlock plugin = ImageToBlock.getInstance();
    FileConfiguration config = plugin.getConfig();
    Server server = plugin.getServer();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 3)
            return true;

        BufferedImage img = getImage();
        List<BufferedImage> imgLayers = new ArrayList<>();
        // split image into layers
        for (int y = img.getHeight()-1; y >= 0; y--) {
            imgLayers.add(img.getSubimage(0, y, img.getWidth()-1, 1));
        }
        reverseList(imgLayers);

        int originX;
        int originY;
        int originZ;
        try {
            originX = parseInt(args[0]);
            originY = parseInt(args[1]);
            originZ = parseInt(args[2]);
        } catch (Exception ignored) {
            sendMessage(sender, "Invalid coords", RED);
            return true;
        }

        // buildImage(sender, img, originX, originY, originZ);

        imgLayers.forEach((layer) -> {
            buildLayer(sender, layer, originX, originY + imgLayers.indexOf(layer), originZ);
        });
        sender.sendMessage("DONE!");

        return true;
    }


    private void buildLayer(CommandSender sender, BufferedImage img, int originX, int originY, int originZ) {
        int imgX = img.getWidth() - 1;
        for (int x = originX; x < originX + img.getWidth(); x++) {
            Color pixelColor = new Color(img.getRGB(imgX, 0));

            Material blockToPlace = getBlockClosestInColor(pixelColor);

            server.getWorlds().get(0).getBlockAt(x, originY, originZ).setType(blockToPlace);

            imgX--;
        }
    }


    private BufferedImage getImage() {
        File pathToFile = new File("C:\\\\Users\\wn10091617\\Downloads\\images.jpg");
        BufferedImage img = null;
        try {
            img = ImageIO.read(pathToFile);
        } catch (IOException e) {
            System.out.println("failed to get image");
        }

        return img;
    }


    private Material getBlockClosestInColor(Color pixelColor) {
        Map<String, Object> blockNames = config.getConfigurationSection("blocks").getValues(false);

        float lowestAverageDifference = 999;
        Material closestBlockInColor = null;

        // TODO: loop through config instead of every material to make things faster

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


    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(Component.text(message));
    }

    private void sendMessage(CommandSender sender, String message, TextColor color) {
        sender.sendMessage(Component.text(message).color(color));
    }

    private static <T> void reverseList(List<T> list)
    {
        if (list.size() <= 1)
            return;

        T value = list.remove(0);

        reverseList(list);

        list.add(value);
    }
}
