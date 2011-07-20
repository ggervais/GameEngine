package com.ggervais.gameengine.math.test;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Point3DTests extends TestCase {

	public void testConstructors() {
		Point3D point1 = new Point3D();
		assertTrue(point1.x() == 0);
		assertTrue(point1.y() == 0);
		assertTrue(point1.z() == 0);
		
		Point3D point2 = new Point3D(1, 2, 3);
		assertTrue(point2.x() == 1);
		assertTrue(point2.y() == 2);
		assertTrue(point2.z() == 3);
	}
	
	public void testAdd() {
		Point3D point = new Point3D(1, 2, 3);
		Vector3D vector = new Vector3D(3, 2, 1);
		
		point.add(vector);
		assertTrue(point.x() == 4);
		assertTrue(point.y() == 4);
		assertTrue(point.z() == 4);
		
		Point3D result = Point3D.add(point, vector);
		assertTrue(result.x() == 7);
		assertTrue(result.y() == 6);
		assertTrue(result.z() == 5);
	}
	
	public void testSub() {
		Point3D point = new Point3D(1, 2, 3);
		Vector3D vector = new Vector3D(3, 2, 1);
		
		point.sub(vector);
		assertTrue(point.x() == -2);
		assertTrue(point.y() == 0);
		assertTrue(point.z() == 2);
		
		Point3D result = Point3D.sub(point, vector);
		assertTrue(result.x() == -5);
		assertTrue(result.y() == -2);
		assertTrue(result.z() == 1);
	}
	
	public void testDistance() {
		Point3D point1 = new Point3D(1, 2, 3);
		Point3D point2 = new Point3D(4, 5, 6);
		
		float distance = Point3D.distance(point1, point2);
		assertTrue(distance == (float) Math.sqrt(27));
		
		distance = point1.distance(point2);
		assertTrue(distance == (float) Math.sqrt(27));
		
		distance = point2.distance(point1);
		assertTrue(distance == (float) Math.sqrt(27));
		
		distance = point1.distance(point1);
		assertTrue(distance == 0);
		
		distance = point2.distance(point2);
		assertTrue(distance == 0);
	}
	
	public static Test suite() {
		return new TestSuite(Point3DTests.class);
	}
}
