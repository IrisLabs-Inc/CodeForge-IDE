/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.adonis.Nukepad;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class SvgIconLoader {
    
    public static ImageIcon loadSvgIcon(String resourcePath, float width, float height) {
        URL url = SvgIconLoader.class.getResource(resourcePath);
        if (url == null) {
            System.err.println("Resource not found: " + resourcePath);
            return null;
        }
        
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        
        transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, height);
        
        try(InputStream is = url.openStream()) {
            TranscoderInput input = new TranscoderInput(is);
            transcoder.transcode(input, null);
            
            BufferedImage img = transcoder.getBufferedImage();
            if(img == null) {
                System.err.println("Batik failed to render the SVG into an image memory buffer");
                return null;
            }
            
            return new ImageIcon(transcoder.getBufferedImage());
        } catch (Exception e) {
            System.err.println("Failed to transcode SVG: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private static class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage img = null;
        
        @Override
        public BufferedImage createImage(int width, int height) {
           return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage bi, TranscoderOutput to) throws TranscoderException {
            this.img = img;
        }
        public BufferedImage getBufferedImage() {
            return img;
        }
        
    }
}
