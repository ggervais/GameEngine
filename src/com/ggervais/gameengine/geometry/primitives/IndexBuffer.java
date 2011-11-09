package com.ggervais.gameengine.geometry.primitives;

import java.util.*;

public class IndexBuffer {

	private Map<Integer, List<Integer>> indices;
	
	public IndexBuffer() {
		this.indices = new HashMap<Integer, List<Integer>>();
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
}
