package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.UninitializedSubsystemException;
import net.java.games.input.Component.Identifier.Key;

import com.ggervais.gameengine.input.InputSubsystem;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import org.apache.log4j.Logger;

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
	
	public FreeFlyCamera(Point3D position, Vector3D direction, Vector3D up) {
		super(position, direction, up);
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
	public void update() {
		
		InputSubsystem inputSubsystem = InputSubsystem.getInstance();

        boolean isForwardKeyDown = false;
        boolean isBackwardKeyDown = false;
        boolean isLeftKeyDown = false;
        boolean isRightKeyDown = false;

        float diffX = 0;
        float diffY = 0;

        try {
            isForwardKeyDown = inputSubsystem.isKeyDown(Key.W);
            isBackwardKeyDown = inputSubsystem.isKeyDown(Key.S);
            isLeftKeyDown = inputSubsystem.isKeyDown(Key.A);
            isRightKeyDown = inputSubsystem.isKeyDown(Key.D);
            diffX = inputSubsystem.getMouseMovementX();
		    diffY = inputSubsystem.getMouseMovementY();
        } catch (UninitializedSubsystemException use) {
            log.fatal("InputSubsystem is not initialized, cannot update camera!");
            System.exit(-1);
        }

		if (isForwardKeyDown) {
			this.position.x(this.position.x() + this.direction.x() * SPEED);
			this.position.y(this.position.y() + this.direction.y() * SPEED);
			this.position.z(this.position.z() + this.direction.z() * SPEED);
		}
		if (isLeftKeyDown) {
			this.position.x(this.position.x() - this.right.x() * SPEED);
			this.position.z(this.position.z() - this.right.z() * SPEED);
		}
		if (isBackwardKeyDown) {
			this.position.x(this.position.x() - this.direction.x() * SPEED);
			this.position.y(this.position.y() - this.direction.y() * SPEED);
			this.position.z(this.position.z() - this.direction.z() * SPEED);
		}
		if (isRightKeyDown) {
			this.position.x(this.position.x() + this.right.x() * SPEED);
			this.position.z(this.position.z() + this.right.z() * SPEED);
		}
		

		
		// Damp the movement.
		this.phi += diffX * 0.005;
		this.theta -= diffY * 0.005;
		
		clampPhi();
		
		this.direction.x((float) Math.cos(this.phi) * (float) Math.cos(this.theta));
		this.direction.y((float) Math.sin(this.theta));
		this.direction.z((float) Math.sin(this.phi) * (float) Math.cos(this.theta));
		this.direction.normalize();
		
		Vector3D cross = Vector3D.crossProduct(this.direction, this.up).normalized();
		this.right.x(cross.x());
		this.right.y(cross.y());
		this.right.z(cross.z());

		//log.warn("Position -> " + this.position + ", Direction -> " + this.direction + ", Right -> " + this.right + ", LookAt -> " + getLookAt());
		
	}

}
