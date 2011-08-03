package com.ggervais.gameengine.timing;

import com.ggervais.gameengine.scene.scenegraph.Spatial;

public abstract class Controller {

    protected long startTime;
    protected long lastUpdateTime;
    protected long duration;
    protected Spatial controlledSpatialObject;
    protected boolean paused;
    protected long pauseOffset;

    public Controller() {
        this.startTime = 0;
        this.duration = 0;
        this.lastUpdateTime = 0;
        this.paused = false;
        this.pauseOffset = 0;
    }

    public Controller(long startTime, long duration) {
        this(null, startTime, duration);
        this.lastUpdateTime = 0;
    }

    public Controller(Spatial controlledSpatialObject, long startTime, long duration) {
        this.controlledSpatialObject = controlledSpatialObject;
        this.startTime = startTime;
        this.duration = duration;
        this.lastUpdateTime = 0;
        this.paused = false;
        this.pauseOffset = 0;
    }

    public void setControlledObject(Spatial object) {
        this.controlledSpatialObject = object;
    }

    public void update(long currentTime) {
        if (this.paused) {
            return;
        }

        if (this.lastUpdateTime == 0) {
            this.lastUpdateTime = currentTime;
        }
        if (this.startTime == 0) {
            this.startTime = currentTime;
        }
        doUpdate(currentTime);
        this.lastUpdateTime = currentTime;
    }

    public Spatial getControlledObject() {
        return this.controlledSpatialObject;
    }

    public abstract void doUpdate(long currentTime);

    public void pause(long currentTime) {
        this.paused = true;
    }

    public void unpause(long currentTime) {
        this.paused = false;
        this.pauseOffset += currentTime - this.lastUpdateTime;
    }

    public boolean isPaused() {
        return this.paused;
    }

}
