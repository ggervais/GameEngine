package com.ggervais.gameengine.timing;

import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.scene.scenegraph.Spatial;

import java.util.List;

public class BezierCurveController extends Controller {

    private float currentLength;
    private List<Point3D> bezierCurvePoints;
    private float speed;
    private RotationMatrix initialRotationMatrix;

    public BezierCurveController() {
        this.speed = 0;
    }

    public BezierCurveController(float speed) {
        this();
        this.currentLength = 0;
        this.speed = speed;
    }

    public void setBezierCurve(BezierCurve bezierCurve) {
        this.bezierCurvePoints = bezierCurve.computeCurve();
    }

    @Override
    public void setControlledObject(Spatial controlledObject) {
        super.setControlledObject(controlledObject);
        this.currentLength = 0;
        this.initialRotationMatrix = controlledObject.getLocalTransformation().getRotationMatrix();
    }

    @Override
    public void doUpdate(long currentTime) {

        float diffInMs = currentTime - this.lastUpdateTime;
        this.currentLength += (this.speed * (diffInMs / 1000f));
        float theta = 0;
        float phi = 0;
        Point3D finalPoint = null;

        float distanceToCurrentPoint = 0;
        for (int i = 0; i < this.bezierCurvePoints.size(); i++) {
            Point3D currentPoint = this.bezierCurvePoints.get(i);
            Point3D nextPoint;

            if (i < this.bezierCurvePoints.size() - 1) {
                nextPoint = this.bezierCurvePoints.get(i + 1);

                float distanceBetweenPoints = currentPoint.distance(nextPoint);

                if (this.currentLength >= distanceToCurrentPoint && this.currentLength <= (distanceToCurrentPoint + distanceBetweenPoints)) {
                    float diffLength = (this.currentLength - distanceToCurrentPoint);
                    Vector3D direction = Point3D.sub(nextPoint, currentPoint).normalized();

                    theta = (float) Math.asin(MathUtils.clamp(direction.y(), -1, 1));
                    phi = (float) Math.asin(MathUtils.clamp(direction.z() / ((float) Math.cos(theta)), -1, 1));

                    finalPoint = Point3D.add(currentPoint, direction.multiplied(diffLength));
                    break;
                }

                distanceToCurrentPoint += distanceBetweenPoints;
            }
        }

        if (finalPoint != null) {
            Vector3D objectTranslation = new Vector3D(finalPoint.x(), finalPoint.y(), finalPoint.z());
            RotationMatrix rotationMatrix = RotationMatrix.createFromXYZ(0, -phi, theta);
            RotationMatrix tempMatrix = new RotationMatrix();
            tempMatrix.mult(rotationMatrix);
            tempMatrix.mult(this.initialRotationMatrix);
            this.controlledSpatialObject.getLocalTransformation().setTranslation(objectTranslation);
            this.controlledSpatialObject.getLocalTransformation().setRotationMatrix(tempMatrix);
        }
    }
}
