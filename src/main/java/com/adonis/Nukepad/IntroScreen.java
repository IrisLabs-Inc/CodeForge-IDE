package com.adonis.Nukepad;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class IntroScreen {

    private JFrame introFrame;
    private static final File RECENTS_FILE = new File(
            System.getProperty("user.home") + "/.nukepad_recents.txt");
    private static final File THEME_FILE   = new File(
            System.getProperty("user.home") + "/.nukepad_theme.txt");
    private static final int MAX_RECENTS = 8;

    static class BackgroundPanel extends JPanel {
        private final BufferedImage bg;
        BackgroundPanel(BufferedImage bg) { this.bg=bg; setLayout(new BoxLayout(this,BoxLayout.Y_AXIS)); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg!=null) g.drawImage(bg,0,0,getWidth(),getHeight(),this);
            Graphics2D g2=(Graphics2D)g.create();
            g2.setColor(new Color(0,0,0,100)); g2.fillRect(0,0,getWidth(),getHeight());
            g2.dispose();
        }
    }

    static class AnimatedBanner extends JComponent {
        private final BufferedImage img;
        private float alpha=0f, scale=0.55f;
        private final int W=360, H;
        private javax.swing.Timer t;
        AnimatedBanner(BufferedImage img) {
            this.img=img;
            double a=(img==null)?0.25:(double)img.getHeight()/img.getWidth();
            H=Math.max((int)(W*a),10);
            setPreferredSize(new Dimension(W,H)); setMaximumSize(new Dimension(W,H));
            setAlignmentX(CENTER_ALIGNMENT); setOpaque(false);
        }
        void startAnimation() {
            t=new javax.swing.Timer(14,e->{
                alpha=Math.min(1f,alpha+0.03f); scale=Math.min(1f,scale+0.028f); repaint();
                if(alpha>=1f&&scale>=1f)((javax.swing.Timer)e.getSource()).stop();
            }); t.start();
        }
        @Override protected void paintComponent(Graphics g) {
            if(img==null) return;
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
            int w=(int)(W*scale),h=(int)(H*scale);
            g2.drawImage(img,(getWidth()-w)/2,(getHeight()-h)/2,w,h,this);
            g2.dispose();
        }
    }

    static class SlideButton extends JComponent {
        private final String label; private final boolean isFolder;
        private float slideX=0f; private boolean hovered=false, pressed=false;
        private javax.swing.Timer slideTimer;
        private static final int BH=42,BW=230,SPX=32;
        SlideButton(String label, boolean isFolder) {
            this.label=label; this.isFolder=isFolder;
            setPreferredSize(new Dimension(BW,BH)); setMaximumSize(new Dimension(BW,BH));
            setMinimumSize(new Dimension(BW,BH));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e){hovered=true; animateTo(1f);}
                @Override public void mouseExited(MouseEvent e) {hovered=false;animateTo(0f);}
                @Override public void mousePressed(MouseEvent e) {pressed=true; repaint();}
                @Override public void mouseReleased(MouseEvent e){pressed=false;repaint();}
            });
        }
        private void animateTo(float tgt) {
            if(slideTimer!=null)slideTimer.stop();
            slideTimer=new javax.swing.Timer(12,e->{
                float d=tgt-slideX;
                if(Math.abs(d)<0.025f){slideX=tgt;((javax.swing.Timer)e.getSource()).stop();}
                else slideX+=d*0.20f;
                repaint();
            }); slideTimer.start();
        }
        void addClickListener(ActionListener l) {
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    l.actionPerformed(new ActionEvent(SlideButton.this,
                        ActionEvent.ACTION_PERFORMED,"click"));
                }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight();
            if(pressed)g2.translate(0,1);
            Color bt=hovered?new Color(255,255,255,60):new Color(255,255,255,30);
            Color bb=hovered?new Color(255,255,255,32):new Color(255,255,255,14);
            g2.setPaint(new GradientPaint(0,0,bt,0,h,bb));
            g2.fill(new RoundRectangle2D.Float(0,0,w,h-1,10,10));
            g2.setColor(new Color(255,255,255,hovered?130:65));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0.5f,0.5f,w-1,h-1.5f,10,10));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,slideX));
            drawIcon(g2,8,(h-20)/2,20);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
            int tx=14+(int)(SPX*slideX);
            g2.setFont(new Font("SansSerif",Font.PLAIN,14));
            FontMetrics fm=g2.getFontMetrics();
            int ty=(h-fm.getHeight())/2+fm.getAscent();
            g2.setColor(new Color(0,0,0,90)); g2.drawString(label,tx+1,ty+1);
            g2.setColor(Color.WHITE); g2.drawString(label,tx,ty);
            g2.dispose();
        }
        private void drawIcon(Graphics2D g2,int x,int y,int size) {
            if(isFolder) {
                g2.setColor(new Color(255,200,55)); g2.fillRoundRect(x,y+4,size,size-4,3,3);
                g2.setColor(new Color(255,225,100)); g2.fillRoundRect(x,y,size/2,5,2,2);
                g2.setColor(new Color(180,130,10,140)); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x,y+4,size-1,size-5,3,3);
            } else {
                int[] px={x,x,x+size-4,x+size,x+size};
                int[] py={y,y+size,y+size,y+4,y};
                g2.setColor(new Color(190,215,255)); g2.fillPolygon(px,py,5);
                g2.setColor(new Color(130,165,220));
                g2.fillPolygon(new int[]{x+size-4,x+size,x+size-4},new int[]{y,y+4,y+4},3);
                g2.setColor(new Color(90,125,175,180)); g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(x+2,y+8,x+size-6,y+8);
                g2.drawLine(x+2,y+11,x+size-6,y+11);
                g2.drawLine(x+2,y+14,x+size-8,y+14);
            }
        }
    }

    static class ThemeButton extends JComponent {
        private boolean dark;
        private boolean hovered=false, pressed=false;
        private static final int BW=160, BH=42;
        ThemeButton(boolean dark) {
            this.dark=dark;
            setPreferredSize(new Dimension(BW,BH)); setMaximumSize(new Dimension(BW,BH));
            setMinimumSize(new Dimension(BW,BH));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e){hovered=true; repaint();}
                @Override public void mouseExited(MouseEvent e) {hovered=false;repaint();}
                @Override public void mousePressed(MouseEvent e) {pressed=true; repaint();}
                @Override public void mouseReleased(MouseEvent e){pressed=false;repaint();}
            });
        }
        void setDark(boolean d){this.dark=d;repaint();}
        void addClickListener(ActionListener l) {
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    l.actionPerformed(new ActionEvent(ThemeButton.this,
                        ActionEvent.ACTION_PERFORMED,"click"));
                }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight();
            if(pressed)g2.translate(0,1);
            // background pill
            Color bt=hovered?new Color(255,255,255,60):new Color(255,255,255,30);
            Color bb=hovered?new Color(255,255,255,32):new Color(255,255,255,14);
            g2.setPaint(new GradientPaint(0,0,bt,0,h,bb));
            g2.fill(new RoundRectangle2D.Float(0,0,w,h-1,10,10));
            g2.setColor(new Color(255,255,255,hovered?130:65));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0.5f,0.5f,w-1,h-1.5f,10,10));
            // scene drawing area
            int scx=8, scy=(h-22)/2, scw=36, sch=22;
            g2.setClip(new RoundRectangle2D.Float(scx,scy,scw,sch,4,4));
            if(dark) {
                // night sky gradient
                g2.setPaint(new GradientPaint(scx,scy,new Color(10,15,40),scx,scy+sch,new Color(20,30,70)));
                g2.fillRect(scx,scy,scw,sch);
                // moon crescent
                g2.setColor(new Color(255,245,180));
                g2.fillOval(scx+8,scy+3,13,13);
                g2.setColor(new Color(15,22,55));
                g2.fillOval(scx+12,scy+2,11,11);
                // stars
                g2.setColor(new Color(255,255,220,220));
                int[][] stars={{scx+28,scy+3},{scx+32,scy+7},{scx+25,scy+9},{scx+30,scy+13},{scx+22,scy+4}};
                for(int[] s:stars)g2.fillOval(s[0],s[1],2,2);
            } else {
                // day sky gradient
                g2.setPaint(new GradientPaint(scx,scy,new Color(135,206,250),scx,scy+sch,new Color(200,230,255)));
                g2.fillRect(scx,scy,scw,sch);
                // sun
                g2.setColor(new Color(255,220,50));
                g2.fillOval(scx+4,scy+3,14,14);
                g2.setColor(new Color(255,200,0,180));
                g2.setStroke(new BasicStroke(1.5f));
                for(int i=0;i<8;i++){
                    double a=Math.toRadians(i*45);
                    int sx=(int)(scx+11+9*Math.cos(a)),sy=(int)(scy+10+9*Math.sin(a));
                    int ex2=(int)(scx+11+12*Math.cos(a)),ey=(int)(scy+10+12*Math.sin(a));
                    g2.drawLine(sx,sy,ex2,ey);
                }
                // clouds
                g2.setColor(new Color(255,255,255,230));
                g2.fillOval(scx+20,scy+8,12,8);
                g2.fillOval(scx+24,scy+5,10,8);
                g2.fillOval(scx+28,scy+8,10,7);
                g2.fillOval(scx+23,scy+12,14,5);
            }
            g2.setClip(null);
            // label
            String lbl=dark?"Light Mode":"Dark Mode";
            g2.setFont(new Font("SansSerif",Font.PLAIN,13));
            FontMetrics fm=g2.getFontMetrics();
            int tx=scx+scw+8;
            int ty=(h-fm.getHeight())/2+fm.getAscent();
            g2.setColor(new Color(0,0,0,80)); g2.drawString(lbl,tx+1,ty+1);
            g2.setColor(Color.WHITE); g2.drawString(lbl,tx,ty);
            g2.dispose();
        }
    }

    static class DropArrowButton extends JComponent {
        private boolean expanded=false;
        private boolean hovered=false;
        private float arrowAlpha=0.6f;
        private javax.swing.Timer pulseTimer;
        DropArrowButton() {
            setPreferredSize(new Dimension(32,32)); setMaximumSize(new Dimension(32,32));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); setOpaque(false);
            // pulse animation when not expanded
            pulseTimer=new javax.swing.Timer(50,e->{
                if(!expanded){arrowAlpha=(float)(0.5+0.4*Math.abs(Math.sin(System.currentTimeMillis()/600.0)));repaint();}
            }); pulseTimer.start();
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e){hovered=true; repaint();}
                @Override public void mouseExited(MouseEvent e) {hovered=false;repaint();}
            });
        }
        void setExpanded(boolean e){expanded=e; repaint();}
        boolean isExpanded(){return expanded;}
        void addClickListener(ActionListener l) {
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    l.actionPerformed(new ActionEvent(DropArrowButton.this,
                        ActionEvent.ACTION_PERFORMED,"click"));
                }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight(),cx=w/2,cy=h/2;
            float fa=expanded?0.9f:(hovered?0.85f:arrowAlpha);
            g2.setColor(new Color(255,255,255,(int)(fa*255)));
            g2.setStroke(new BasicStroke(2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            if(expanded) {
                // up arrow
                int[] ax={cx-8,cx,cx+8}; int[] ay={cy+4,cy-4,cy+4};
                g2.drawPolyline(ax,ay,3);
            } else {
                // down arrow
                int[] ax={cx-8,cx,cx+8}; int[] ay={cy-4,cy+4,cy-4};
                g2.drawPolyline(ax,ay,3);
            }
            // small "RECENT" label below arrow when not expanded
            if(!expanded) {
                g2.setFont(new Font("SansSerif",Font.BOLD,7));
                g2.setColor(new Color(200,200,200,(int)(fa*200)));
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString("RECENT",(w-fm.stringWidth("RECENT"))/2,h-1);
            }
            g2.dispose();
        }
    }

    public IntroScreen() {
        try {
            UIManager.setLookAndFeel(loadTheme().equals("dark")
                ? new FlatDarculaLaf() : new FlatIntelliJLaf());
        } catch (Exception e) { e.printStackTrace(); }

        BufferedImage bannerImg = loadResource("/icons/banner.png");
        BufferedImage bgImg     = loadRandomBackground();
        FlatSVGIcon logoIcon = new FlatSVGIcon("icons/nukepadlogo.svg", 24, 24);
        Image logoImg = logoIcon.getImage();

        introFrame = new JFrame("Welcome to Nukepad!");
        if (logoImg != null)
            introFrame.setIconImage(logoImg);
        else if (bannerImg != null)
            introFrame.setIconImage(bannerImg.getScaledInstance(32,32,Image.SCALE_SMOOTH));
        introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        introFrame.setSize(700, 580);
        introFrame.setLocationRelativeTo(null);
        introFrame.setResizable(false);

        BackgroundPanel root = new BackgroundPanel(bgImg);
        root.setBorder(new EmptyBorder(28, 44, 28, 44));
        introFrame.setContentPane(root);

        AnimatedBanner banner = new AnimatedBanner(bannerImg);

        JLabel subtitle = new JLabel("Open a file or project to get started");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(200,200,200));
        subtitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        SlideButton openFileBtn    = new SlideButton("Open File",             false);
        SlideButton openProjectBtn = new SlideButton("Open Project / Folder", true);

        openFileBtn.addClickListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fc.showOpenDialog(introFrame) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                new Thread(() -> {
                    try {
                        String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                        saveRecent(file.getAbsolutePath());
                        SwingUtilities.invokeLater(() -> {
                            introFrame.dispose();
                            new Nukepad(file.getParentFile()).openFileInNewTab(file, content);
                        });
                    } catch (Exception ex) { ex.printStackTrace(); }
                }).start();
            }
        });

        openProjectBtn.addClickListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(introFrame) == JFileChooser.APPROVE_OPTION) {
                File folder = fc.getSelectedFile();
                new Thread(() -> {
                    saveRecent(folder.getAbsolutePath());
                    SwingUtilities.invokeLater(() -> {
                        introFrame.dispose();
                        Nukepad ed = new Nukepad(folder);
                        ed.addToOpenedProjects(folder.getAbsolutePath());
                    });
                }).start();
            }
        });

        boolean isDark = loadTheme().equals("dark");
        ThemeButton themeBtn = new ThemeButton(isDark);
        themeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        themeBtn.addClickListener(e -> {
            String next = loadTheme().equals("dark") ? "light" : "dark";
            saveTheme(next);
            try {
                UIManager.setLookAndFeel(next.equals("dark")
                    ? new FlatDarculaLaf() : new FlatIntelliJLaf());
                themeBtn.setDark(next.equals("dark"));
                SwingUtilities.updateComponentTreeUI(introFrame);
                introFrame.repaint();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        JLabel recentsTitle = new JLabel("RECENT");
        recentsTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        recentsTitle.setForeground(new Color(160,160,160));
        recentsTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JPanel recentsPanel = new JPanel();
        recentsPanel.setLayout(new BoxLayout(recentsPanel, BoxLayout.Y_AXIS));
        recentsPanel.setOpaque(false);
        recentsPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        recentsPanel.setMaximumSize(new Dimension(560, 999));
        recentsPanel.add(recentsTitle);
        recentsPanel.add(Box.createVerticalStrut(8));

        List<String> recents = loadRecents();
        if (recents.isEmpty()) {
            JLabel none = new JLabel("No recent files yet.");
            none.setFont(new Font("SansSerif", Font.PLAIN, 12));
            none.setForeground(new Color(120,120,120));
            none.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            recentsPanel.add(none);
        } else {
            for (String p : recents) {
                recentsPanel.add(buildRecentRow(p));
                recentsPanel.add(Box.createVerticalStrut(2));
            }
        }
        recentsPanel.setVisible(false);

        DropArrowButton dropArrow = new DropArrowButton();
        dropArrow.addClickListener(e -> {
            boolean nowExpanded = !dropArrow.isExpanded();
            dropArrow.setExpanded(nowExpanded);
            recentsPanel.setVisible(nowExpanded);
            root.revalidate();
            root.repaint();
        });

        JPanel arrowWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        arrowWrapper.setOpaque(false);
        arrowWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        arrowWrapper.add(dropArrow);

        JPanel btnCol = new JPanel();
        btnCol.setLayout(new BoxLayout(btnCol, BoxLayout.Y_AXIS));
        btnCol.setOpaque(false);
        btnCol.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        btnCol.add(openFileBtn);
        btnCol.add(Box.createVerticalStrut(10));
        btnCol.add(openProjectBtn);
        btnCol.add(Box.createVerticalStrut(10));
        btnCol.add(themeBtn);

        root.add(Box.createVerticalStrut(16));
        root.add(banner);
        root.add(Box.createVerticalStrut(18));
        root.add(subtitle);
        root.add(Box.createVerticalStrut(30));
        root.add(btnCol);
        root.add(Box.createVerticalGlue());
        root.add(recentsPanel);
        root.add(arrowWrapper);

        introFrame.setVisible(true);
        new Thread(this::playOpenSound).start();

        javax.swing.Timer delay = new javax.swing.Timer(180, e -> {
            banner.startAnimation();
            ((javax.swing.Timer)e.getSource()).stop();
        });
        delay.start();
    }

    private JPanel buildRecentRow(String path) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(4,6,4,6));
        row.setMaximumSize(new Dimension(560,34));
        row.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        File f=new File(path); boolean isDir=f.isDirectory();
        Icon ico=isDir?MaterialIconLoader.getIcon("folder"):MaterialIconLoader.forFile(f,false);
        JLabel iconLabel=new JLabel(ico);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0,0,0,6));
        JLabel nameLabel=new JLabel(f.getName());
        nameLabel.setFont(new Font("SansSerif",Font.PLAIN,13));
        nameLabel.setForeground(Color.WHITE);
        JLabel pathLabel=new JLabel(path);
        pathLabel.setFont(new Font("SansSerif",Font.PLAIN,11));
        pathLabel.setForeground(new Color(160,160,160));
        row.add(iconLabel); row.add(nameLabel);
        row.add(Box.createHorizontalStrut(10));
        row.add(pathLabel); row.add(Box.createHorizontalGlue());
        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e){
                row.setOpaque(true);row.setBackground(new Color(255,255,255,28));row.repaint();}
            @Override public void mouseExited(MouseEvent e){
                row.setOpaque(false);row.repaint();}
            @Override public void mouseClicked(MouseEvent e){
                if(!f.exists())return;
                saveRecent(path); introFrame.dispose();
                new Thread(()->{
                    try{
                        if(isDir){
                            SwingUtilities.invokeLater(()->{
                                Nukepad ed=new Nukepad(f);
                                ed.addToOpenedProjects(f.getAbsolutePath());});
                        }else{
                            String content=new String(java.nio.file.Files.readAllBytes(f.toPath()));
                            SwingUtilities.invokeLater(()->{
                                Nukepad ed=new Nukepad(f.getParentFile());
                                ed.openFileInNewTab(f,content);});
                        }
                    }catch(Exception ex){ex.printStackTrace();}
                }).start();
            }
        });
        return row;
    }

    private void saveRecent(String path) {
        List<String> list=loadRecents(); list.remove(path); list.add(0,path);
        if(list.size()>MAX_RECENTS)list=list.subList(0,MAX_RECENTS);
        try(BufferedWriter w=new BufferedWriter(new FileWriter(RECENTS_FILE))){
            for(String p:list){w.write(p);w.newLine();}
        }catch(IOException ex){ex.printStackTrace();}
    }
    private List<String> loadRecents() {
        List<String> list=new ArrayList<>();
        if(!RECENTS_FILE.exists())return list;
        try(BufferedReader r=new BufferedReader(new FileReader(RECENTS_FILE))){
            String line; while((line=r.readLine())!=null)if(!line.isBlank())list.add(line.trim());
        }catch(IOException ex){ex.printStackTrace();}
        return list;
    }
    private void playOpenSound() {
        try{
            java.net.URL url=getClass().getResource("/assets/open.wav");
            if(url==null)return;
            AudioInputStream audio=AudioSystem.getAudioInputStream(url);
            Clip clip=AudioSystem.getClip(); clip.open(audio); clip.start();
        }catch(Exception ex){System.out.println("Sound error: "+ex.getMessage());}
    }
    private void saveTheme(String t) {
        try(BufferedWriter w=new BufferedWriter(new FileWriter(THEME_FILE))){w.write(t);}
        catch(IOException ex){ex.printStackTrace();}
    }
    private String loadTheme() {
        if(!THEME_FILE.exists())return "light";
        try(BufferedReader r=new BufferedReader(new FileReader(THEME_FILE))){
            String line=r.readLine();
            return(line!=null&&!line.isBlank())?line.trim():"light";
        }catch(IOException ex){return "light";}
    }
    private BufferedImage loadRandomBackground() {
        try {
        
        String[] wallpaperFiles = { "bg1.jpg", "bg2.jpg", "bg3.jpg", "bg4.jpg", "bg5.jpg",
        "bg6.jpg", "bg7.jpg", "bg8.jpg", "bg9.jpg", "bg10.jpg", "bg11.jpg", "bg12.jpg",
        "bg13.jpg", "bg14.jpg", "bg15.jpg", "bg16.jpg", "bg17.jpg", "bg18.jpg", "bg19.jpg",
        "bg20.jpg", "bg21.jpg", "bg22.jpg", "bg23.jpg", "bg24.jpg", "bg25.jpg", "bg26.jpg",
        "bg27.jpg"}; 
        
        Random rand = new Random();
        String selected = wallpaperFiles[rand.nextInt(wallpaperFiles.length)];
        
        java.net.URL imgUrl = IntroScreen.class.getResource("/wallpapers/" + selected);
        if (imgUrl == null) return null;
        
        return ImageIO.read(imgUrl);
    } catch (Exception e) {
        System.out.println("Error loading random background: " + e.getMessage());
        return null;
    }
    }

    private static BufferedImage loadResource(String path) {
        try {
            java.net.URL url = IntroScreen.class.getResource(path);
            if (url == null) {
                System.out.println("Resource not found: " + path);
                return null;
            }
            return ImageIO.read(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
