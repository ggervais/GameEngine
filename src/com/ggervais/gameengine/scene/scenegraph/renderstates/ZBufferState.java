package com.ggervais.gameengine.scene.scenegraph.renderstates;

public class ZBufferState extends GlobalState {

    private boolean enabled;

    public ZBufferState() {
        this(true);
    }

    public ZBufferState(boolean enabled) {
        super(GlobalStateType.ZBUFFER);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
