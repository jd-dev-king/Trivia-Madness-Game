package com.jeremiah.triviagame.ui;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;

public final class ImageUtils {
    private ImageUtils() {}

    public static ImageIcon loadIcon(String path) {
        URL resource = ImageUtils.class.getResource(normalize(path));
        return resource == null ? null : new ImageIcon(resource);
    }

    public static ImageIcon loadImageIcon(String path) {
        return loadIcon(path);
    }

    public static ImageIcon loadScaledIcon(String path, int width, int height) {
        ImageIcon icon = loadIcon(path);
        if (icon == null) {
            return null;
        }
        return scaleIcon(icon, width, height);
    }

    public static ImageIcon loadScaledImage(String path, int width, int height) {
        return loadScaledIcon(path, width, height);
    }

    public static ImageIcon getScaledIcon(String path, int width, int height) {
        return loadScaledIcon(path, width, height);
    }

    public static ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        if (icon == null) {
            return null;
        }

        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static String normalize(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
