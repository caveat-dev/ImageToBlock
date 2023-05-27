package dev.caveatemptor.imagetoblock;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Objects;

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

        Objects.requireNonNull(this.getCommand("image")).setExecutor(new ImageCommand());
        Objects.requireNonNull(this.getCommand("url")).setExecutor(new URLCommand());

        this.saveDefaultConfig();
    }
}