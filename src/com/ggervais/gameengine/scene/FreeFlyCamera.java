package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.UninitializedSubsystemException;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import net.java.games.input.Component.Identifier.Key;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import org.apache.log4j.Logger;

import java.awt.datatransfer.Transferable;

public class FreeFlyCamera extends Camera {

    private static final Logger log = Logger.getLogger(FreeFlyCamera.class);

	private static final float SPEED = 0.1f;
	private static final float ONE_RADIAN = 0.01745f;
	private static final float MIN_THETA = (float) (-Math.PI / 2 + ONE_RADIAN);
	private static final float MAX_THETA = (float) (Math.PI / 2 - ONE_RADIAN);
	
	private float theta;
	private float phi;
	
	Vector3D up;
	Vector3D right;
	
	public FreeFlyCamera(Point3D position, Vector3D direction, Vector3D up, float fieldOfView, float near, float far) {
		super(position, direction, up, fieldOfView, near, far);
		this.phi = (float) Math.toRadians(-90);
		this.theta = 0;
		this.up = new Vector3D(0, 1, 0);
		this.right = new Vector3D(1, 0, 0);
	}
	
	public FreeFlyCamera() {
		super();
		this.phi = (float) Math.toRadians(-90);
		this.theta = 0;
		this.up = new Vector3D(0, 1, 0);
		this.right = new Vector3D(1, 0, 0);
	}
	
	private void clampPhi() {
		if (this.theta < MIN_THETA) {
			this.theta = MIN_THETA;
		}
		
		if (this.theta > MAX_THETA) {
			this.theta = MAX_THETA;
		}
	}
	
	@Override
	public void update(InputController inputController, Spatial sceneGraphRoot) {
		
		boolean isForwardKeyDown = false;
        boolean isBackwardKeyDown = false;
        boolean isLeftKeyDown = false;
        boolean isRightKeyDown = false;

        float diffX = 0;
        float diffY = 0;

        isForwardKeyDown = inputController.isKeyDown(Key.W);
        isBackwardKeyDown = inputController.isKeyDown(Key.S);
        isLeftKeyDown = inputController.isKeyDown(Key.A);
        isRightKeyDown = inputController.isKeyDown(Key.D);
        diffX = inputController.getMouseMovementX();
        diffY = inputController.getMouseMovementY();

        Point3D oldPosition = getPosition().copy();

		if (isForwardKeyDown) {
            setPosition(new Point3D(this.position.x() + this.direction.x() * SPEED, this.position.y() + this.direction.y() * SPEED, this.position.z() + this.direction.z() * SPEED));
	    }
		if (isLeftKeyDown) {
            setPosition(new Point3D(this.position.x() - this.right.x() * SPEED, this.position.y(), this.position.z() - this.right.z() * SPEED));
		}
		if (isBackwardKeyDown) {
            setPosition(new Point3D(this.position.x() - this.direction.x() * SPEED, this.position.y() - this.direction.y() * SPEED, this.position.z() - this.direction.z() * SPEED));
	    }
		if (isRightKeyDown) {
            setPosition(new Point3D(this.position.x() + this.right.x() * SPEED, this.position.y(), this.position.z() + this.right.z() * SPEED));
		}

		// Damp the movement.
		this.phi += diffX * 0.005;
		this.theta -= diffY * 0.005;
		
		clampPhi();

        setDirection(new Vector3D((float) Math.cos(this.phi) * (float) Math.cos(this.theta), (float) Math.sin(this.theta), (float) Math.sin(this.phi) * (float) Math.cos(this.theta)).normalized());

		Vector3D cross = Vector3D.crossProduct(this.direction, this.up).normalized();
        this.right.x(cross.x());
		this.right.y(cross.y());
		this.right.z(cross.z());


        Transformation cameraTransformation = new Transformation();
        cameraTransformation.setTranslation(Point3D.sub(getPosition(), Point3D.zero()));
        cameraTransformation.setRotation(this.direction.x(), this.direction.y(), this.direction.z());

        this.cameraGeometry.setLocalTransformation(cameraTransformation);
        this.cameraGeometry.updateGeometryState(System.currentTimeMillis(), false);

        if (this.cameraGeometry.intersectsWithUnderlyingGeometry(sceneGraphRoot)) {
            setPosition(oldPosition);
        }
		//log.warn("Position -> " + this.position + ", Direction -> " + this.direction + ", Right -> " + this.right + ", LookAt -> " + getLookAt());
		
	}

}
