package com.ggervais.gameengine.math.test;

import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class Matrix4x4Tests extends TestCase {
	public void testIdentity() {
		Matrix4x4 matrix = new Matrix4x4();
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
	
	public void testSetterAndGetter() {
		Matrix4x4 matrix = new Matrix4x4();
		matrix.setElement(1, 1, 42);
		assertTrue(matrix.getElement(1, 1) == 42);
	}
	
	public void testMultiplyStatic() {
		Matrix4x4 m1 = new Matrix4x4();
		Matrix4x4 m2 = new Matrix4x4();
		
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				m1.setElement(i, j, i + j);
			}
		}
		
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				m2.setElement(i, j, 2 * (i + j));
			}
		}
		
		Matrix4x4 m3 = Matrix4x4.mult(m1, m2);
		assertTrue(m3.getElement(1, 1) == 108);
		assertTrue(m3.getElement(1, 2) == 136);
		assertTrue(m3.getElement(1, 3) == 164);
		assertTrue(m3.getElement(1, 4) == 192);
		
		assertTrue(m3.getElement(2, 1) == 136);
		assertTrue(m3.getElement(2, 2) == 172);
		assertTrue(m3.getElement(2, 3) == 208);
		assertTrue(m3.getElement(2, 4) == 244);
		
		assertTrue(m3.getElement(3, 1) == 164);
		assertTrue(m3.getElement(3, 2) == 208);
		assertTrue(m3.getElement(3, 3) == 252);
		assertTrue(m3.getElement(3, 4) == 296);
		
		assertTrue(m3.getElement(4, 1) == 192);
		assertTrue(m3.getElement(4, 2) == 244);
		assertTrue(m3.getElement(4, 3) == 296);
		assertTrue(m3.getElement(4, 4) == 348);
	}
	
	public void testMultiplyInInstance() {
		Matrix4x4 m1 = new Matrix4x4();
		Matrix4x4 m2 = new Matrix4x4();
		
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				m1.setElement(i, j, i + j);
			}
		}
		
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				m2.setElement(i, j, 2 * (i + j));
			}
		}
		
		m1.mult(m2);
		assertTrue(m1.getElement(1, 1) == 108);
		assertTrue(m1.getElement(1, 2) == 136);
		assertTrue(m1.getElement(1, 3) == 164);
		assertTrue(m1.getElement(1, 4) == 192);
		
		assertTrue(m1.getElement(2, 1) == 136);
		assertTrue(m1.getElement(2, 2) == 172);
		assertTrue(m1.getElement(2, 3) == 208);
		assertTrue(m1.getElement(2, 4) == 244);
		
		assertTrue(m1.getElement(3, 1) == 164);
		assertTrue(m1.getElement(3, 2) == 208);
		assertTrue(m1.getElement(3, 3) == 252);
		assertTrue(m1.getElement(3, 4) == 296);
		
		assertTrue(m1.getElement(4, 1) == 192);
		assertTrue(m1.getElement(4, 2) == 244);
		assertTrue(m1.getElement(4, 3) == 296);
		assertTrue(m1.getElement(4, 4) == 348);
	}
	
	public void testMultiplyPoint() {
		Matrix4x4 matrix = new Matrix4x4();
		matrix.setElement(1, 1, 2);
		matrix.setElement(2, 2, 2);
		matrix.setElement(3, 3, 2);
		matrix.setElement(4, 4, 2);
		
		Point3D point = matrix.mult(new Point3D(1, 2, 3));
		assertTrue(point.x() == 2);
		assertTrue(point.y() == 4);
		assertTrue(point.z() == 6);
	}
	
	public void testMultiplyScalarStatic() {
		Matrix4x4 matrix = new Matrix4x4();
		matrix.mult(2);
		assertTrue(matrix.getElement(1, 1) == 2);
		assertTrue(matrix.getElement(1, 2) == 0);
		assertTrue(matrix.getElement(1, 3) == 0);
		assertTrue(matrix.getElement(1, 4) == 0);
		
		assertTrue(matrix.getElement(2, 1) == 0);
		assertTrue(matrix.getElement(2, 2) == 2);
		assertTrue(matrix.getElement(2, 3) == 0);
		assertTrue(matrix.getElement(2, 4) == 0);
		
		assertTrue(matrix.getElement(3, 1) == 0);
		assertTrue(matrix.getElement(3, 2) == 0);
		assertTrue(matrix.getElement(3, 3) == 2);
		assertTrue(matrix.getElement(3, 4) == 0);
		
		assertTrue(matrix.getElement(4, 1) == 0);
		assertTrue(matrix.getElement(4, 2) == 0);
		assertTrue(matrix.getElement(4, 3) == 0);
		assertTrue(matrix.getElement(4, 4) == 2);
	}
	
	public void testMultiplyScalarInInstance() {
		Matrix4x4 matrix = new Matrix4x4();
		Matrix4x4 result = Matrix4x4.mult(matrix, 2);
		
		assertTrue(result.getElement(1, 1) == 2);
		assertTrue(result.getElement(1, 2) == 0);
		assertTrue(result.getElement(1, 3) == 0);
		assertTrue(result.getElement(1, 4) == 0);
		
		assertTrue(result.getElement(2, 1) == 0);
		assertTrue(result.getElement(2, 2) == 2);
		assertTrue(result.getElement(2, 3) == 0);
		assertTrue(result.getElement(2, 4) == 0);
		
		assertTrue(result.getElement(3, 1) == 0);
		assertTrue(result.getElement(3, 2) == 0);
		assertTrue(result.getElement(3, 3) == 2);
		assertTrue(result.getElement(3, 4) == 0);
		
		assertTrue(result.getElement(4, 1) == 0);
		assertTrue(result.getElement(4, 2) == 0);
		assertTrue(result.getElement(4, 3) == 0);
		assertTrue(result.getElement(4, 4) == 2);
	}
	
	public static Test suite() {
		return new TestSuite(Matrix4x4Tests.class);
	}
}
