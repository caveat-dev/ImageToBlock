package dev.caveatemptor.imagetoblock;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.bukkit.Material.*;

public class ImageCommand implements CommandExecutor {
    ImageToBlock plugin = ImageToBlock.getInstance();
    FileConfiguration config = plugin.getConfig();
    Server server = plugin.getServer();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length < 3)
            return true;

        BufferedImage img = getImage();
        List<BufferedImage> imgLayers = new ArrayList<>();
        // split image into layers
        for (int y = img.getHeight()-1; y >= 0; y--) {
            imgLayers.add(img.getSubimage(0, y, img.getWidth()-1, 1));
        }

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

        // make a barrier block layer on the bottom to stop physics blocks from falling
        sendMessage(sender, "Placing barrier block bottom...");
        for (int x = originX; x < originX + img.getWidth()-1; x++) {
            server.getWorlds().get(0).getBlockAt(x, originY-1, originZ).setType(BARRIER);
        }
        sendMessage(sender, "Done!");

        sendMessage(sender, "Placing layers...");
        imgLayers.forEach((layer) -> {
            buildLayer(layer, originX, originY + imgLayers.indexOf(layer), originZ);
        });
        sendMessage(sender, "Done!");

        return true;
    }


    private void buildLayer(BufferedImage img, int originX, int originY, int originZ) {
        int imgX = img.getWidth() - 1;
        for (int x = originX; x < originX + img.getWidth(); x++) {
            Color pixelColor = new Color(img.getRGB(imgX, 0));

            Material blockToPlace = getBlockClosestInColor(pixelColor);

            server.getWorlds().get(0).getBlockAt(x, originY, originZ).setType(blockToPlace);

            imgX--;
        }
    }


    private BufferedImage getImage() {
        URL url = null;
        try {
            url = new URL("IMAGE URL HERE");
        } catch (MalformedURLException ignored) {
            System.out.println("bad url");
        }

        BufferedImage img;
        try {
            assert url != null;
            img = ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return img;
    }


    private Material getBlockClosestInColor(Color pixelColor) {
        Map<String, Object> blockNames = Objects.requireNonNull(config.getConfigurationSection("blocks")).getValues(false);

        float lowestAverageDifference = 999;
        Material closestBlockInColor = null;

        for (Material material : Material.values()) {

            String materialName = material.key().toString().replace("minecraft:", "").toUpperCase();
            if (!blockNames.containsKey(materialName))
                continue;

            Map<String, Object> blockColors;
            try {
                blockColors = Objects.requireNonNull(config.getConfigurationSection("blocks." + materialName)).getValues(false);
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
}
