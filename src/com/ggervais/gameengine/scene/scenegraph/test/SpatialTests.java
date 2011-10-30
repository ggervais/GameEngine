package com.ggervais.gameengine.scene.scenegraph.test;

import com.ggervais.gameengine.geometry.CubeGeometry;
import com.ggervais.gameengine.scene.scenegraph.Node;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SpatialTests extends TestCase {

    public void testIntersection() {

        long currentTime = 0L;

        Spatial cube1 = new CubeGeometry();
        Transformation cube1Transformation = new Transformation();
        cube1Transformation.setTranslation(-2, 0, 0);
        cube1.setLocalTransformation(cube1Transformation);
        cube1.updateBoundState();

        Spatial cube2 = new CubeGeometry();
        Transformation cube2Transformation = new Transformation();
        cube2Transformation.setTranslation(2, 0, 0);
        cube2.setLocalTransformation(cube2Transformation);
        cube2.updateBoundState();

        Spatial cube3 = new CubeGeometry();
        cube3.updateBoundState();

        Node cubes = new Node();
        cubes.addChild(cube1);
        cubes.addChild(cube2);

        cube1.updateGeometryState(currentTime, true);
        cube2.updateGeometryState(currentTime, true);

        cubes.updateBoundState();

        assertFalse(cube3.intersectsWithUnderlyingGeometry(cubes));

    }

    public static Test suite() {
		return new TestSuite(SpatialTests.class);
	}
}
