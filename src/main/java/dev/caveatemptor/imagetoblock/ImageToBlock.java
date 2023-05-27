package dev.caveatemptor.imagetoblock;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static dev.caveatemptor.imagetoblock.ImageCommand.*;
import static java.util.Objects.requireNonNull;

public final class ImageToBlock extends JavaPlugin {
    public static ImageToBlock instance;
    public static ImageToBlock plugin = null;
    public static FileConfiguration config = null;
    public static Server server = null;

    public static URL url = null;
    public static BufferedImage img = null;

    @Override
    public void onEnable() {
        instance = this;
        plugin = instance;
        config = instance.getConfig();
        server = instance.getServer();

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

        this.saveDefaultConfig();
    }
}