package dev.caveatemptor.imagetoblock;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadImageFromURL {
    public static BufferedImage getImage() {
        URL url = null;
        try {
            url = new URL("https://img.freepik.com/premium-vector/color-spectrum-palette-hue-brightness-black-background-vector-illustration_522680-171.jpg?w=2000");
        } catch (MalformedURLException ignored) {
            System.out.println("bad url");
        }

        BufferedImage img = null;
        try {
            img = ImageIO.read(url);
        } catch (IOException e) {
            System.out.println("some other error");
        }

        /* img = new BufferedImage(
                img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_INT_ARGB,
        ); */

        return img;
    }
}
