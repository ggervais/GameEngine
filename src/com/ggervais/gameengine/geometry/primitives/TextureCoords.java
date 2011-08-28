package com.ggervais.gameengine.geometry.primitives;

public class TextureCoords {
	private float tu;
	private float tv;
	
	public TextureCoords(float tu, float tv) {
		this.tu = tu;
		this.tv = tv;
	}
	
	public TextureCoords() {
		this.tu = 0;
		this.tv = 0;
	}
	
	public void setTu(float tu) {
		this.tu = tu;
	}
	public float getTextureU() {
		return tu;
	}
	public void setTv(float tv) {
		this.tv = tv;
	}
	public float getTextureV() {
		return tv;
	}

    public String toString() {
        return "{" + this.tu + ", " + this.tv + "}";
    }
	
	
}
