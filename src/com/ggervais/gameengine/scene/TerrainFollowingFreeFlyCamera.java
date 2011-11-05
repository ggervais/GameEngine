package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import org.apache.log4j.Logger;

public class TerrainFollowingFreeFlyCamera extends FreeFlyCamera {

    private static final Logger log = Logger.getLogger(TerrainFollowingFreeFlyCamera.class);

	private Terrain terrain;
	
	public TerrainFollowingFreeFlyCamera(Point3D position, Vector3D direction, Vector3D up, float fieldOfView, float near, float far, Terrain terrain) {
		super(position, direction, up, fieldOfView, near, far);
		this.terrain = terrain;
	}
	
	public TerrainFollowingFreeFlyCamera() {
		super();
	}
	
	public TerrainFollowingFreeFlyCamera(Terrain terrain) {
		super();
		this.terrain = terrain;
	}
	
	@Override
	public void update(InputController inputController, Spatial sceneGraphRoot) {

        super.update(inputController, sceneGraphRoot);

        Point3D currentPosition = this.position.copy();


        Point3D newPosition = this.position.copy();

		if (this.terrain != null) {
			float height = this.terrain.getHeightAtPosition(this.position);
            float totalHeight = height + 2;

            newPosition.y(totalHeight);
			this.position.y(totalHeight);

            Point3D firstXZ = new Point3D(currentPosition.x(), 0, currentPosition.z());
            Point3D secondXZ = new Point3D(newPosition.x(), 0, newPosition.z());
            float diffXZ = Point3D.distance(secondXZ, firstXZ);
            float diffY = newPosition.y() - currentPosition.y();

            float slope = 0;
            if (diffXZ > 0) {
                slope = Math.abs(diffY / diffXZ);
            }

            if (slope > 2) {
                this.position = currentPosition;
            }


		}
	}
	
	public void setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
}
