package com.ggervais.gameengine.physics;

import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.input.InputSensitive;
import com.ggervais.gameengine.math.MathUtils;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.physics.collision.Collision;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.scene.scenegraph.Transformation;
import net.java.games.input.Component;
import org.apache.log4j.Logger;

import java.util.List;

public class InputControlledController extends MotionController {

    private static final Logger log = Logger.getLogger(InputControlledController.class);
    private InputController previousInputController;
    private static final float SPEED = 0.1f;
    private static final float ONE_RADIAN = 0.01745f;
	private static final float MIN_THETA = (float) (-Math.PI / 4 + ONE_RADIAN);
	private static final float MAX_THETA = (float) (Math.PI / 4 - ONE_RADIAN);

    private float theta;
    private float phi;

    public InputControlledController() {
        super(null, null);
        this.theta = 0;
        this.phi = 0;
    }

    @Override
    public void doUpdate(long currentTime, InputController inputController) {
        if (this.previousInputController == null) {
            this.previousInputController = inputController;
        }

        boolean isForwardKeyDown = false;
        boolean isBackwardKeyDown = false;
        boolean isLeftKeyDown = false;
        boolean isRightKeyDown = false;

        float diffX = 0;
        float diffY = 0;

        isForwardKeyDown = inputController.isKeyDown(Component.Identifier.Key.W);
        isBackwardKeyDown = inputController.isKeyDown(Component.Identifier.Key.S);
        isLeftKeyDown = inputController.isKeyDown(Component.Identifier.Key.A);
        isRightKeyDown = inputController.isKeyDown(Component.Identifier.Key.D);
        diffX = inputController.getMouseMovementX();
        diffY = inputController.getMouseMovementY();

        Vector3D oldTranslation = null;
        Vector3D forwardVector = new Vector3D(0, 0, -1);
        Vector3D rightVector = new Vector3D(1, 0, 0);
        Transformation transformation = this.controlledSpatialObject.getLocalTransformation();
        if (transformation != null) {
            oldTranslation = transformation.getTranslation();
            forwardVector = transformation.getRotationMatrix().mult(forwardVector);
            rightVector = transformation.getRotationMatrix().mult(rightVector);
        }

        Vector3D candidateTranslation = new Vector3D();

        Vector3D velocity = new Vector3D();

        boolean newPosition = false;
		if (isForwardKeyDown) {
            velocity.add(Vector3D.multiply(forwardVector, SPEED));
            newPosition = true;
        }
		if (isLeftKeyDown) {
            velocity.add(Vector3D.multiply(rightVector, -SPEED));
            newPosition = true;
        }
		if (isBackwardKeyDown) {
            velocity.add(Vector3D.multiply(forwardVector, -SPEED));
            newPosition = true;
        }
		if (isRightKeyDown) {
            velocity.add(Vector3D.multiply(rightVector, SPEED));
            newPosition = true;
        }

        if (oldTranslation != null) {
            candidateTranslation.add(oldTranslation);
            candidateTranslation.add(velocity);
            transformation.setTranslation(candidateTranslation);
        }


		// Damp the movement.

        float displacementPhi = diffX * 0.005f;
        float displacementTheta = diffY * 0.005f;

		this.phi += displacementPhi;
		this.theta += displacementTheta;

		//this.phi = MathUtils.clamp(this.phi, MIN_THETA, MAX_THETA);
		this.theta = MathUtils.clamp(this.theta, MIN_THETA, MAX_THETA);

        if (transformation != null) {
            RotationMatrix diffMatrix = RotationMatrix.createFromXYZ(-this.theta, -this.phi, 0);
            RotationMatrix tempMatrix = new RotationMatrix();
            tempMatrix.mult(diffMatrix);
            tempMatrix.mult(transformation.getRotationMatrix());
            transformation.setRotationMatrix(diffMatrix);
        }
    }
}
