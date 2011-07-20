package com.ggervais.gameengine.math.test;

import com.ggervais.gameengine.math.Vector3D;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Vector3DTests extends TestCase {
	public void testConstructors() {
		Vector3D vector = new Vector3D();
		assertTrue(vector.x() == 0);
		assertTrue(vector.y() == 0);
		assertTrue(vector.z() == 0);
		
		vector = new Vector3D(1, 2, 3);
		assertTrue(vector.x() == 1);
		assertTrue(vector.y() == 2);
		assertTrue(vector.z() == 3);
	}
	
	public void testAdd() {
		Vector3D v1 = new Vector3D(1, 2, 3);
		Vector3D v2 = new Vector3D(4, 5, 6);
		
		Vector3D sum = Vector3D.add(v1, v2);
		assertTrue(sum.x() == 5);
		assertTrue(sum.y() == 7);
		assertTrue(sum.z() == 9);
		
		v1.add(v2);
		assertTrue(v1.x() == 5);
		assertTrue(v1.y() == 7);
		assertTrue(v1.z() == 9);
	}
	
	public void testSub() {
		Vector3D v1 = new Vector3D(1, 2, 3);
		Vector3D v2 = new Vector3D(4, 5, 6);
		
		Vector3D diff = Vector3D.sub(v1, v2);
		assertTrue(diff.x() == -3);
		assertTrue(diff.y() == -3);
		assertTrue(diff.z() == -3);
		
		v1.sub(v2);
		assertTrue(v1.x() == -3);
		assertTrue(v1.y() == -3);
		assertTrue(v1.z() == -3);
	}
	
	public void testLength() {
		Vector3D v1 = new Vector3D(1, 2, 3);
		assertTrue(v1.length() == (float) Math.sqrt(14));
		
		assertTrue(v1.lengthSquared() == 14);
	}
	
	public void testDotProduct() {
		Vector3D v1 = new Vector3D(1, 2, 3);
		Vector3D v2 = new Vector3D(4, 5, 6);
		
		assertTrue(v1.dotProduct(v1) == 14);
		assertTrue(Vector3D.dotProduct(v1, v1) == 14);
		
		assertTrue(v1.dotProduct(v2) == 32);
		assertTrue(Vector3D.dotProduct(v1, v2) == 32);
		assertTrue(v2.dotProduct(v1) == 32);
		assertTrue(Vector3D.dotProduct(v2, v1) == 32);
	}
	
	public void testCrossProduct() {
		Vector3D v1 = new Vector3D(1, 2, 3);
		Vector3D v2 = new Vector3D(4, 5, 6);
		
		Vector3D result = v1.crossProduct(v2);
		assertTrue(result.x() == -3);
		assertTrue(result.y() == 6);
		assertTrue(result.z() == -3);
		
		result = Vector3D.crossProduct(v1, v2);
		assertTrue(result.x() == -3);
		assertTrue(result.y() == 6);
		assertTrue(result.z() == -3);
		
		result = v2.crossProduct(v1);
		assertTrue(result.x() == 3);
		assertTrue(result.y() == -6);
		assertTrue(result.z() == 3);
		
		result = Vector3D.crossProduct(v2, v1);
		assertTrue(result.x() == 3);
		assertTrue(result.y() == -6);
		assertTrue(result.z() == 3);
		
		result = v1.crossProduct(v1);
		assertTrue(result.x() == 0);
		assertTrue(result.y() == 0);
		assertTrue(result.z() == 0);
		
		result = Vector3D.crossProduct(v1, v1);
		assertTrue(result.x() == 0);
		assertTrue(result.y() == 0);
		assertTrue(result.z() == 0);
	}
	
	public void testNormalize() {
		Vector3D v1 = new Vector3D(1, 2, 3);
		Vector3D norm = v1.normalized();
		assertTrue(norm.x() == 0.26726124f);
		assertTrue(norm.y() == 0.5345225f);
		assertTrue(norm.z() == 0.8017837f);
		assertTrue(Math.round(norm.length()) == 1);
		
		v1.normalize();
		assertTrue(v1.x() == 0.26726124f);
		assertTrue(v1.y() == 0.5345225f);
		assertTrue(v1.z() == 0.8017837f);
		assertTrue(Math.round(norm.length()) == 1);
	}
	
	public static Test suite() {
		return new TestSuite(Vector3DTests.class);
	}
}
