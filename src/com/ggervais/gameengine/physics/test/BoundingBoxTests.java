package com.ggervais.gameengine.physics.test;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Ray;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.physics.boundingvolumes.AxisAlignedBoundingBox;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BoundingBoxTests extends TestCase {

    public void testRayInsideBox() {
        Ray ray = new Ray(new Point3D(0, 0, 0), new Vector3D(0, 0, 1));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNotNull(intersectionPoint);
        assertEquals(0f, intersectionPoint.x());
        assertEquals(0f, intersectionPoint.y());
        assertEquals(0f, intersectionPoint.z());
    }

    public void testIntersectWithXPositive() {
        Ray ray = new Ray(new Point3D(1, 0, 0), new Vector3D(-1, 0, 0));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNotNull(intersectionPoint);
        assertEquals(0.5f, intersectionPoint.x());
        assertEquals(0f, intersectionPoint.y());
        assertEquals(0f, intersectionPoint.z());
    }

    public void testIntersectWithXNegative() {
        Ray ray = new Ray(new Point3D(-1, 0, 0), new Vector3D(1, 0, 0));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNotNull(intersectionPoint);
        assertEquals(-0.5f, intersectionPoint.x());
        assertEquals(0f, intersectionPoint.y());
        assertEquals(0f, intersectionPoint.z());
    }

    public void testIntersectWithYPositive() {
        Ray ray = new Ray(new Point3D(0, 1, 0), new Vector3D(0, -1, 0));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNotNull(intersectionPoint);
        assertEquals(0f, intersectionPoint.x());
        assertEquals(0.5f, intersectionPoint.y());
        assertEquals(0f, intersectionPoint.z());
    }

    public void testIntersectWithYNegative() {
        Ray ray = new Ray(new Point3D(0, -1, 0), new Vector3D(0, 1, 0));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNotNull(intersectionPoint);
        assertEquals(0f, intersectionPoint.x());
        assertEquals(-0.5f, intersectionPoint.y());
        assertEquals(0f, intersectionPoint.z());
    }

    public void testIntersectWithZPositive() {
        Ray ray = new Ray(new Point3D(0, 0, 1), new Vector3D(0, 0, -1));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNotNull(intersectionPoint);
        assertEquals(0f, intersectionPoint.x());
        assertEquals(0f, intersectionPoint.y());
        assertEquals(0.5f, intersectionPoint.z());
    }

    public void testIntersectWithZNegative() {
        Ray ray = new Ray(new Point3D(0, 0, -1), new Vector3D(0, 0, 1));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNotNull(intersectionPoint);
        assertEquals(0f, intersectionPoint.x());
        assertEquals(0f, intersectionPoint.y());
        assertEquals(-0.5f, intersectionPoint.z());
    }

    public void testAngledIntersection() {
        Ray ray = new Ray(new Point3D(0.1f, 0, -1), new Vector3D(0.1f, 0, 1));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNotNull(intersectionPoint);
        assertEquals(0.15f, intersectionPoint.x());
        assertEquals(0f, intersectionPoint.y());
        assertEquals(-0.5f, intersectionPoint.z());
    }

    public void testGameIntersection() {
        Ray ray = new Ray(new Point3D(-2.4645088f, -5.4985056E-5f, 2.4436433f), new Vector3D(0.7231091f, -0.054973796f, -0.6885428f));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNotNull(intersectionPoint);
    }

    public void testNoIntersection() {
        Ray ray = new Ray(new Point3D(0, 0, -1), new Vector3D(0, 0, -1));
        BoundingBox box = new BoundingBox(new Point3D(-0.5f, -0.5f, -0.5f), new Point3D(0.5f, 0.5f, 0.5f));

        Point3D intersectionPoint = box.intersects(ray);
        assertNull(intersectionPoint);
    }

    public void testBoundingBoxIntersection() {
        BoundingBox firstBox = new BoundingBox(new Point3D(-1, -1, -1), new Point3D(1, 1, 1));
        BoundingBox secondBox = new BoundingBox(new Point3D(0, 0, 0), new Point3D(2, 2, 2));
        BoundingBox thirdBox = new BoundingBox(new Point3D(2.1f, 2.1f, 2.1f), new Point3D(4, 4, 4));

        assertTrue(firstBox.intersects(secondBox) != null);
        assertTrue(secondBox.intersects(firstBox) != null);
        assertTrue(firstBox.intersects(firstBox) != null);
        assertTrue(secondBox.intersects(secondBox) != null);
        assertTrue(firstBox.intersects(thirdBox) == null);
        assertTrue(secondBox.intersects(thirdBox) == null);
    }

    public static Test suite() {
		return new TestSuite(BoundingBoxTests.class);
	}
}
