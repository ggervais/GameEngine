package com.ggervais.gameengine.physics.boundingvolumes;

import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.scene.scenegraph.Transformation;

import java.util.ArrayList;
import java.util.List;

// Right now, this class actually represents an AxisAlignedBoundingBox.
// TODO: make this class abstract and subclass it as AxisAlignedBoundingBox (AABB) and OrientedBoudingBox (OBB).
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

    public boolean contains(Point3D point) {
        return minCorner.x() <= point.x() && maxCorner.x() >= point.x() &&
               minCorner.y() <= point.y() && maxCorner.y() >= point.y() &&
               minCorner.z() <= point.z() && maxCorner.z() >= point.z();
    }

    public void transform(Transformation transformation) {

        float width = getWidth();
        float height = getHeight();
        float depth = getDepth();

        Point3D corner1 = this.minCorner;
        Point3D corner2 = Point3D.add(this.minCorner, new Vector3D(width, 0, 0));
        Point3D corner3 = Point3D.add(this.minCorner, new Vector3D(width, 0, depth));
        Point3D corner4 = Point3D.add(this.minCorner, new Vector3D(0, 0, depth));

        Point3D corner5 = Point3D.add(this.minCorner, new Vector3D(0, height, 0));
        Point3D corner6 = Point3D.add(this.minCorner, new Vector3D(0, height, depth));
        Point3D corner7 = Point3D.add(this.minCorner, new Vector3D(width, 0, depth));
        Point3D corner8 = this.maxCorner;

        List<Point3D> corners = new ArrayList<Point3D>();
        corners.add(transformation.getMatrix().mult(corner1));
        corners.add(transformation.getMatrix().mult(corner2));
        corners.add(transformation.getMatrix().mult(corner3));
        corners.add(transformation.getMatrix().mult(corner4));
        corners.add(transformation.getMatrix().mult(corner5));
        corners.add(transformation.getMatrix().mult(corner6));
        corners.add(transformation.getMatrix().mult(corner7));
        corners.add(transformation.getMatrix().mult(corner8));

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;

        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        float maxZ = -Float.MAX_VALUE;

        for (int i = 0; i < corners.size(); i++) {
            Point3D p = corners.get(i);
            minX = Math.min(minX, p.x());
            minY = Math.min(minY, p.y());
            minZ = Math.min(minZ, p.z());

            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
            maxZ = Math.max(maxZ, p.z());
        }

        /*Point3D minCornerCopy = transformation.getMatrix().mult(this.minCorner);
        Point3D maxCornerCopy = transformation.getMatrix().mult(this.maxCorner);
        this.minCorner = new Point3D(Math.min(minCornerCopy.x(), maxCornerCopy.x()), Math.min(minCornerCopy.y(), maxCornerCopy.y()), Math.min(minCornerCopy.z(), maxCornerCopy.z()));
        this.maxCorner = new Point3D(Math.max(minCornerCopy.x(), maxCornerCopy.x()), Math.max(minCornerCopy.y(), maxCornerCopy.y()), Math.max(minCornerCopy.z(), maxCornerCopy.z()));*/

        this.minCorner = new Point3D(minX, minY, minZ);
        this.maxCorner = new Point3D(maxX, maxY, maxZ);
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

    public boolean intersectsOrIsInside(Plane plane) {

        boolean result = true;

        List<Point3D> points = new ArrayList<Point3D>();
        float width = getWidth();
        float height = getHeight();
        float depth = getDepth();

        points.add(this.minCorner);
        points.add(Point3D.add(this.minCorner, new Vector3D(width, 0, 0)));
        points.add(Point3D.add(this.minCorner, new Vector3D(width, 0, depth)));
        points.add(Point3D.add(this.minCorner, new Vector3D(0, 0, depth)));
        points.add(Point3D.add(this.minCorner, new Vector3D(0, height, 0)));
        points.add(Point3D.add(this.minCorner, new Vector3D(0, height, depth)));
        points.add(Point3D.add(this.minCorner, new Vector3D(width, 0, depth)));
        points.add(this.maxCorner);

        int nbIn = 0;
        int nbOut = 0;
        for (int i = 0; i < points.size(); i++) {
            float signedDistance = plane.getSignedDistanceFromPlane(points.get(i));
            if (signedDistance < 0) {
                nbOut++;
            } else {
                nbIn++;
            }

            // TODO do we really need another check to exit the loop prematurely?
            if (nbOut > 0 && nbIn > 0) {
                break;
            }
        }


        if (nbIn == 0) {
            result = false;
        }

        //System.out.println(nbIn);

        return result;
    }

    public BoundingBox copy() {
        return new BoundingBox(this.minCorner.copy(), this.maxCorner.copy());
    }

    public static BoundingBox copy(BoundingBox box) {
        return box.copy();
    }

    public Vector3D intersects(BoundingBox box) {

        boolean intersects = true;

        Vector3D penetrationVector = null;

        // Using the Separating Axis Theorem, or SAT, we can quickly figure out if the bouding boxes are NOT
        // intersecting, so we assume they are intersecting by default and short-circuit as soon a we can, if needed.
        if (this.maxCorner.x() < box.getMinCorner().x() || this.minCorner.x() > box.getMaxCorner().x() |
            this.maxCorner.y() < box.getMinCorner().y() || this.minCorner.y() > box.getMaxCorner().y() ||
            this.maxCorner.z() < box.getMinCorner().z() || this.minCorner.z() > box.getMaxCorner().z()) {

            intersects = false;
        }


        if (intersects) {
            penetrationVector = Vector3D.zero();

            for (int i = 0; i < 3; i++) {
                float minValue = Float.MAX_VALUE;

                if (this.maxCorner.get(i) >= box.getMinCorner().get(i) && this.maxCorner.get(i) <= box.getMaxCorner().get(i)) {
                    if (Math.abs(this.maxCorner.get(i) - box.getMinCorner().get(i)) < Math.abs(minValue)) {
                        minValue = this.maxCorner.get(i) - box.getMinCorner().get(i);
                    }
                }

                if (this.minCorner.get(i) <= box.getMaxCorner().get(i) && this.minCorner.get(i) >= box.getMinCorner().get(i)) {

                    if (Math.abs(this.minCorner.get(i) - box.getMaxCorner().get(i)) < Math.abs(minValue)) {
                        minValue = this.minCorner.get(i) - box.getMaxCorner().get(i);
                    }
                }

                penetrationVector.set(i, minValue);
            }
        }

        return penetrationVector;
    }

    public List<Point3D> getPoints() {

        List<Point3D> points = new ArrayList<Point3D>();

        float width = getWidth();
        float height = getHeight();
        float depth = getDepth();

        points.add(this.minCorner);
        points.add(Point3D.add(this.minCorner, new Vector3D(width, 0, 0)));
        points.add(Point3D.add(this.minCorner, new Vector3D(width, 0, depth)));
        points.add(Point3D.add(this.minCorner, new Vector3D(0, 0, depth)));
        points.add(Point3D.add(this.minCorner, new Vector3D(0, height, 0)));
        points.add(Point3D.add(this.minCorner, new Vector3D(0, height, depth)));
        points.add(Point3D.add(this.minCorner, new Vector3D(width, 0, depth)));
        points.add(this.maxCorner);

        return points;
    }

    public String toString() {
        return this.minCorner + " -> " + this.maxCorner;
    }
}

