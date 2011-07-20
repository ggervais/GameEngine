package com.ggervais.gameengine.material.texture;

import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.resource.Resource;
import com.ggervais.gameengine.resource.ResourceType;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class Texture implements Resource {
	private int[] pixels;
	private int width;
	private int height;
	ByteBuffer byteBuffer;
	String filename;
	private int id;
	boolean keepPixels;
	
	public Texture(String filename, int[] pixels, int width, int height) {
		this.filename = filename;
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		this.byteBuffer = null;
		this.id = -1;
		this.keepPixels = false;
	}
	
	public Texture(String filename, int[] pixels, int width, int height, boolean keepPixels) {
		this(filename, pixels, width, height);
		this.keepPixels = keepPixels;
	}
	
	public String getFilename() {
		return this.filename;
	}
	
	public void setKeepPixels(boolean keepPixels) {
		this.keepPixels = keepPixels;
	}
	
	public boolean keepPixels() {
		return this.keepPixels;
	}
	
	public void buildBuffer() {
		
		this.byteBuffer = ByteBuffer.allocateDirect(this.pixels.length * 4); // RGBA
		
		for (int i = 0; i < this.pixels.length; i++) {
			int pixel  = this.pixels[i];
			byte alpha = (byte) ((pixel >> 24) & 0xFF);
			byte red   = (byte) ((pixel >> 16) & 0xFF);
			byte green = (byte) ((pixel >> 8) & 0xFF);
			byte blue  = (byte) ((pixel >> 0) & 0xFF);
			
			this.byteBuffer.put(red);
			this.byteBuffer.put(green);
			this.byteBuffer.put(blue);
			this.byteBuffer.put(alpha);
		}
		
		this.byteBuffer.flip();
		
		if (!keepPixels) {
			this.pixels = null;
		}
	}
	
	public Buffer getBuffer() {
		if (this.byteBuffer == null) {
			buildBuffer();
		}
		return this.byteBuffer;
	}
	
	public int[] getPixels() {
		return this.pixels;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getPixel(int i, int j) throws ArrayIndexOutOfBoundsException {
		int index = j * this.width + i;
		
		if (index < 0 || index > this.pixels.length - 1) {
			throw new ArrayIndexOutOfBoundsException("You are trying to access an index (" + index + ") lower or higher than the amount of pixels in the image!");
		} else {
			return this.pixels[index];
		}
	}

    public String getName() {
        return this.filename;
    }

    public void init() {
        // This method does nothing for now.
    }

    public void destroy() {
        this.pixels = null;
        this.byteBuffer.clear();
        this.byteBuffer = null;
    }

    public boolean isInitialized() {
        return this.id != -1;
    }

    public ResourceType getType() {
        return ResourceType.TEXTURE;
    }

    public Vector3D getMinBounds(int index) {
        return getMinBounds();
    }

    public Vector3D getMaxBounds(int index) {
        return getMaxBounds();
    }

    public Vector3D getMinBounds() {
        return new Vector3D(0, 0, 0);
    }

    public Vector3D getMaxBounds() {
        return new Vector3D(1, 1, 0);
    }

    public int getNbCellsWidth() {
        return 1;
    }

    public int getNbCellsHeight() {
        return 1;
    }
}
