package com.ggervais.gameengine.render;


import com.ggervais.gameengine.resource.Resource;
import com.ggervais.gameengine.resource.ResourceType;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Font implements Resource {

    private static final Logger log = Logger.getLogger(Font.class);
    private BufferedImage tempBitmap;
    private BufferedImage asciiBitmap;
    private java.awt.Font font;
    private String fontFileName;

    public Font() {
        this("");
    }

    public Font(String fontFileName) {
        this.fontFileName = fontFileName;
        this.tempBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        init();
    }

    public String getName() {
        return this.fontFileName;
    }

    public void init() {
        if (isInitialized()) {
            return;
        }

        boolean createDefaultFont = false;
        try {
            this.font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File(this.fontFileName));
        } catch (FontFormatException ffe) {
            log.error("FontFormatException occurred while loading font file " + this.fontFileName + ": " + ffe.getMessage());
            createDefaultFont = true;
        } catch (IOException ioe) {
            log.error("IOException occurred while load font file " + this.fontFileName + ": " + ioe.getMessage());
            createDefaultFont = true;
        }

        if (createDefaultFont) {
            this.font = new java.awt.Font("Courier New", java.awt.Font.PLAIN, 20);
        }

        createAsciiBitmap();
    }

    public void createAsciiBitmap() {

        double minWidth = Double.MAX_VALUE;
        double maxWidth = (-Double.MAX_VALUE);
        double minHeight = Double.MAX_VALUE;
        double maxHeight = (-Double.MAX_VALUE);

        for (int i = 0; i < 256; i++) {
            char character = (char) i;
            String individualCharacter = character + "";
            FontProperties properties = getFontPropertiesForText(individualCharacter);
            double width = properties.width;
            double height = properties.height;//properties.ascent + properties.descent;

            minWidth = Math.min(width, minWidth);
            maxWidth = Math.max(width, maxWidth);
            minHeight = Math.min(height, minHeight);
            maxHeight = Math.max(height, maxHeight);
        }

        /*if (maxWidth < maxHeight) {
            maxWidth = maxHeight;
        } else if (maxHeight < maxWidth) {
            maxHeight = maxWidth;
        }*/

        double width = maxWidth * 16;
        double height = maxHeight * 16 + 16 * 6;

        this.asciiBitmap = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics g = this.asciiBitmap.getGraphics();
        g.setFont(this.font);
        g.setColor(new Color(0, 0, 0, 255));
        g.fillRect(0, 0, this.asciiBitmap.getWidth(), this.asciiBitmap.getHeight());
        g.setColor(Color.WHITE);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                char character = (char) ((i * 16) + j);
                String individualCharacter = character + "";
                int x = j * (int) Math.ceil(maxWidth);
                int y = i * ((int) maxHeight + 6) + (int) maxHeight - 3;
                g.drawString(individualCharacter + "", x, y);
            }
        }

        g.dispose();
    }

    public FontProperties getFontPropertiesForText(String text) {
        if (!isInitialized()) {
            init();
        }

        if (this.font != null) {
            Graphics g = this.tempBitmap.getGraphics();

            g.setFont(this.font);
            FontMetrics metrics = g.getFontMetrics();
            Rectangle2D bounds = metrics.getStringBounds(text, g);
            g.dispose();

            FontProperties properties = new FontProperties();
            properties.width = bounds.getWidth();
            properties.height = bounds.getHeight();
            properties.ascent = metrics.getAscent();
            properties.descent = metrics.getDescent();

            return properties;
        }

        return null;
    }

    public BufferedImage getBitmapForText(String text) {

        BufferedImage bitmap = null;

        if (!isInitialized()) {
            init();
        }

        if (this.font != null) {
            Graphics g = this.tempBitmap.getGraphics();
            g.setFont(this.font);
            FontMetrics metrics = g.getFontMetrics();
            Rectangle2D bounds = metrics.getStringBounds(text, g);
            g.dispose();

            double width = bounds.getWidth();
            double height = metrics.getAscent() + metrics.getDescent();//bounds.getHeight();
            bitmap = new BufferedImage((int) Math.ceil(width), (int) Math.ceil(height), BufferedImage.TYPE_4BYTE_ABGR);

            g = bitmap.getGraphics();
            g.setFont(this.font);
            g.setColor(new Color(255, 255, 255, 0));
            g.fillRect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            g.setColor(Color.GREEN);
            g.drawString(text, 0, metrics.getAscent());
            g.dispose();
        }

        return bitmap;
    }

    public void destroy() {

    }

    public boolean isInitialized() {
        return (this.font != null);
    }

    public ResourceType getType() {
        return ResourceType.FONT;
    }

    public BufferedImage getAsciiBitmap() {
        return this.asciiBitmap;
    }

    public static void main(String[] args) {
        Font font = new Font("");
        BufferedImage image = font.getAsciiBitmap();
        try {
            ImageIO.write(image, "png", new File("image.png"));
        } catch (IOException ioe) {

        }
    }
}
