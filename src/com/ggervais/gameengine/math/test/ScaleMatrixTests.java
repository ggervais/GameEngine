package com.ggervais.gameengine.math.test;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.ScaleMatrix;
import com.ggervais.gameengine.math.TranslationMatrix;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ScaleMatrixTests extends TestCase {
	public void testIdentity() {
		ScaleMatrix matrix = new ScaleMatrix();
		
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
	
	public void testScalePoint() {
		ScaleMatrix matrix = new ScaleMatrix(1, 2, 3);
		Point3D point = new Point3D(6, 10, 13);
		
		Point3D newPoint = matrix.mult(point);
		
		assertTrue(newPoint.x() == 6);
		assertTrue(newPoint.y() == 20);
		assertTrue(newPoint.z() == 39);
	}
	
	public static Test suite() {
		return new TestSuite(ScaleMatrixTests.class);
	}
}
