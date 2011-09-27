package com.ggervais.gameengine.geometry.test;

import com.ggervais.gameengine.geometry.primitives.IndexBuffer;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.geometry.primitives.VertexBuffer;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.*;

public class GeometryTests extends TestCase {

    public void testHorizontalFaceReturnsVerticalNormals() {

        TestGeometry geometry = new TestGeometry();

        Vertex v1 = new Vertex(new Point3D(0, 0, 0), Color.WHITE, 0, 0);
        Vertex v2 = new Vertex(new Point3D(1, 0, 0), Color.WHITE, 0, 0);
        Vertex v3 = new Vertex(new Point3D(0.5f, 0, -1), Color.WHITE, 0, 0);

        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.addVertex(v1);
        vertexBuffer.addVertex(v2);
        vertexBuffer.addVertex(v3);

        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.addIndex(0);
        indexBuffer.addIndex(1);
        indexBuffer.addIndex(2);

        geometry.setBuffers(vertexBuffer, indexBuffer);
        geometry.setGeometryDirty(true);
        geometry.updateWorldBound();

        int count = 0;
        for (int i = 0; i < vertexBuffer.size(); i++) {
            Vector3D normal = geometry.getNormal(i);
            assertTrue(normal.x() == 0);
            assertTrue(normal.y() == 1);
            assertTrue(normal.z() == 0);
            count++;
        }
        assertTrue(count == 3);
    }

    public void testVerticalFaceReturnsHorizontalNormals() {

        TestGeometry geometry = new TestGeometry();

        Vertex v1 = new Vertex(new Point3D(0, 0, 0), Color.WHITE, 0, 0);
        Vertex v2 = new Vertex(new Point3D(0, 1, 0), Color.WHITE, 0, 0);
        Vertex v3 = new Vertex(new Point3D(0, 0.5f, 1), Color.WHITE, 0, 0);

        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.addVertex(v1);
        vertexBuffer.addVertex(v2);
        vertexBuffer.addVertex(v3);

        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.addIndex(0);
        indexBuffer.addIndex(1);
        indexBuffer.addIndex(2);

        geometry.setBuffers(vertexBuffer, indexBuffer);
        geometry.setGeometryDirty(true);
        geometry.updateWorldBound();

        int count = 0;
        for (int i = 0; i < vertexBuffer.size(); i++) {
            Vector3D normal = geometry.getNormal(i);
            assertTrue(normal.x() == 1);
            assertTrue(normal.y() == 0);
            assertTrue(normal.z() == 0);
            count++;
        }
        assertTrue(count == 3);
    }

    public void testDiagonalFaceReturnsDiagonalNormals() {

        TestGeometry geometry = new TestGeometry();

        Vertex v1 = new Vertex(new Point3D(0, 0, 0), Color.WHITE, 0, 0);
        Vertex v2 = new Vertex(new Point3D(1, 1, 1), Color.WHITE, 0, 0);
        Vertex v3 = new Vertex(new Point3D(1, 1, -1), Color.WHITE, 0, 0);

        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.addVertex(v1);
        vertexBuffer.addVertex(v2);
        vertexBuffer.addVertex(v3);

        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.addIndex(0);
        indexBuffer.addIndex(1);
        indexBuffer.addIndex(2);

        geometry.setBuffers(vertexBuffer, indexBuffer);
        geometry.setGeometryDirty(true);
        geometry.updateWorldBound();

        int count = 0;
        for (int i = 0; i < vertexBuffer.size(); i++) {
            Vector3D normal = geometry.getNormal(i);
            assertTrue(normal.x() == -0.7071067690849304f);
            assertTrue(normal.y() == 0.7071067690849304f);
            assertTrue(normal.z() == 0);
            count++;
        }
        assertTrue(count == 3);
    }

    public void testQuadNormals() {
         TestGeometry geometry = new TestGeometry();

        Vertex v1 = new Vertex(new Point3D(0, 0, -1), Color.WHITE, 0, 0);
        Vertex v2 = new Vertex(new Point3D(0, 0, 0), Color.WHITE, 0, 0);
        Vertex v3 = new Vertex(new Point3D(1, 0, -1), Color.WHITE, 0, 0);
        Vertex v4 = new Vertex(new Point3D(1, 0, 0), Color.WHITE, 0, 0);

        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.addVertex(v1);
        vertexBuffer.addVertex(v2);
        vertexBuffer.addVertex(v3);
        vertexBuffer.addVertex(v4);

        IndexBuffer indexBuffer = new IndexBuffer();
        indexBuffer.addIndex(0);
        indexBuffer.addIndex(1);
        indexBuffer.addIndex(3);

        indexBuffer.addIndex(0);
        indexBuffer.addIndex(3);
        indexBuffer.addIndex(2);

        geometry.setBuffers(vertexBuffer, indexBuffer);
        geometry.setGeometryDirty(true);
        geometry.updateWorldBound();

        Vector3D n1 = geometry.getNormal(0);
        Vector3D n2 = geometry.getNormal(1);
        Vector3D n3 = geometry.getNormal(2);
        Vector3D n4 = geometry.getNormal(3);

        int count = 0;
        for (int i = 0; i < vertexBuffer.size(); i++) {
            Vector3D normal = geometry.getNormal(i);
            assertTrue(normal.x() == 0);
            assertTrue(normal.y() == 1);
            assertTrue(normal.z() == 0);
            count++;
        }
        assertTrue(count == 4);
    }

	public static Test suite() {
		return new TestSuite(GeometryTests.class);
	}
}
