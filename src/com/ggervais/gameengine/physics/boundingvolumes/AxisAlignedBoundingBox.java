package com.ggervais.gameengine.physics.boundingvolumes;

import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;

public class AxisAlignedBoundingBox {

	private Point3D minCorner;
	private Point3D maxCorner;
	
	public AxisAlignedBoundingBox(Point3D min, Point3D max) {
		this.minCorner = min;
		this.maxCorner = max;
	}
	
	public static AxisAlignedBoundingBox buildFromModel(com.ggervais.gameengine.geometry.Model model) {
		
		float maxX = Float.MIN_VALUE;
		float maxY = Float.MIN_VALUE;
		float maxZ = Float.MIN_VALUE;
		
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float minZ = Float.MAX_VALUE;
		
		for(int i = 0; i < model.getVertexBuffer().size(); i++) {
			Vertex vertex = model.getVertexBuffer().getVertex(i);
			Point3D point = vertex.getPosition();
			
			maxX = Math.max(maxX, point.x());
			maxY = Math.max(maxY, point.y());
			maxZ = Math.max(maxZ, point.z());
			
			minX = Math.min(minX, point.x());
			minY = Math.min(minY, point.y());
			minZ = Math.min(minZ, point.z());
		}
		
		return new AxisAlignedBoundingBox(new Point3D(minX, minY, minZ), new Point3D(maxX, maxY, maxZ));
	}

}
