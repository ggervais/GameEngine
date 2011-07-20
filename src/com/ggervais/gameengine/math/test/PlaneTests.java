package com.ggervais.gameengine.math.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.ggervais.gameengine.math.Plane;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Ray;
import com.ggervais.gameengine.math.Vector3D;


public class PlaneTests extends TestCase {
	public void testDistance() {
		Plane plane = new Plane(new Point3D(1, 2, 0), new Vector3D(0, 0, 1));
		assertEquals(0.0f, plane.getDistanceFromPlane(new Point3D(20, 1000, 0)));
		assertEquals(1.0f, plane.getDistanceFromPlane(new Point3D(20, 1000, -1)));
		assertEquals(1.0f, plane.getDistanceFromPlane(new Point3D(20, 1000, 1)));
	}
	
	public void testPointInsidePlane() {
		Plane plane = new Plane(new Point3D(1, 2, 0), new Vector3D(0, 0, 1));
		assertTrue(plane.isPointOnPlane(new Point3D(20, 1000, 0)));
		assertFalse(plane.isPointOnPlane(new Point3D(20, 1000, 1)));
	}
	
	public void testRayIntersectsPlane() {
		Plane plane = new Plane(new Point3D(0, 0, 0), new Vector3D(0, 0, 1));
		Ray ray1 = new Ray(new Point3D(0, 0, 1), new Vector3D(0, 0, -1));
		Ray ray2 = new Ray(new Point3D(0, 0, -1), new Vector3D(0, 0, 1));
		Ray ray3 = new Ray(new Point3D(0,0, -1), new Vector3D(0, 0, -1));
		assertTrue(plane.doesRayIntersectPlane(ray1));
		assertTrue(plane.doesRayIntersectPlane(ray2));
		assertFalse(plane.doesRayIntersectPlane(ray3));
	}
	
	public void testRayIntersectsPlaneDistance() {
		Plane plane = new Plane(new Point3D(0, 0, 0), new Vector3D(0, 0, 1));
		Ray ray = new Ray(new Point3D(0, 0, 1), new Vector3D(0, 0, -1));
		Point3D point = plane.getRayIntersectionPoint(ray);
		Float distance = plane.getRayIntersectPlaneDistance(ray);
		assertNotNull(distance);
		assertEquals(1.0f, distance.floatValue());
		assertEquals(0.0f, point.x());
		assertEquals(0.0f, point.y());
		assertEquals(0.0f, point.z());
	}
	
	public static Test suite() {
		return new TestSuite(PlaneTests.class);
	}
}
