/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adonis.Nukepad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author croco
 */
class LineNumberPanel extends JPanel implements CaretListener, DocumentListener {
    private final RSyntaxTextArea textArea;
    private int lastDigits;
    
    public LineNumberPanel(RSyntaxTextArea textArea) {
        this.textArea = textArea;
        // Set font to Consolas
        setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        textArea.getDocument().addDocumentListener(this);
        textArea.addCaretListener(this);
        setPreferredWidth();
        updateColors();
    }
    
    private void updateColors() {
        
        setBackground(textArea.getBackground());
     
        Color bgColor = textArea.getBackground();
        int brightness = (bgColor.getRed() + bgColor.getGreen() + bgColor.getBlue()) / 3;
        
       
        if (brightness < 128) {
            setForeground(new Color(200, 200, 200)); 
        } else {
            setForeground(new Color(50, 50, 50)); // Dark gray for light theme
        }
    }
    
    private void setPreferredWidth() {
        int lines = textArea.getLineCount();
        int digits = String.valueOf(lines).length();
        
        if(digits != lastDigits) {
            lastDigits = digits;
            // Calculate width based on digit count with padding
            int width = 20 + digits * 10;
            setPreferredSize(new Dimension(width, Integer.MAX_VALUE));
            revalidate();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
       
        updateColors();
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(getFont());
        
        // Draw background
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw line numbers with appropriate color
        g2.setColor(getForeground());
        
        FontMetrics fontm = textArea.getFontMetrics(textArea.getFont());
        int lineheight = fontm.getHeight();
        int start = textArea.getVisibleRect().y / lineheight + 1;
        int end = start + textArea.getVisibleRect().height / lineheight;
        
        // Get actual line count from text area
        int actualLines = textArea.getLineCount();
        
        // Only draw line numbers that actually exist
        for(int i = start; i <= end && i <= actualLines; i++) {
            String lineNum = String.valueOf(i);
            int y = i * lineheight - fontm.getDescent();
            
            // Calculate centered X position
            int stringWidth = fontm.stringWidth(lineNum);
            int panelWidth = getWidth();
            int x = (panelWidth - stringWidth) / 2;
            
            g2.drawString(lineNum, x, y);
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
       repaint();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        setPreferredWidth();
        repaint();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
       setPreferredWidth();
       repaint();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        repaint();
    }
}