package dev.caveatemptor.imagetoblock;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import static dev.caveatemptor.imagetoblock.Message.*;
import static dev.caveatemptor.imagetoblock.ImageToBlock.*;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.bukkit.Material.*;
import static java.util.Objects.requireNonNull;

public class ImageCommand implements CommandExecutor {

    private static int widthLimit;
    private static int heightLimit;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        // TODO: Change to use enum instead of config for block colors

        if (args.length > 4)
            return sendMessageAndReturn(sender, "Too many arguments!");

        // get the image again every time to reset any scaling done to it
        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            url = null;
            return sendMessageAndReturn(sender, "Cannot get image, invalid URL", RED);
        }

        Player player = null;
        try {
            player = (Player) sender;
        } catch (Exception ignored) {}

        int originX;
        int originY;
        int originZ;

        if (args.length <= 1) {
            if (player == null)
                return sendMessageAndReturn(sender, "Must be in-game to do this. Please re-run and specify coords", RED);

            originX = player.getLocation().getBlockX();
            originY = player.getLocation().getBlockY();
            originZ = player.getLocation().getBlockZ();
        }
        else if (args.length == 3 || args.length == 4) {
            try {
                originX = parseInt(args[0]);
                originY = parseInt(args[1]);
                originZ = parseInt(args[2]);
            } catch (Exception ignored) {
                return sendMessageAndReturn(sender, "Invalid coords", RED);
            }
        }
        else
            return sendMessageAndReturn(sender, "Invalid arguments", RED);

        sendMessage(sender, "Scaling to fit within limits...");
        if (img.getWidth() > widthLimit) {
            float scale = ((float) widthLimit) / img.getWidth();
            int newHeight = (int) (img.getHeight() * scale);

            scaleImage(widthLimit, newHeight);
        }

        if (img.getHeight() > heightLimit) {
            float scale = ((float) heightLimit) / img.getHeight();
            int newWidth = (int) (img.getWidth() * scale);

            scaleImage(newWidth, heightLimit);
        }
        sendMessage(sender, "Done!");

        sendMessage(sender, "Scaling to fit with world height limit...");
        int maxHeight = server.getWorlds().get(0).getMaxHeight();
        if (img.getHeight() + originY > maxHeight) {
            int blocksOver = img.getHeight() + originY - maxHeight;
            int newHeight = img.getHeight() - blocksOver;

            float scale = ((float) newHeight) / img.getHeight();

            int newWidth = (int) (img.getWidth() * scale);

            scaleImage(newWidth, newHeight);
        }
        sendMessage(sender, "Done!");

        // make a barrier block layer on the bottom to stop physics blocks from falling
        sendMessage(sender, "Placing barrier block bottom...");
        for (int x = originX; x < originX + img.getWidth() - 1; x++) {
            server.getWorlds().get(0).getBlockAt(x, originY - 1, originZ).setType(BARRIER);
        }
        sendMessage(sender, "Done!");

        sendMessage(sender, "Building image...");
        buildImage(originX, originY, originZ);
        sendMessage(sender, "Done!");

        return true;
    }


    private void buildImage(int originX, int originY, int originZ) {
        int imgX = img.getWidth() - 1;
        int imgY = img.getHeight() - 1;

        for (int y = originY; y < originY + img.getHeight(); y++) {
            for (int x = originX; x < originX + img.getWidth(); x++) {
                Color pixelColor = new Color(img.getRGB(imgX, imgY));

                Material blockToPlace = getBlockClosestInColor(pixelColor);

                server.getWorlds().get(0).getBlockAt(x, y, originZ).setType(blockToPlace);

                imgX--;
            }
            imgX = img.getWidth() - 1;
            imgY--;
        }
    }


    private void scaleImage(int newWidth, int newHeight) {
        BufferedImage scaledImg = new BufferedImage(newWidth, newHeight, BufferedImage.TRANSLUCENT);

        Graphics2D g2 = scaledImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, newWidth, newHeight, null);
        g2.dispose();

        img = scaledImg;
    }


    private Material getBlockClosestInColor(Color pixelColor) {

        Map<String, Object> blockNames = requireNonNull(config.getConfigurationSection("blocks")).getValues(false);

        float lowestAverageDifference = 999; // TODO: find a better way to do this
        Material closestBlockInColor = null;

        for (Material material : Material.values()) {

            String materialName = material.key().toString().replace("minecraft:", "").toUpperCase();
            if (!blockNames.containsKey(materialName))
                continue;

            Map<String, Object> blockColors;
            try {
                blockColors = requireNonNull(config.getConfigurationSection("blocks." + materialName)).getValues(false);
            } catch (IllegalArgumentException e) {
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

        if (closestBlockInColor == null) // This really shouldn't be an issue, but it is. Short term solution
            closestBlockInColor = AIR; // TODO: Fix this

        return closestBlockInColor;
    }


    public static boolean setWidthLimit(int widthLimit) {
        if (widthLimit < 32)
            return false;

        ImageCommand.widthLimit = widthLimit;
        instance.saveConfig();
        return true;
    }

    public static boolean setHeightLimit(int heightLimit) {
        if (widthLimit < 32)
            return false;

        ImageCommand.heightLimit = heightLimit;
        instance.saveConfig();
        return true;
    }

    public static int getWidthLimit() {
        return widthLimit;
    }

    public static int getHeightLimit() {
        return heightLimit;
    }
}