package com.ggervais.gameengine.physics.boundingvolumes;

import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Ray;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.scene.scenegraph.Transformation;

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

    public void transform(Transformation transformation) {
        this.minCorner = transformation.getMatrix().mult(this.minCorner);
        this.maxCorner = transformation.getMatrix().mult(this.maxCorner);
    }

    public void transform(Matrix4x4 matrix) {
        this.minCorner = matrix.mult(this.minCorner);
        this.maxCorner = matrix.mult(this.maxCorner);
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

    public void grow(BoundingBox subBoundingBox) {
        this.minCorner.x(Math.min(this.minCorner.x(), subBoundingBox.minCorner.x()));
        this.minCorner.y(Math.min(this.minCorner.y(), subBoundingBox.minCorner.y()));
        this.minCorner.z(Math.min(this.minCorner.z(), subBoundingBox.minCorner.z()));

        this.maxCorner.x(Math.max(this.maxCorner.x(), subBoundingBox.maxCorner.x()));
        this.maxCorner.y(Math.max(this.maxCorner.y(), subBoundingBox.maxCorner.y()));
        this.maxCorner.z(Math.max(this.maxCorner.z(), subBoundingBox.maxCorner.z()));
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
        }

        if (origin.y() > this.maxCorner.y() && direction.dotProduct(yPositiveNormal) < 0) {
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
        }

        if (origin.z() > this.maxCorner.z() && direction.dotProduct(zPositiveNormal) < 0) {
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

    public BoundingBox copy() {
        return new BoundingBox(this.minCorner.copy(), this.maxCorner.copy());
    }

    public static BoundingBox copy(BoundingBox box) {
        return box.copy();
    }
}

