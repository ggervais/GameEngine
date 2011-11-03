package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.UninitializedSubsystemException;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.physics.collision.Collision;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import net.java.games.input.Component.Identifier.Key;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import org.apache.log4j.Logger;

import java.awt.datatransfer.Transferable;
import java.util.*;

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

        boolean newPosition = false;
		if (isForwardKeyDown) {
            setPosition(new Point3D(this.position.x() + this.direction.x() * SPEED, this.position.y() + this.direction.y() * SPEED, this.position.z() + this.direction.z() * SPEED));
	        newPosition = true;
        }
		if (isLeftKeyDown) {
            setPosition(new Point3D(this.position.x() - this.right.x() * SPEED, this.position.y(), this.position.z() - this.right.z() * SPEED));
		    newPosition = true;
        }
		if (isBackwardKeyDown) {
            setPosition(new Point3D(this.position.x() - this.direction.x() * SPEED, this.position.y() - this.direction.y() * SPEED, this.position.z() - this.direction.z() * SPEED));
	        newPosition = true;
        }
		if (isRightKeyDown) {
            setPosition(new Point3D(this.position.x() + this.right.x() * SPEED, this.position.y(), this.position.z() + this.right.z() * SPEED));
		    newPosition = true;
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
        this.right.normalize();


        if (newPosition) {
            Transformation cameraTransformation = new Transformation();
            this.cameraGeometry.setLocalTransformation(cameraTransformation);
            cameraTransformation.setRotation(this.direction.x(), this.direction.y(), this.direction.z());

            Vector3D velocity = Point3D.sub(this.position, oldPosition);

            /*List<Map<String, Float>> sortedAxis = new ArrayList<Map<String, Float>>();
            for (int i = 0; i < 3; i++) {
                sortedAxis.add(new HashMap<String, Float>());
                sortedAxis.get(i).put("index", (float) i);
                sortedAxis.get(i).put("velocity", velocity.get(i));
            }
            Collections.sort(sortedAxis, new Comparator<Map<String, Float>>() {
                public int compare(Map<String, Float> integerFloatMap, Map<String, Float> integerFloatMap1) {
                    Float velocity1 = Math.abs(integerFloatMap.get("velocity"));
                    Float velocity2 = Math.abs(integerFloatMap1.get("velocity"));
                    return velocity2.compareTo(velocity1);
                }
            });
            int[] indices = new int[3];
            for (int i = 0; i < 3; i++) {
                indices[i] = sortedAxis.get(i).get("index").intValue();
            }*/

            int[] indices = new int[3];
            indices[0] = 0;
            indices[1] = 1;
            indices[2] = 2;


            // For each axis.
            boolean collisionOccurred = false;

            Point3D candidatePosition = oldPosition.copy();
            candidatePosition.add(velocity);
            cameraTransformation.setTranslation(Point3D.sub(candidatePosition, Point3D.zero()));
            this.cameraGeometry.updateGeometryState(System.currentTimeMillis(), false);

            List<Collision> collisions = this.cameraGeometry.intersectsWithUnderlyingGeometry(sceneGraphRoot);
            for (Collision collision : collisions) {
                if (collision.getFirst() == this.cameraGeometry) {

                    float minComponent = Float.MAX_VALUE;
                    int minAxis = 0;

                    for (int i = 0; i < 3; i++) {
                        if (Math.abs(collision.getPenetrationVector().get(i)) < Math.abs(minComponent)) {
                            minComponent = collision.getPenetrationVector().get(i);
                            minAxis = i;
                        }
                    }

                    velocity.set(minAxis, velocity.get(minAxis) - collision.getPenetrationVector().get(minAxis));
                    collisionOccurred = true;
                    break;
                }
            }
            setPosition(Point3D.add(oldPosition, velocity));

            cameraTransformation.setTranslation(Point3D.sub(getPosition(), Point3D.zero()));
            this.cameraGeometry.updateGeometryState(System.currentTimeMillis(), false);
        }

		//log.warn("Position -> " + this.position + ", Direction -> " + this.direction + ", Right -> " + this.right + ", LookAt -> " + getLookAt());
		
	}

}
