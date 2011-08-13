package com.ggervais.gameengine.geometry.primitives;

import com.ggervais.gameengine.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class VertexBuffer {
	private List<Vertex> vertices;
    private int nbVertices;
	
	public VertexBuffer() {
		this.vertices = new ArrayList<Vertex>();
        resetNbVertices();
	}

    private void resetNbVertices() {
        this.nbVertices = this.vertices.size();
    }
	
	public int size() {
		return this.nbVertices;
	}
	
	public Vertex getVertex(int i) {
		return this.vertices.get(i);
	}
	
	public void addVertex(Vertex vertex) {
		this.vertices.add(vertex);
        resetNbVertices();
	}
	
	public void removeVertex(Vertex vertex) {
		this.vertices.remove(vertex);
        resetNbVertices();
	}

    public void setNbVertices(int size) {
        this.nbVertices = MathUtils.clamp(size, 0, this.vertices.size());
    }
}
