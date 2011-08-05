package com.ggervais.gameengine.material.texture;

import com.ggervais.gameengine.render.Font;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TextureLoader {

    private static Texture doLoadTexture(String filename) {
		if (filename == null) {
			return null;
		}
		
		File imageFile = new File(filename);
		if (!imageFile.exists()) {
			return null;
		}
		
		int width = 0;
		int height = 0;
		int[] pixels = null;
		PixelGrabber grabber = null;
		try {
			BufferedImage image = ImageIO.read(imageFile);
			width = image.getWidth();
			height = image.getHeight();
			pixels = new int[width * height];
			
			grabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
			grabber.grabPixels();
		} catch (IOException ioe) {
			return null;
		} catch (InterruptedException ie) {
			return null;
		}
		
		if ((grabber.getStatus() & ImageObserver.ABORT) != 0) {
			return null;
		}
		
		Texture texture = new Texture(filename, pixels, width, height);
		return texture;
	}

    public static Texture loadDefaultFontAsciiTexture() {
        Font font = new Font();

        int width = 0;
        int height = 0;
        int[] pixels = null;
		PixelGrabber grabber = null;

        try {
            BufferedImage image = font.getAsciiBitmap();
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];

			grabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
			grabber.grabPixels();
        } catch (InterruptedException ie) {
            return null;
        }

        if ((grabber.getStatus() & ImageObserver.ABORT) != 0) {
			return null;
		}

		Texture texture = new Texture("FONT_DEFAULT_ASCII", pixels, width, height);
		return texture;
    }

    public static Texture loadTextAsTexture(String text) {
        Font font = new Font();

        int width = 0;
        int height = 0;
        int[] pixels = null;
		PixelGrabber grabber = null;

        try {
            BufferedImage image = font.getBitmapForText(text);
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];

			grabber = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
			grabber.grabPixels();
        } catch (InterruptedException ie) {
            return null;
        }

        if ((grabber.getStatus() & ImageObserver.ABORT) != 0) {
			return null;
		}

		Texture texture = new Texture("FONT_" + text, pixels, width, height);
		return texture;
    }
	
	public static Texture loadTexture(String filename) {
		return doLoadTexture(filename);
	}
	
	public static Texture loadTexture(String filename, boolean keepPixels) {
		Texture texture = doLoadTexture(filename);
		if (texture != null) {
			texture.setKeepPixels(keepPixels);
		}
		return texture;
	}

    public static Texture loadTexture(String filename, int nbCellsWidth, int nbCellsHeight) {
        Texture texture = doLoadTexture(filename);
		if (texture != null) {
            texture = new GridTexture(texture, nbCellsWidth, nbCellsHeight);
		}
		return texture;
    }

    public static Texture loadTexture(String filename, boolean keepPixels, int nbCellsWidth, int nbCellsHeight) {
        Texture texture = doLoadTexture(filename);
		if (texture != null) {
			texture.setKeepPixels(keepPixels);
            texture = new GridTexture(texture, nbCellsWidth, nbCellsHeight);
		}
		return texture;
    }
}
