package com.ggervais.gameengine.geometry.primitives;

import java.util.ArrayList;
import java.util.List;

public class VertexBuffer {
	private List<Vertex> vertices;
	
	public VertexBuffer() {
		this.vertices = new ArrayList<Vertex>();
	}
	
	public int size() {
		return this.vertices.size();
	}
	
	public Vertex getVertex(int i) {
		return this.vertices.get(i);
	}
	
	public void addVertex(Vertex vertex) {
		this.vertices.add(vertex);
	}
	
	public void removeVertex(Vertex vertex) {
		this.vertices.remove(vertex);
	}
}
