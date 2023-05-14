package dev.caveatemptor.imagetoblock;

import org.bukkit.plugin.java.JavaPlugin;

public final class ImageToBlock extends JavaPlugin {
    private static ImageToBlock instance;
    public static ImageToBlock getInstance() { return instance; }

    @Override
    public void onEnable() {
        instance = this;

        this.getCommand("image").setExecutor(new Image());
    }
}