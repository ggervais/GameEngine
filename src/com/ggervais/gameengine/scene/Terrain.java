package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.geometry.Grid;
import com.ggervais.gameengine.geometry.Model;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;

public class Terrain extends DisplayableEntity {

	private int nbCellsWidth;
	private int nbCellsDepth;
	
	public Terrain(Grid grid) {
		super(grid);
		this.nbCellsWidth = grid.getNbCellsWidth();
		this.nbCellsDepth = grid.getNbCellsDepth();
	}

	public float getHeightAtPosition(Point3D position) {
		
		if (position == null) {
			return 0.0f;
		}
		
		float height = 0;
		
		int nbVertices = this.getModel().getVertexBuffer().size();
		Vertex firstVertex = this.getModel().getVertexBuffer().getVertex(0);
		Vertex lastVertex = this.getModel().getVertexBuffer().getVertex(nbVertices - 1);
		
		float minX = firstVertex.getPosition().x();
		float minZ = firstVertex.getPosition().z();
		float maxX = lastVertex.getPosition().x();
		float maxZ = lastVertex.getPosition().z();
		
		minX = minX * this.getScale().x() + this.getPosition().x();
		maxX = maxX * this.getScale().x() + this.getPosition().x();
		minZ = minZ * this.getScale().z() + this.getPosition().z();
		maxZ = maxZ * this.getScale().z() + this.getPosition().z();
		
		float px = position.x();
		float pz = position.z();
		
		float cellLengthX = this.getScale().x() / this.nbCellsWidth;
		float cellLengthZ = this.getScale().z() / this.nbCellsDepth;
		
		if (minX <= px && px <= maxX && minZ <= pz && pz <= maxZ) {
			int cx = (int) ((px - minX) / cellLengthX);
			int cz = (int) ((pz - minZ) / cellLengthZ);
			
			int index = (this.nbCellsWidth + 1) * cz + cx;
			
			Vertex first = this.getModel().getVertexBuffer().getVertex(index);
			float fx = first.getPosition().x() * this.getScale().x() + this.getPosition().x();
			float fy = first.getPosition().y() * this.getScale().y() + this.getPosition().y();
			float fz = first.getPosition().z() * this.getScale().z() + this.getPosition().z();
			
			float dx = position.x() - fx;
			float dz = position.z() - fz;
			
			Vertex second = null;
			Vertex third = null;
			
			if (dz > dx) {
				second = this.getModel().getVertexBuffer().getVertex(index + this.nbCellsWidth + 1);
				third = this.getModel().getVertexBuffer().getVertex(index + this.nbCellsWidth + 2);
			} else {
				second = this.getModel().getVertexBuffer().getVertex(index + this.nbCellsWidth + 2);
				third = this.getModel().getVertexBuffer().getVertex(index + 1);
			}
			
			float sx = second.getPosition().x() * this.getScale().x() + this.getPosition().x();
			float sy = second.getPosition().y() * this.getScale().y() + this.getPosition().y();
			float sz = second.getPosition().z() * this.getScale().z() + this.getPosition().z();
			
			float tx = third.getPosition().x() * this.getScale().x() + this.getPosition().x();
			float ty = third.getPosition().y() * this.getScale().y() + this.getPosition().y();
			float tz = third.getPosition().z() * this.getScale().z() + this.getPosition().z();
			
			Point3D firstTransformed = new Point3D(fx, fy, fz);
			Point3D secondTransformed = new Point3D(sx, sy, sz);
			Point3D thirdTransformed = new Point3D(tx, ty, tz);
			
			Vector3D v1 = secondTransformed.sub(firstTransformed);
			Vector3D v2 = thirdTransformed.sub(firstTransformed);
			
			Vector3D normal = v1.crossProduct(v2).normalized();
			
			height = fy + (normal.x() * dx + normal.z() * dz) / -normal.y();
		}
		
		return height;
	}
	
}
