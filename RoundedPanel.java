package com.jeremiah.triviagame.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

public class RoundedPanel extends JPanel {
    private int radius;
    private Color panelColor;

    public RoundedPanel() {
        this(Theme.CORNER_RADIUS, Theme.CARD_BACKGROUND);
    }

    public RoundedPanel(int radius) {
        this(radius, Theme.CARD_BACKGROUND);
    }

    public RoundedPanel(Color color) {
        this(Theme.CORNER_RADIUS, color);
    }

    public RoundedPanel(int radius, Color color) {
        this.radius = radius;
        this.panelColor = color;
        setOpaque(false);
    }

    public RoundedPanel(LayoutManager layout) {
        this();
        setLayout(layout);
    }

    public RoundedPanel(LayoutManager layout, int radius) {
        this(radius);
        setLayout(layout);
    }

    public RoundedPanel(LayoutManager layout, int radius, Color color) {
        this(radius, color);
        setLayout(layout);
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public void setCornerRadius(int radius) {
        setRadius(radius);
    }

    public void setPanelColor(Color color) {
        this.panelColor = color;
        repaint();
    }

    @Override
    public void setBackground(Color color) {
        super.setBackground(color);
        panelColor = color;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(panelColor != null ? panelColor : getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(graphics);
    }
}
