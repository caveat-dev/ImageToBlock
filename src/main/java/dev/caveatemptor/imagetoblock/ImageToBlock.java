package dev.caveatemptor.imagetoblock;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static java.util.Objects.requireNonNull;

public final class ImageToBlock extends JavaPlugin {
    public static ImageToBlock instance;
    public static ImageToBlock plugin = null;
    public static FileConfiguration config = null;
    public static Server server = null;

    public static URL url = null;
    public static BufferedImage img = null;

    private static int widthLimit;
    private static int heightLimit;

    private static int colorTolerance;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        instance = this;
        plugin = instance;
        config = instance.getConfig();
        server = instance.getServer();

        widthLimit = (int) requireNonNull(config.get("widthLimit"));
        heightLimit = (int) requireNonNull(config.get("heightLimit"));

        colorTolerance = (int) requireNonNull(config.get("colorTolerance"));

        // get url from config
        try {
            url = new URL((String) requireNonNull(config.get("url")));

            // try url, set back to null if unusable
            try {
                img = ImageIO.read(url);
            } catch (IOException e) {
                url = null;
            }
        } catch (Exception ignored) {}

        // get size limits from config
        setWidthLimit((int) requireNonNull(config.get("widthLimit")));
        setHeightLimit((int) requireNonNull(config.get("heightLimit")));

        requireNonNull(this.getCommand("image")).setExecutor(new ImageCommand());
        requireNonNull(this.getCommand("url")).setExecutor(new URLCommand());
        requireNonNull(this.getCommand("sizelimit")).setExecutor(new SizeLimitCommand());
        requireNonNull(this.getCommand("colorTolerance")).setExecutor(new ColorToleranceCommand());

        this.saveDefaultConfig();
    }


    public static boolean setWidthLimit(int widthLimit) {
        if (widthLimit < 32)
            return false;

        ImageToBlock.widthLimit = widthLimit;
        config.set("widthLimit", widthLimit);
        instance.saveConfig();
        return true;
    }

    public static boolean setHeightLimit(int heightLimit) {
        if (widthLimit < 32)
            return false;

        ImageToBlock.heightLimit = heightLimit;
        config.set("heightLimit", heightLimit);
        instance.saveConfig();
        return true;
    }

    public static int getWidthLimit() {
        return widthLimit;
    }

    public static int getHeightLimit() {
        return heightLimit;
    }


    public static boolean setColorTolerance(int colorTolerance) {
        if (colorTolerance < 0)
            return false;

        ImageToBlock.colorTolerance = colorTolerance;
        config.set("colorTolerance", colorTolerance);
        instance.saveConfig();
        return true;
    }

    public static int getColorTolerance() { return colorTolerance; }
}