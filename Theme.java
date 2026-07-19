package com.jeremiah.triviagame.ui;

import java.awt.Color;
import java.awt.Font;

public final class Theme {
    private Theme() {}

    public static final Color BACKGROUND = new Color(15, 23, 42);
    public static final Color BACKGROUND_COLOR = BACKGROUND;
    public static final Color PANEL_BACKGROUND = new Color(30, 41, 59);
    public static final Color CARD_BACKGROUND = PANEL_BACKGROUND;
    public static final Color SURFACE = PANEL_BACKGROUND;
    public static final Color PRIMARY = new Color(124, 58, 237);
    public static final Color PRIMARY_COLOR = PRIMARY;
    public static final Color PRIMARY_HOVER = new Color(109, 40, 217);
    public static final Color SECONDARY = new Color(14, 165, 233);
    public static final Color SECONDARY_COLOR = SECONDARY;
    public static final Color ACCENT = new Color(245, 158, 11);
    public static final Color ACCENT_COLOR = ACCENT;
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color ERROR = new Color(239, 68, 68);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    public static final Color TEXT_SECONDARY = new Color(203, 213, 225);
    public static final Color MUTED_TEXT = new Color(148, 163, 184);
    public static final Color BORDER = new Color(71, 85, 105);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 34);
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 24);
    public static final Font SUBHEADING_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 15);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 15);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    public static final int CORNER_RADIUS = 20;
    public static final int BUTTON_RADIUS = 16;
}
