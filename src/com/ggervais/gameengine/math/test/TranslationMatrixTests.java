package com.ggervais.gameengine.math.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.TranslationMatrix;

public class TranslationMatrixTests extends TestCase {
	public void testIdentity() {
		TranslationMatrix matrix = new TranslationMatrix();
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
	
	public void testTranslatePoint() {
		TranslationMatrix matrix = new TranslationMatrix(1, 2, 3);
		Point3D point = new Point3D(6, 10, 13);
		
		Point3D newPoint = matrix.mult(point);
		
		assertTrue(newPoint.x() == 7);
		assertTrue(newPoint.y() == 12);
		assertTrue(newPoint.z() == 16);
	}
	
	public static Test suite() {
		return new TestSuite(TranslationMatrixTests.class);
	}
}
