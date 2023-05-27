package dev.caveatemptor.imagetoblock;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

import static dev.caveatemptor.imagetoblock.Message.sendMessageAndReturn;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

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

        try {
            url = new URL((String) Objects.requireNonNull(config.get("url")));

            // try url, set back to null if unusable
            try {
                img = ImageIO.read(url);
            } catch (IOException e) {
                url = null;
            }
        } catch (Exception ignored) {}

        System.out.println(url);

        Objects.requireNonNull(this.getCommand("image")).setExecutor(new ImageCommand());
        Objects.requireNonNull(this.getCommand("url")).setExecutor(new URLCommand());

        this.saveDefaultConfig();
    }
}