package com.jeremiah.triviagame.ui;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

public final class ImageUtils {

    private ImageUtils() {
    }

    public static ImageIcon loadScaledIcon(
            String resourcePath,
            int width,
            int height
    ) {
        URL imageUrl = ImageUtils.class.getResource(resourcePath);

        if (imageUrl == null) {
            System.err.println(
                    "Image resource was not found: " + resourcePath
            );
            return null;
        }

        ImageIcon originalIcon = new ImageIcon(imageUrl);

        Image scaledImage = originalIcon
                .getImage()
                .getScaledInstance(
                        width,
                        height,
                        Image.SCALE_SMOOTH
                );

        return new ImageIcon(scaledImage);
    }
}
