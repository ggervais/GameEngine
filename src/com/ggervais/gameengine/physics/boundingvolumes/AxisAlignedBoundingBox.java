package com.ggervais.gameengine.physics.boundingvolumes;

import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Ray;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;

public class AxisAlignedBoundingBox {

	private Point3D minCorner;
	private Point3D maxCorner;
	
	public AxisAlignedBoundingBox(Point3D min, Point3D max) {
		this.minCorner = min;
		this.maxCorner = max;
	}
	
	public static AxisAlignedBoundingBox buildFromModel(com.ggervais.gameengine.geometry.Model model) {
		
		float maxX = (-Float.MAX_VALUE);
		float maxY = (-Float.MAX_VALUE);
		float maxZ = (-Float.MAX_VALUE);
		
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

    public boolean isInside(Point3D point) {
        boolean inside = false;

        if (point.x() >= this.minCorner.x() && point.x() <= this.maxCorner.x() &&
            point.y() >= this.minCorner.y() && point.y() <= this.maxCorner.y() &&
            point.z() >= this.minCorner.z() && point.z() <= this.maxCorner.z()) {
            inside = true;
        }

        return inside;
    }

    public Point3D intersects(Ray ray) {

        Vector3D xPositiveNormal = new Vector3D(1, 0, 0);
        Vector3D xNegativeNormal = new Vector3D(-1, 0, 0);
        Vector3D yPositiveNormal = new Vector3D(0, 1, 0);
        Vector3D yNegativeNormal = new Vector3D(0, -1, 0);
        Vector3D zPositiveNormal = new Vector3D(0, 0, 1);
        Vector3D zNegativeNormal = new Vector3D(0, 0, -1);

        Point3D origin = ray.getOrigin();
        Vector3D direction = ray.getDirection().normalized();
        if (isInside(origin)) {
            return origin.copy();
        }

        if (origin.x() > this.maxCorner.x() && direction.dotProduct(xPositiveNormal) < 0) {
            Vector3D extendedDirection = direction.copy();
            float k = Math.abs(this.maxCorner.x() - origin.x()) / Math.abs((origin.x() + direction.x()) - origin.x());
            extendedDirection.multiply(k);

            Point3D candidate = origin.copy();
            candidate.add(extendedDirection);

            if (candidate.y() <= this.maxCorner.y() && candidate.y() >= this.minCorner.y() &&
                candidate.z() <= this.maxCorner.z() && candidate.z() >= this.minCorner.z()) {
                return candidate;
            }
        } else if (origin.x() < this.minCorner.x() && direction.dotProduct(xNegativeNormal) < 0) {
            Vector3D extendedDirection = direction.copy();
            float k = Math.abs(this.minCorner.x() - origin.x()) / Math.abs((origin.x() + direction.x()) - origin.x());
            extendedDirection.multiply(k);

            Point3D candidate = origin.copy();
            candidate.add(extendedDirection);

            if (candidate.y() <= this.maxCorner.y() && candidate.y() >= this.minCorner.y() &&
                candidate.z() <= this.maxCorner.z() && candidate.z() >= this.minCorner.z()) {
                return candidate;
            }
        } else if (origin.y() > this.maxCorner.y() && direction.dotProduct(yPositiveNormal) < 0) {
            Vector3D extendedDirection = direction.copy();
            float k = Math.abs(this.maxCorner.y() - origin.y()) / Math.abs((origin.y() + direction.y()) - origin.y());
            extendedDirection.multiply(k);

            Point3D candidate = origin.copy();
            candidate.add(extendedDirection);

            if (candidate.x() <= this.maxCorner.x() && candidate.x() >= this.minCorner.x() &&
                candidate.z() <= this.maxCorner.z() && candidate.z() >= this.minCorner.z()) {
                return candidate;
            }
        } else if (origin.y() < this.minCorner.y() && direction.dotProduct(yNegativeNormal) < 0) {
            Vector3D extendedDirection = direction.copy();
            float k = Math.abs(this.minCorner.y() - origin.y()) / Math.abs((origin.y() + direction.y()) - origin.y());
            extendedDirection.multiply(k);

            Point3D candidate = origin.copy();
            candidate.add(extendedDirection);

            if (candidate.x() <= this.maxCorner.x() && candidate.x() >= this.minCorner.x() &&
                candidate.z() <= this.maxCorner.z() && candidate.z() >= this.minCorner.z()) {
                return candidate;
            }
        } else if (origin.z() > this.maxCorner.z() && direction.dotProduct(zPositiveNormal) < 0) {
            Vector3D extendedDirection = direction.copy();
            float k = Math.abs(this.maxCorner.z() - origin.z()) / Math.abs((origin.z() + direction.z()) - origin.z());
            extendedDirection.multiply(k);

            Point3D candidate = origin.copy();
            candidate.add(extendedDirection);

            if (candidate.x() <= this.maxCorner.x() && candidate.x() >= this.minCorner.x() &&
                candidate.y() <= this.maxCorner.y() && candidate.y() >= this.minCorner.y()) {
                return candidate;
            }
        }
         else if (origin.z() < this.minCorner.z() && direction.dotProduct(zNegativeNormal) < 0) {
            Vector3D extendedDirection = direction.copy();
            float k = Math.abs(this.minCorner.z() - origin.z()) / Math.abs((origin.z() + direction.z()) - origin.z());
            extendedDirection.multiply(k);

            Point3D candidate = origin.copy();
            candidate.add(extendedDirection);

            if (candidate.x() <= this.maxCorner.x() && candidate.x() >= this.minCorner.x() &&
                candidate.y() <= this.maxCorner.y() && candidate.y() >= this.minCorner.y()) {
                return candidate;
            }
        }

        return null;
    }
}
