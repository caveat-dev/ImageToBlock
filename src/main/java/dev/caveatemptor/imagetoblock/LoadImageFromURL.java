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
            url = new URL("https://www.gannett-cdn.com/presto/2023/03/31/PPAS/b2205264-eb93-4cf5-b96d-ff637e40b8c2-20230330_HarpoTheClownPortraits_001.jpg");
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
