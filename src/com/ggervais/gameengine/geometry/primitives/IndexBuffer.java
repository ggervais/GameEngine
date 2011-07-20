package com.ggervais.gameengine.geometry.primitives;

import java.util.ArrayList;
import java.util.List;

public class IndexBuffer {

	private List<Integer> indices;
	
	public IndexBuffer() {
		this.indices = new ArrayList<Integer>();
	}
	
	public int size() {
		return this.indices.size();
	}
	
	public int getIndex(int i) {
		return this.indices.get(i);
	}
	
	public void addIndex(int index) {
		this.indices.add(index);
	}
	
	public void removeIndex(Integer index) {
		this.indices.remove(index);
	}
}
