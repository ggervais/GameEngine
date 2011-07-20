package com.ggervais.gameengine.timing;

public class LifeController extends Controller {
    private boolean isAlive;

    public LifeController(long startTime, long duration) {
        super(startTime, duration);
        isAlive = true;
    }

    @Override
    public void doUpdate(long currentTime) {
        if (currentTime - this.startTime > duration) {
            isAlive = false;
        }
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public boolean isDead() {
        return !this.isAlive;
    }
}
