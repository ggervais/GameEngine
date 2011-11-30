package com.ggervais.gameengine.math.test;

import com.ggervais.gameengine.math.*;

import com.ggervais.gameengine.scene.scenegraph.Transformation;
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

    public void testExtractRotation() {

        RotationMatrix baseRotationMatrix = RotationMatrix.createFromXYZ((float) Math.toRadians(60), (float) Math.toRadians(70), (float) Math.toRadians(80));
        Point3D originalPoint = new Point3D(1, 2, 3);

        Point3D firstTransformedPoint = baseRotationMatrix.mult(originalPoint);

        Transformation transformation = new Transformation();
        transformation.setScale(10, 20, 30);
        transformation.setRotation((float) Math.toRadians(60), (float) Math.toRadians(70), (float) Math.toRadians(80));
        transformation.setTranslation(45, 46, 47);
        Matrix4x4 matrix = transformation.getMatrix();

        RotationMatrix extractedRotationMatrix = matrix.extractRotationMatrix();

        Point3D secondTransformedPoint = extractedRotationMatrix.mult(originalPoint);

        for (int i = 0; i < 3; i++) {
            firstTransformedPoint.set(i, (float) Math.round(firstTransformedPoint.get(i) * 1000f) / 1000f);
            secondTransformedPoint.set(i, (float) Math.round(secondTransformedPoint.get(i) * 1000f) / 1000f);
        }

        for (int i = 0; i < 3; i++) {
            assertEquals(firstTransformedPoint.get(i), secondTransformedPoint.get(i));
        }
    }
	
    public void testExtractScale() {
        Transformation transformation = new Transformation();
        transformation.setScale(10, 20, 30);
        transformation.setRotation((float) Math.toRadians(60), (float) Math.toRadians(70), (float) Math.toRadians(80));
        Matrix4x4 matrix = transformation.getMatrix();

        ScaleMatrix extractedScaleMatrix = matrix.extractScaleMatrix();
        float sx = extractedScaleMatrix.getElement(1, 1);
        float sy = extractedScaleMatrix.getElement(2, 2);
        float sz = extractedScaleMatrix.getElement(3, 3);

        assertEquals(10, Math.round(sx));
        assertEquals(20, Math.round(sy));
        assertEquals(30, Math.round(sz));
    }

    public void testExtractTranslation() {
        Transformation transformation = new Transformation();
        transformation.setScale(10, 20, 30);
        transformation.setRotation((float) Math.toRadians(60), (float) Math.toRadians(70), (float) Math.toRadians(80));
        transformation.setTranslation(45, 46, 47);
        Matrix4x4 matrix = transformation.getMatrix();

        TranslationMatrix extractedRotationMatrix = matrix.extractTranslationMatrix();
        float tx = extractedRotationMatrix.getElement(1, 4);
        float ty = extractedRotationMatrix.getElement(2, 4);
        float tz = extractedRotationMatrix.getElement(3, 4);

        assertEquals(45, Math.round(tx));
        assertEquals(46, Math.round(ty));
        assertEquals(47, Math.round(tz));
    }

	public static Test suite() {
		return new TestSuite(Matrix4x4Tests.class);
	}
}
