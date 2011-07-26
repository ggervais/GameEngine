package com.ggervais.gameengine.math.test;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RotationMatrixTests extends TestCase {
	public void testIdentity() {
		RotationMatrix matrix = new RotationMatrix();
		
		assertTrue(matrix.getElement(1, 1) == 1);
		assertTrue(matrix.getElement(1, 2) == 0);
		assertTrue(matrix.getElement(1, 3) == 0);
		assertTrue(matrix.getElement(1, 4) == 0);
		
		assertTrue(matrix.getElement(2, 1) == 0);
		assertTrue(matrix.getElement(2, 2) == 1);
		assertTrue(matrix.getElement(2, 3) == 0);
		assertTrue(matrix.getElement(2, 4) == 0);
		
		assertTrue(matrix.getElement(3, 1) == 0);
		assertTrue(matrix.getElement(3, 2) == 0);
		assertTrue(matrix.getElement(3, 3) == 1);
		assertTrue(matrix.getElement(3, 4) == 0);
		
		assertTrue(matrix.getElement(4, 1) == 0);
		assertTrue(matrix.getElement(4, 2) == 0);
		assertTrue(matrix.getElement(4, 3) == 0);
		assertTrue(matrix.getElement(4, 4) == 1);
	}
	
	public void testRotatePoint() {
		Point3D point = new Point3D(1, 0, 0);
		RotationMatrix matrix = new RotationMatrix();
		
		// Rounding values prevents precision error (10E-8 instead of 0, for instance).
		matrix.setYawPitchRoll((float) Math.PI / 2, 0.0f, 0.0f);
		Point3D newPoint = matrix.mult(point);
		assertTrue(Math.round(newPoint.x()) == 0);
		assertTrue(Math.round(newPoint.y()) == 1);
		assertTrue(Math.round(newPoint.z()) == 0);
	
		matrix.setYawPitchRoll(0.0f, 0.0f, (float) Math.PI / 2);
		newPoint = matrix.mult(newPoint);
		assertTrue(Math.round(newPoint.x()) == 0);
		assertTrue(Math.round(newPoint.y()) == 0);
		assertTrue(Math.round(newPoint.z()) == 1);
		
		matrix.setYawPitchRoll(0.0f, (float) Math.PI / 2, 0.0f);
		newPoint = matrix.mult(newPoint);
		assertTrue(Math.round(newPoint.x()) == 1);
		assertTrue(Math.round(newPoint.y()) == 0);
		assertTrue(Math.round(newPoint.z()) == 0);
	}
	
	public static Test suite() {
		return new TestSuite(RotationMatrixTests.class);
	}
}
