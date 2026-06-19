package com.adonis.Nukepad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

class CustomButton extends JButton {

    public enum Style { DEFAULT, PRIMARY, SUCCESS, DANGER, GHOST }

    private Style style;
    private boolean hovered = false;
    private boolean pressed = false;

    public CustomButton(String text) {
        this(text, Style.DEFAULT);
    }

    public CustomButton(String text, Style style) {
        super(text);
        this.style = style;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(new Font("SansSerif", Font.PLAIN, 13));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
            public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight(), arc = 8;
        float yShift = pressed ? 1f : 0f;
        g2.translate(0, yShift);

        boolean isDark = false;
        Color panelBg = UIManager.getColor("Panel.background");
        if (panelBg != null) {
            isDark = panelBg.getRed() < 100;
        }

        Color top, bot, border;
        switch (style) {
            case PRIMARY -> {
                top    = isDark ? new Color( 70, 130, 230) : new Color( 91, 156, 246);
                bot    = isDark ? new Color( 40,  90, 190) : new Color( 45, 110, 223);
                border = isDark ? new Color( 30,  70, 160) : new Color( 36,  96, 204);
            }
            case SUCCESS -> {
                top    = isDark ? new Color( 45, 180,  90) : new Color( 62, 207, 110);
                bot    = isDark ? new Color( 25, 130,  60) : new Color( 36, 163,  78);
                border = isDark ? new Color( 18, 100,  45) : new Color( 26, 146,  68);
            }
            case DANGER -> {
                top    = isDark ? new Color(210,  70,  70) : new Color(247, 119, 119);
                bot    = isDark ? new Color(170,  35,  35) : new Color(224,  48,  48);
                border = isDark ? new Color(140,  25,  25) : new Color(196,  42,  42);
            }
            case GHOST -> {
                top    = new Color(0, 0, 0, 0);
                bot    = new Color(0, 0, 0, 0);
                border = isDark ? new Color(100, 100, 100) : new Color(160, 155, 148);
            }
            default -> {
                Color base = (panelBg != null) ? panelBg : new Color(220, 220, 220);
                top    = hovered ? base.brighter() : base;
                bot    = hovered ? base            : base.darker();
                border = isDark  ? new Color(80, 80, 80) : new Color(187, 184, 178);
            }
        }

        if (style != Style.GHOST) {
            GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bot);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h - 1, arc, arc));
        }

        if (style != Style.GHOST) {
            g2.setColor(new Color(255, 255, 255, pressed ? 30 : 60));
            g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, (h / 2f) - 1, arc - 1, arc - 1));
        }

        g2.setColor(border);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1.5f, arc, arc));

        if (style != Style.GHOST && !pressed) {
            g2.setColor(new Color(0, 0, 0, 40));
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(1, h - 1, w - 2, h - 1);
        }

        g2.translate(0, -yShift);
        FontMetrics fm = g2.getFontMetrics(getFont());
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();

        boolean isLight = style == Style.PRIMARY
                       || style == Style.SUCCESS
                       || style == Style.DANGER;

        if (isLight) {
            g2.setColor(new Color(0, 0, 0, 60));
            g2.setFont(getFont());
            g2.drawString(getText(), tx, ty + 1);
        }

        Color textCol = isLight
                ? Color.WHITE
                : (isDark ? new Color(220, 220, 220) : new Color(44, 44, 42));
        g2.setColor(textCol);
        g2.setFont(getFont());
        g2.drawString(getText(), tx, ty);
        g2.dispose();
    }
}