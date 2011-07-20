package com.ggervais.gameengine.material.texture.test;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.material.texture.TextureLoader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class TextureLoaderTests extends TestCase {
	
	private int[] getARGB(int pixel) {
		int[] argb = new int[4];
		
		argb[0] = (pixel >> 24) & 0xFF;
		argb[1] = (pixel >> 16) & 0xFF;
		argb[2] = (pixel >> 8) & 0xFF;
		argb[3] = (pixel >> 0) & 0xFF;
		
		return argb;
	}
	
	private byte[] getARGBInBytes(int pixel) {
		byte[] argb = new byte[4];
		
		argb[0] = (byte) ((pixel >> 24) & 0xFF);
		argb[1] = (byte) ((pixel >> 16) & 0xFF);
		argb[2] = (byte) ((pixel >> 8) & 0xFF);
		argb[3] = (byte) ((pixel >> 0) & 0xFF);
		
		return argb;
	}
	
	public void testLoadImageWithNoFile() {
		assertNull(TextureLoader.loadTexture(null));
		assertNull(TextureLoader.loadTexture(""));
		assertNull(TextureLoader.loadTexture("INEXISTING_FILE"));
	}
	
	public void testTextureDimensions() {
		
		List<Texture> texturesToTest = new ArrayList<Texture>();
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture.gif"));
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture.jpg"));
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture.jpg"));
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture_transparent.gif"));
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture_transparent.jpg"));
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture_transparent.png"));
		
		for (Texture texture : texturesToTest) {
			assertTrue(texture.getWidth() == 2 && texture.getHeight() == 2);
		}
	}
	
	public void testPixelsBlackAndWhite() {
		List<Texture> texturesToTest = new ArrayList<Texture>();
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture.gif"));
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture.jpg"));
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture.png"));
	
		for (Texture texture : texturesToTest) {
			int[] pixels = texture.getPixels();
			
			try {
				int pixel1 = texture.getPixel(0, 0);
				int pixel2 = texture.getPixel(0, 1);
				int pixel3 = texture.getPixel(1, 0);
				int pixel4 = texture.getPixel(1, 1);
				
				int[] argb1 = getARGB(pixel1);
				int[] argb2 = getARGB(pixel2);
				int[] argb3 = getARGB(pixel3);
				int[] argb4 = getARGB(pixel4);
				
				byte[] bargb1 = getARGBInBytes(pixel1);
				byte[] bargb2 = getARGBInBytes(pixel2);
				byte[] bargb3 = getARGBInBytes(pixel3);
				byte[] bargb4 = getARGBInBytes(pixel4);
				
				assertTrue((bargb1[0] & 0xFF) == 255 && (bargb1[1] & 0xFF) == 0 && (bargb1[2] & 0xFF) == 0 && (bargb1[3] & 0xFF) == 0);
				assertTrue((bargb2[0] & 0xFF) == 255 && (bargb2[1] & 0xFF) == 255 && (bargb2[2] & 0xFF) == 255 && (bargb2[3] & 0xFF) == 255);
				assertTrue((bargb3[0] & 0xFF) == 255 && (bargb3[1] & 0xFF) == 255 && (bargb3[2] & 0xFF) == 255 && (bargb3[3] & 0xFF) == 255);
				assertTrue((bargb4[0] & 0xFF) == 255 && (bargb4[1] & 0xFF) == 0 && (bargb4[2] & 0xFF) == 0 && (bargb4[3] & 0xFF) == 0);
				
				assertTrue(argb1[0] == 255 && argb1[1] == 0 && argb1[2] == 0 && argb1[3] == 0);
				assertTrue(argb2[0] == 255 && argb2[1] == 255 && argb2[2] == 255 && argb2[3] == 255);
				assertTrue(argb3[0] == 255 && argb3[1] == 255 && argb3[2] == 255 && argb3[3] == 255);
				assertTrue(argb4[0] == 255 && argb4[1] == 0 && argb4[2] == 0 && argb4[3] == 0);
				
			} catch(ArrayIndexOutOfBoundsException aioobe) {
				fail(aioobe.getMessage());
			}
			
		}
	}
	
	public void testPixelsBlackAndTransparentGIFAndPNG() {
		List<Texture> texturesToTest = new ArrayList<Texture>();
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture_transparent.gif"));
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture_transparent.png"));
	
		for (Texture texture : texturesToTest) {
			int[] pixels = texture.getPixels();
			
			try {
				int pixel1 = texture.getPixel(0, 0);
				int pixel2 = texture.getPixel(0, 1);
				int pixel3 = texture.getPixel(1, 0);
				int pixel4 = texture.getPixel(1, 1);
				
				int[] argb1 = getARGB(pixel1);
				int[] argb2 = getARGB(pixel2);
				int[] argb3 = getARGB(pixel3);
				int[] argb4 = getARGB(pixel4);
				
				byte[] bargb1 = getARGBInBytes(pixel1);
				byte[] bargb2 = getARGBInBytes(pixel2);
				byte[] bargb3 = getARGBInBytes(pixel3);
				byte[] bargb4 = getARGBInBytes(pixel4);
				
				int value = 0;
				if (texture.getFilename().toLowerCase().endsWith("gif")) {
					value = 255;
				} else if (texture.getFilename().toLowerCase().endsWith("png")) {
					value = 0;
				}
				
				assertTrue((bargb1[0] & 0xFF) == 255 && (bargb1[1] & 0xFF) == 0 && (bargb1[2] & 0xFF) == 0 && (bargb1[3] & 0xFF) == 0);
				assertTrue((bargb2[0] & 0xFF) == 0 && (bargb2[1] & 0xFF) == value && (bargb2[2] & 0xFF) == value && (bargb2[3] & 0xFF) == value);
				assertTrue((bargb3[0] & 0xFF) == 0 && (bargb3[1] & 0xFF) == value && (bargb3[2] & 0xFF) == value && (bargb3[3] & 0xFF) == value);
				assertTrue((bargb4[0] & 0xFF) == 255 && (bargb4[1] & 0xFF) == 0 && (bargb4[2] & 0xFF) == 0 && (bargb4[3] & 0xFF) == 0);
				
				assertTrue(argb1[0] == 255 && argb1[1] == 0 && argb1[2] == 0 && argb1[3] == 0);
				assertTrue(argb2[0] == 0 && argb2[1] == value && argb2[2] == value && argb2[3] == value);
				assertTrue(argb3[0] == 0 && argb3[1] == value && argb3[2] == value && argb3[3] == value);
				assertTrue(argb4[0] == 255 && argb4[1] == 0 && argb4[2] == 0 && argb4[3] == 0);
				
			} catch(ArrayIndexOutOfBoundsException aioobe) {
				fail(aioobe.getMessage());
			}
		}
	}
	
	public void testPixelsBlackAndTransparentJPG() {
		List<Texture> texturesToTest = new ArrayList<Texture>();
		texturesToTest.add(TextureLoader.loadTexture("test_data/test_texture_transparent.jpg"));
	
		for (Texture texture : texturesToTest) {
			int[] pixels = texture.getPixels();
			
			try {
				int pixel1 = texture.getPixel(0, 0);
				int pixel2 = texture.getPixel(0, 1);
				int pixel3 = texture.getPixel(1, 0);
				int pixel4 = texture.getPixel(1, 1);
				
				int[] argb1 = getARGB(pixel1);
				int[] argb2 = getARGB(pixel2);
				int[] argb3 = getARGB(pixel3);
				int[] argb4 = getARGB(pixel4);
				
				byte[] bargb1 = getARGBInBytes(pixel1);
				byte[] bargb2 = getARGBInBytes(pixel2);
				byte[] bargb3 = getARGBInBytes(pixel3);
				byte[] bargb4 = getARGBInBytes(pixel4);
				
				assertTrue((bargb1[0] & 0xFF) == 255 && (bargb1[1] & 0xFF) == 0 && (bargb1[2] & 0xFF) == 0 && (bargb1[3] & 0xFF) == 0);
				assertTrue((bargb2[0] & 0xFF) == 255 && (bargb2[1] & 0xFF) == 255 && (bargb2[2] & 0xFF) == 255 && (bargb2[3] & 0xFF) == 255);
				assertTrue((bargb3[0] & 0xFF) == 255 && (bargb3[1] & 0xFF) == 255 && (bargb3[2] & 0xFF) == 255 && (bargb3[3] & 0xFF) == 255);
				assertTrue((bargb4[0] & 0xFF) == 255 && (bargb4[1] & 0xFF) == 0 && (bargb4[2] & 0xFF) == 0 && (bargb4[3] & 0xFF) == 0);
				
				assertTrue(argb1[0] == 255 && argb1[1] == 0 && argb1[2] == 0 && argb1[3] == 0);
				assertTrue(argb2[0] == 255 && argb2[1] == 255 && argb2[2] == 255 && argb2[3] == 255);
				assertTrue(argb3[0] == 255 && argb3[1] == 255 && argb3[2] == 255 && argb3[3] == 255);
				assertTrue(argb4[0] == 255 && argb4[1] == 0 && argb4[2] == 0 && argb4[3] == 0);
				
			} catch(ArrayIndexOutOfBoundsException aioobe) {
				fail(aioobe.getMessage());
			}
		}
	}
	
	public void testGetBuffer() {
		Texture texture = TextureLoader.loadTexture("test_data/test_texture_transparent.gif");
		
		int expectedCapacity = texture.getPixels().length * 4;
		
		Buffer buffer = texture.getBuffer();
		
		assertTrue(buffer.capacity() == expectedCapacity);
		assertTrue(buffer.position() == 0);
		
		assertTrue(texture.getPixels() == null);
	}
	
	public static Test suite() {
		return new TestSuite(TextureLoaderTests.class);
	}
}
