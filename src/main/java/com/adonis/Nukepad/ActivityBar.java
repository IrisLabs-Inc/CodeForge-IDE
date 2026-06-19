package com.adonis.Nukepad;

import com.adonis.Nukepad.ActivityBar.IconBtn.IconPainter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import com.formdev.flatlaf.extras.FlatSVGIcon;

/**
 * VS Code-style vertical activity bar.
 * Sits on the far left of the window, replacing the top JMenuBar.
 * Each icon is a custom-painted JComponent.
 */
public class ActivityBar extends JPanel {

    // ── colours ──────────────────────────────────────────────────────────
    private static final Color ACTIVE_INDICATOR = new Color(255, 255, 255, 220);
    private static final Color HOVER_BG          = new Color(255, 255, 255, 25);
    private static final Color BAR_BG_DARK       = new Color(40,  40,  44);
    private static final Color BAR_BG_LIGHT      = new Color(50,  50,  120);
    private static final int   BAR_W             = 48;
    private static final int   ICON_SIZE         = 24;

    
    public static class IconBtn extends JComponent {
        private final String id;
        private final IconPainter painter;
        private boolean active  = false;
        private boolean hovered = false;
        private String  tooltip;

        interface IconPainter { void paint(Graphics2D g2, int x, int y, int size, boolean active); }

        IconBtn(String id, IconPainter painter, String tooltip) {
            this.id = id; this.painter = painter; this.tooltip = tooltip;
            setPreferredSize(new Dimension(BAR_W, BAR_W));
            setMaximumSize  (new Dimension(BAR_W, BAR_W));
            setMinimumSize  (new Dimension(BAR_W, BAR_W));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
            setToolTipText(tooltip);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered=true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered=false; repaint(); }
            });
        }

        public void setActive(boolean a) { active=a; repaint(); }
        public boolean isActive() { return active; }
        public String getId() { return id; }

        void addClickListener(ActionListener l) {
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    l.actionPerformed(new ActionEvent(IconBtn.this,
                        ActionEvent.ACTION_PERFORMED, id));
                }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(), h=getHeight();
            // hover background
            if (hovered && !active) {
                g2.setColor(HOVER_BG);
                g2.fillRoundRect(4, 4, w-8, h-8, 6, 6);
            }
            // active left indicator bar
            if (active) {
                g2.setColor(ACTIVE_INDICATOR);
                g2.fillRoundRect(0, h/2-10, 3, 20, 3, 3);
            }
            // icon - ALWAYS PAINT, not just on hover
            int ix = (w - ICON_SIZE) / 2;
            int iy = (h - ICON_SIZE) / 2;
            painter.paint(g2, ix, iy, ICON_SIZE, active || hovered);
            g2.dispose();
        }
    }

    // ── logo button (top) ─────────────────────────────────────────────────
    static class LogoBtn extends JComponent {
        private FlatSVGIcon svgIcon;
        private boolean hovered = false;
        LogoBtn() {
            setPreferredSize(new Dimension(BAR_W, BAR_W));
            setMaximumSize  (new Dimension(BAR_W, BAR_W));
            setMinimumSize  (new Dimension(BAR_W, BAR_W));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setOpaque(false);
            setToolTipText("Nukepad — File Menu");
            try {
                svgIcon = new FlatSVGIcon("icons/nukepadlogo.svg", 30, 30);
            } catch (Exception e) { 
                System.err.println("Failed to load logo SVG: " + e.getMessage());
            }
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e){hovered=true; repaint();}
                @Override public void mouseExited (MouseEvent e){hovered=false;repaint();}
            });
        }
        void addClickListener(ActionListener l) {
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    l.actionPerformed(new ActionEvent(LogoBtn.this,
                        ActionEvent.ACTION_PERFORMED, "logo"));
                }
            });
        }
        @Override protected void paintComponent(Graphics g) {
               super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth(), h = getHeight();
        
        if (hovered) {
            g2.setColor(HOVER_BG);
            g2.fillRoundRect(4, 4, w-8, h-8, 6, 6);
        }   
        
        if (svgIcon != null) {
            int ix = (w - ICON_SIZE) / 2;
            int iy = (h - ICON_SIZE) / 2;
            svgIcon.paintIcon(this, g2, ix, iy);
        }
        
        g2.dispose();
    }
        
    }

    // ── divider ───────────────────────────────────────────────────────────
    static class Divider extends JComponent {
        Divider() {
            setPreferredSize(new Dimension(BAR_W, 8));
            setMaximumSize  (new Dimension(BAR_W, 8));
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            g.setColor(new Color(255,255,255,30));
            g.drawLine(8, 4, BAR_W-8, 4);
        }
    }

    // ── icon painters ─────────────────────────────────────────────────────
    private static void stroke(Graphics2D g2, boolean on) {
        g2.setStroke(new BasicStroke(on ? 1.8f : 1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }
    private static Color ic(boolean on) {
        return on ? new Color(255,255,255,230) : new Color(200,200,200,160);
    }

    // Helper method to load icons from resources
    private static BufferedImage loadIcon(String path) {
        try {
            java.net.URL url = ActivityBar.class.getResource(path);
            if (url != null) {
                return ImageIO.read(url);
            }
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + path);
        }
        return null;
    }

    // File/Edit/View/Git/Compile/Run/Terminal/Sidebar/Author/Settings/Plugins painters
    static final IconPainter PAINT_FILE = (g,x,y,s,on) -> {
        g.setColor(ic(on)); stroke(g,on);
        // dog-eared page
        int f=s/4;
        int[] px={x,x,x+s-f,x+s,x+s}; int[] py={y,y+s,y+s,y+f,y};
        g.drawPolygon(px,py,5);
        g.drawLine(x+s-f,y,x+s-f,y+f); g.drawLine(x+s-f,y+f,x+s,y+f);
        // lines
        g.drawLine(x+3,y+s/2,   x+s-f-1,y+s/2);
        g.drawLine(x+3,y+s/2+4, x+s-f-1,y+s/2+4);
    };
    
    static final IconPainter PAINT_EDIT = (g, x, y, s, on) -> {
        BufferedImage img = loadIcon("/icons/ab_edit.png");
        if (img != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, on ? 0.95f : 0.60f));
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(img, x, y, s, s, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        } else {
            // fallback
            g.setColor(ic(on)); stroke(g, on);
            int tip = s - 3;
            g.drawLine(x+3, y+tip, x+tip, y+3);
            int[] hx={x,x+4,x+tip+1,x+tip-3}; int[] hy={y+s,y+tip-2,y+4,y};
            g.drawPolyline(hx, hy, 4);
            g.drawLine(x, y+s, x+3, y+s-2);
            g.drawLine(x+s-6, y+2, x+s-2, y+6);
        }
    };
    

    
    static final IconPainter PAINT_GIT = (g, x, y, s, on) -> {
        BufferedImage img = loadIcon("/icons/ab_git.png");
        if (img != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, on ? 0.95f : 0.60f));
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(img, x, y, s, s, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        } else {
            // fallback
            g.setColor(ic(on)); stroke(g, on);
            int mx = x + s/2;
            g.drawLine(mx, y+s-3, mx, y+8);
            g.drawLine(mx, y+8, x+s-4, y+3);
            g.fillOval(mx-3, y+s-6, 6, 6);
            g.fillOval(x+s-7, y, 6, 6);
            g.fillOval(mx-3, y+5, 6, 6);
        }
    };
    
    static final IconPainter PAINT_COMPILE = (g, x, y, s, on) -> {
        BufferedImage img = loadIcon("/icons/ab_compile.png");
        if (img != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, on ? 0.95f : 0.60f));
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(img, x, y, s, s, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        } else {
            // fallback
            g.setColor(ic(on)); stroke(g, on);
            int hs = s/3;
            g.fillRoundRect(x+s/2-2, y+hs, 4, s-hs-2, 2, 2);
            g.fillRoundRect(x+s/2-hs, y+1, hs*2, hs+2, 3, 3);
        }
    };
    
    static final IconPainter PAINT_RUN = (g, x, y, s, on) -> {
        BufferedImage img = loadIcon("/icons/ab_run.png");
        if (img != null) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, on ? 0.95f : 0.60f));
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(img, x, y, s, s, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        } else {
            // fallback
            g.setColor(new Color(100, 220, 100, on ? 230 : 160));
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(x+1, y+1, s-2, s-2);
            int[] px2={x+s/3, x+s-s/4, x+s/3};
            int[] py2={y+s/4, y+s/2,   y+s-s/4};
            g.fillPolygon(px2, py2, 3);
        }
    };
    
    static final IconPainter PAINT_TERM = (g,x,y,s,on) -> {
        g.setColor(ic(on)); stroke(g,on);
        // terminal box with prompt
        g.drawRoundRect(x+1,y+1,s-2,s-2,4,4);
        g.drawLine(x+4,y+s/2-2,x+4+s/4,y+s/2+2); // >
        g.drawLine(x+4+s/4,y+s/2+2,x+4,y+s/2+6);
        g.drawLine(x+4+s/4+2,y+s/2+4,x+s-4,y+s/2+4); // underscore
    };
    
    static final IconPainter PAINT_SIDEBAR = (g,x,y,s,on) -> {
        g.setColor(ic(on)); stroke(g,on);
        // layout: left panel + main
        g.drawRoundRect(x+1,y+1,s-2,s-2,3,3);
        g.drawLine(x+s/3,y+1,x+s/3,y+s-1);
    };
    
    static final IconPainter PAINT_AUTHOR = (g,x,y,s,on) -> {
        g.setColor(ic(on)); stroke(g,on);
        // person
        int cx=x+s/2;
        g.drawOval(cx-s/5,y+1,s*2/5,s*2/5);
        g.drawArc(cx-s/3,y+s/2,s*2/3,s/2,0,180);
    };
    
    static final IconPainter PAINT_SETTINGS = (g,x,y,s,on) -> {
        g.setColor(ic(on));
        // gear: circle with teeth
        int cx=x+s/2, cy=y+s/2, r=s/2-3;
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(cx-r/2,cy-r/2,r,r);
        for(int i=0;i<8;i++){
            double a=Math.toRadians(i*45);
            int x1=(int)(cx+(r-1)*Math.cos(a)), y1=(int)(cy+(r-1)*Math.sin(a));
            int x2=(int)(cx+(r+3)*Math.cos(a)), y2=(int)(cy+(r+3)*Math.sin(a));
            g.drawLine(x1,y1,x2,y2);
        }
    };
    
    static final IconPainter PAINT_PLUGINS = (g,x,y,s,on) -> {
    BufferedImage img = loadIcon("/icons/ab_plugins.png");
    if (img != null) {
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, on ? 0.95f : 0.60f));
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(img, x, y, s, s, null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    } else {
        // fallback: puzzle piece
        g.setColor(ic(on)); stroke(g, on);
        g.drawRoundRect(x+2, y+2, s-4, s-4, 3, 3);
        g.fillRoundRect(x+s/2-2, y-2, 5, 5, 2, 2);
        g.fillRoundRect(x+s+2, y+s/2-2, 5, 5, 2, 2);
    }
    };

    // ── fields ────────────────────────────────────────────────────────────
    private final List<IconBtn> topBtns    = new ArrayList<>();
    private final List<IconBtn> bottomBtns = new ArrayList<>();
    private final JPanel topPanel;
    private final JPanel bottomPanel;

    // ── constructor ───────────────────────────────────────────────────────
    public ActivityBar() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(BAR_W, 0));
        setMinimumSize  (new Dimension(BAR_W, 0));
        setBorder(BorderFactory.createMatteBorder(0,0,0,1,new Color(0,0,0,60)));
        updateBg();

        topPanel    = new JPanel(); topPanel.setLayout(new BoxLayout(topPanel,    BoxLayout.Y_AXIS)); topPanel.setOpaque(false);
        bottomPanel = new JPanel(); bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); bottomPanel.setOpaque(false);

        add(topPanel,    BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateBg() {
        boolean dark = ThemeManager.load().equals("dark");
        setBackground(dark ? BAR_BG_DARK : BAR_BG_LIGHT);
        setOpaque(true);
    }

    public void applyTheme() { updateBg(); repaint(); }

    // ── public builders ───────────────────────────────────────────────────
    public LogoBtn addLogo(ActionListener l) {
        LogoBtn btn = new LogoBtn();
        btn.addClickListener(l);
        topPanel.add(btn);
        topPanel.add(new Divider());
        return btn;
    }

    public IconBtn addTop(String id, IconPainter p, String tooltip, ActionListener l) {
        IconBtn btn = new IconBtn(id, p, tooltip);
        btn.addClickListener(l);
        topPanel.add(btn);
        topBtns.add(btn);
        return btn;
    }

    public IconBtn addBottom(String id, IconPainter p, String tooltip, ActionListener l) {
        IconBtn btn = new IconBtn(id, p, tooltip);
        btn.addClickListener(l);
        bottomBtns.add(btn);
        bottomPanel.add(btn);
        return btn;
    }

    public void addDividerTop()    { topPanel.add(new Divider()); }
    public void addDividerBottom() { bottomPanel.add(new Divider()); }

    /** Deactivate all top buttons, then activate the given one. */
    public void setActive(String id) {
        for (IconBtn b : topBtns) b.setActive(b.getId().equals(id));
    }

    public static int getBarWidth() { return BAR_W; }
}