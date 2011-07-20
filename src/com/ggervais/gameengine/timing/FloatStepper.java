package com.ggervais.gameengine.timing;

import org.apache.log4j.Logger;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: ggervais
 * Date: 12/06/11
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FloatStepper {

    private static final Logger log = Logger.getLogger(FloatStepper.class);
    private static final Random random = new Random();
    private float minValue;
    private float maxValue;
    private float transitionTimeInMs;
    private float currentValue;
    private float targetValue;
    private float step;
    private long startTime;
    private long lastUpdateTime;

    public FloatStepper(float minValue, float maxValue, float transitionTimeInMs) {
        this(minValue, maxValue, 1, transitionTimeInMs);
    }

    public FloatStepper(float minValue, float maxValue, float initialValue, float transitionTimeInMs) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.transitionTimeInMs =  transitionTimeInMs;
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        this.step = 0;
    }

    private void setNewTargetValue() {
        float diff = this.maxValue - this.minValue;
        float proportion = this.random.nextFloat(); // This is between 0 and 1.

        float newTarget = this.minValue + proportion * diff;

        this.step = (newTarget - this.targetValue) / this.transitionTimeInMs;

        this.targetValue = newTarget;
        this.startTime = this.lastUpdateTime;

    }

    public void update(long currentTime) {

        float diff = currentTime - this.lastUpdateTime;

        this.currentValue += (this.step * diff);

        if (this.lastUpdateTime - this.startTime >= this.transitionTimeInMs) {
            setNewTargetValue();
        }

        this.lastUpdateTime = currentTime;
    }

    public void activate(long currentTime) {
        this.startTime = currentTime;
        this.lastUpdateTime = currentTime;
        setNewTargetValue();
    }

    public void deactivate() {

        this.step = 0;
        this.currentValue = 1;
        this.targetValue = 1;
    }

    public float getValue() {
        return this.currentValue;
    }

    public FloatStepper copy() {
        FloatStepper stepper = new FloatStepper(this.minValue, this.maxValue, this.transitionTimeInMs);
        return stepper;
    }

    public static FloatStepper copy(FloatStepper stepper) {
        return stepper.copy();
    }
}
