package dev.caveatemptor.imagetoblock;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoadImageFromURL {
    public static BufferedImage getImage() {
        URL url = null;
        try {
            url = new URL("https://img1.cgtrader.com/items/1891525/f39e130380/minecraft-grass-block-3d-model-low-poly-max-obj-mtl-fbx-dae.jpg");
        } catch (MalformedURLException ignored) {
            System.out.println("bad url");
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            System.out.println("some other error");
        }

        img = new BufferedImage(
                img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        return img;
    }
}
