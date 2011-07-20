package com.ggervais.gameengine.math;

public class Plane {
	private Point3D point;
	private Vector3D normal;
	
	public float getDistanceFromPlane(Point3D p) {
		Vector3D v = p.sub(this.point);
		float distance = Math.abs(this.normal.dotProduct(v));
		return distance;
	}
	
	public boolean isPointOnPlane(Point3D p) {
		return getDistanceFromPlane(p) == 0;
	}
	
	public Point3D getRayIntersectionPoint(Ray ray) {
		Point3D result = null;
		
		Float distance = getRayIntersectPlaneDistance(ray);
		if (distance != null) {
			float d = distance.floatValue();
			result = Point3D.add(ray.getOrigin(), ray.getDirection().normalized().multiplied(d));
		}
		
		return result;
	}
	
	public boolean doesRayIntersectPlane(Ray ray) {
		return getRayIntersectPlaneDistance(ray) != null;
	}
	
	public Float getRayIntersectPlaneDistance(Ray ray) {
		
		boolean result = false;
		
		Vector3D v = this.point.sub(ray.getOrigin());
		float numerator = v.dotProduct(this.normal);
		float denominator = ray.getDirection().dotProduct(this.normal);
		
		float distance = 0;
		
		if (numerator != 0 && denominator == 0) {
			result = false;
		} else if (numerator == 0 && denominator == 0) {
			result = true;
		} else {
			result = true;
			distance = numerator / denominator;
		}
		
		// Handle the case where the line intersects, but not the ray
		// (because of its origin/direction).
		if (result) {
			Vector3D originPointVector = ray.getOrigin().sub(this.point);
			float dotProduct = Vector3D.dotProduct(originPointVector, ray.getDirection());
			
			if (dotProduct > 0) {
				result = false;
			}
		}
		
		return result ? new Float(distance) : null;
	}
	
	public static Plane create(Point3D p1, Point3D p2, Point3D p3) {
		Vector3D normal = Vector3D.crossProduct(p2.sub(p1), p3.sub(p1)).normalized();
		return new Plane(p1, normal);
	}
	
	public Plane(Point3D point, Vector3D normal) {
		this.point = point;
		this.normal = normal.normalized();
	}
	
	public Plane() {
		this(new Point3D(), new Vector3D());
	}
	
	public void setPoint(Point3D point) {
		this.point = point;
	}
	
	public Point3D getPoint() {
		return this.point;
	}
	
	public void setNormal(Vector3D normal) {
		this.normal = normal;
	}
	
	public Vector3D getNormal() {
		return this.normal;
	}
}
