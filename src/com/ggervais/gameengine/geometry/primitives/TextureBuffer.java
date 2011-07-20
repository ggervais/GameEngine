package com.ggervais.gameengine.geometry.primitives;

import java.util.ArrayList;
import java.util.List;

public class TextureBuffer {
	private List<TextureCoords> coords;
	
	public TextureBuffer() {
		this.coords = new ArrayList<TextureCoords>();
	}
	
	public int size() {
		return this.coords.size();
	}
	
	public TextureCoords getCoords(int i) {
		return this.coords.get(i);
	}
	
	public void addCoords(float tu, float tv) {
		addCoords(new TextureCoords(tu, tv));
	}
	
	public void addCoords(TextureCoords coords) {
		this.coords.add(coords);
	}
	
	public void removeCoords(TextureCoords coords) {
		this.coords.remove(coords);
	}
}
