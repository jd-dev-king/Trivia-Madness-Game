package com.jeremiah.triviagame.ui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class RoundedButton extends JButton {
    private int radius = Theme.BUTTON_RADIUS;
    private Color normalColor = Theme.PRIMARY;
    private Color hoverColor = Theme.PRIMARY_HOVER;
    private boolean hovering;

    public RoundedButton() {
        this("");
    }

    public RoundedButton(String text) {
        super(text);
        initialize();
    }

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        initialize();
    }

    public RoundedButton(String text, Color color) {
        super(text);
        normalColor = color;
        hoverColor = color.brighter();
        initialize();
    }

    public RoundedButton(String text, Color background, Color foreground) {
        this(text, background);
        setForeground(foreground);
    }

    private void initialize() {
        setFont(Theme.BUTTON_FONT);
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent event) {
                hovering = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent event) {
                hovering = false;
                repaint();
            }
        });
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public void setCornerRadius(int radius) {
        setRadius(radius);
    }

    public void setNormalColor(Color color) {
        normalColor = color;
        repaint();
    }

    public void setHoverColor(Color color) {
        hoverColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color color = hovering && isEnabled() ? hoverColor : normalColor;
        if (!isEnabled()) {
            color = color.darker();
        }

        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(graphics);
    }

    @Override
    protected void paintBorder(Graphics graphics) {
    }
}
