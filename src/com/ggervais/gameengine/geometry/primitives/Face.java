package com.ggervais.gameengine.geometry.primitives;

import java.util.ArrayList;
import java.util.List;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;

public class Face {
	private List<Vertex> vertices;
	private List<TextureCoords> textureCoords;
	
	public Face() {
		this.vertices = new ArrayList<Vertex>();
		this.textureCoords = new ArrayList<TextureCoords>();
	}
	
	public int nbVertices() {
		return this.vertices.size();
	}
	
	public void addVertex(Vertex vertex) {
		this.vertices.add(vertex);
	}
	
	public void addTextureCoords(TextureCoords coords) {
		this.textureCoords.add(coords);
	}
	
	public Vertex getVertex(int index) {
		return this.vertices.get(index);
	}
	
	public int nbTextureCoords() {
		return this.textureCoords.size();
	}
	
	public TextureCoords getTextureCoords(int index) {
		return this.textureCoords.get(index);
	}
}
