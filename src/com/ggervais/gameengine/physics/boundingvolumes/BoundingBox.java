package com.ggervais.gameengine.physics.boundingvolumes;

import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;

public class BoundingBox {
	private Point3D minCorner;
	private Point3D maxCorner;

    public BoundingBox(Point3D minCorner, Point3D maxCorner) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
    }

    public Point3D getMinCorner() {
        return this.minCorner;
    }

    public Point3D getMaxCorner() {
        return this.maxCorner;
    }

    public float getWidth() {
        return Math.abs(maxCorner.x() - minCorner.x());
    }

    public float getHeight() {
       return Math.abs(maxCorner.y() - minCorner.y());
    }

    public float getDepth() {
       return Math.abs(maxCorner.z() - minCorner.z());
    }

    public void transform(Matrix4x4 matrix) {
        this.minCorner = matrix.mult(this.minCorner);
        this.maxCorner = matrix.mult(this.maxCorner);
    }
}

