package com.ggervais.gameengine.geometry.primitives;

import com.ggervais.gameengine.math.MathUtils;
import com.ggervais.gameengine.math.Point3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VertexBuffer {
    private int id; // From rendering engine (OpenGL, DirectX, etc.).
	private List<Vertex> vertices;
    private int nbVertices;
	private static final Random rand = new Random();
    
	public VertexBuffer() {
        this.id = -1;
		this.vertices = new ArrayList<Vertex>();
        resetNbVertices();
	}

    public int getRealSize() {
        return this.vertices.size();
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

    public void clear() {
        this.vertices.clear();
        resetNbVertices();
    }

    public VertexBuffer copy() {
        VertexBuffer vertexBufferCopy = new VertexBuffer();
        for (Vertex vertex : this.vertices) {
            vertexBufferCopy.addVertex(vertex.copy());
        }
        return vertexBufferCopy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
