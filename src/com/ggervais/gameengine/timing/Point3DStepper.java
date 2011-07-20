package com.ggervais.gameengine.timing;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import org.apache.log4j.Logger;

import java.util.Random;

public class Point3DStepper {
    private static final Logger log = Logger.getLogger(Point3DStepper.class);

    private static final Random random = new Random();
    private Point3D minValue;
    private Point3D maxValue;
    private Point3D currentValue;
    private Point3D targetValue;
    private Vector3D step;

    private float transitionTimeInMs;

    private long startTime;
    private long lastUpdateTime;

    public Point3DStepper(Point3D minValue, Point3D maxValue, float transitionTimeInMs) {
        this(minValue, maxValue, Point3D.zero(), transitionTimeInMs);
    }

    public Point3DStepper(Point3D minValue, Point3D maxValue, Point3D initialValue, float transitionTimeInMs) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.transitionTimeInMs =  transitionTimeInMs;
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        this.step = Vector3D.zero();
    }

    private void setNewTargetValue() {
        Vector3D diff = this.maxValue.sub(this.minValue);
        float proportion = this.random.nextFloat(); // This is between 0 and 1.

        Point3D newTarget = Point3D.add(this.minValue, Vector3D.multiply(diff, proportion));

        this.step = Point3D.sub(newTarget, this.targetValue).multiplied(1 / this.transitionTimeInMs);

        this.targetValue = newTarget;
        this.startTime = this.lastUpdateTime;

    }

    public void update(long currentTime) {

        float diff = currentTime - this.lastUpdateTime;

        this.currentValue.add(Vector3D.multiply(this.step, diff));

        if (this.lastUpdateTime - this.startTime >= this.transitionTimeInMs) {
            setNewTargetValue();
        }

        this.lastUpdateTime = currentTime;
    }

    public void activate(long currentTime) {
        this.startTime = currentTime;
        this.lastUpdateTime = currentTime;
        setNewTargetValue();
        this.currentValue = this.targetValue;
    }

    public void deactivate() {

        this.step = Vector3D.zero();
        this.currentValue = Point3D.zero();
        this.targetValue = Point3D.zero();
    }

    public Point3D getValue() {
        return this.currentValue;
    }

    public Point3DStepper copy() {
        Point3DStepper stepper = new Point3DStepper(this.minValue, this.maxValue, this.transitionTimeInMs);
        return stepper;
    }

    public static Point3DStepper copy(Point3DStepper stepper) {
        return stepper.copy();
    }
}
