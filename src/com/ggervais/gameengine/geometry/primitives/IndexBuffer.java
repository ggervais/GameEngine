package com.ggervais.gameengine.geometry.primitives;

import java.util.*;

public class IndexBuffer {

    private Map<Integer, Integer> ids; // From rendering engine (OpenGL, DirectX, etc.).
	private Map<Integer, List<Integer>> indices;
	
	public IndexBuffer() {
		this.indices = new HashMap<Integer, List<Integer>>();
        this.ids = new HashMap<Integer, Integer>();
	}
	
	public int size() {
        int sum = 0;
        for (int nbVertices : this.indices.keySet()) {
            sum += this.indices.get(nbVertices).size();
        }
		return sum;
	}
	
	public int getIndex(int nbVertices, int i) {
		return this.indices.get(nbVertices).get(i);
	}
	
	public void addIndex(int nbVertices, int index) {
        if (!this.indices.containsKey(nbVertices)) {
            this.indices.put(nbVertices, new ArrayList<Integer>());
        }
		this.indices.get(nbVertices).add(index);
	}

    public List<Integer> getNbVerticesList() {
        List<Integer> returnList = new ArrayList<Integer>();
        for (int nbVertices : this.indices.keySet()) {
            returnList.add(nbVertices);
        }
        return returnList;
    }

    public boolean hasSubIndexBuffer(int nbVertices) {
        return this.indices.containsKey(nbVertices);
    }

    public List<Integer> getSubIndexBuffer(int nbVertices) {
        return this.indices.get(nbVertices);
    }
    
    public int getId(int nbVerticesPerFace) {
        int id = -1;
        if (this.ids.containsKey(nbVerticesPerFace)) {
            id = this.ids.get(nbVerticesPerFace);
        }
        return id;
    }
    
    public void setId(int nbVerticesPerFace, int id) {
        this.ids.put(nbVerticesPerFace, id);
    }

    public int[] getIndexAsIntegerArray(int nbVerticesPerFace, int nbVertices) {

        List<Integer> intermediateIndices = new ArrayList<Integer>();
        for (int index : this.indices.get(nbVerticesPerFace)) {
            if (index < nbVertices) {
                intermediateIndices.add(index);
            }
        }

        int[] buffer = new int[intermediateIndices.size()];
        int bufferIndex = 0;
        for (int index : intermediateIndices) {
            buffer[bufferIndex] = index;
            bufferIndex++;
        }
        return buffer;
    }   
    
    public int getNbIndices(int nbVerticesPerFace) {
        return this.indices.get(nbVerticesPerFace).size();
    }

    public int getNbIndices(int nbVerticesPerFace, int nbVertices) {
        int nbIndices = 0;
        for (int index : this.indices.get(nbVerticesPerFace)) {
            if (index < nbVertices) {
                nbIndices++;
            }
        }
        return nbIndices;
    }
}
