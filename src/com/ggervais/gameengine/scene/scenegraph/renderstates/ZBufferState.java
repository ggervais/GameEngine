package com.ggervais.gameengine.scene.scenegraph.renderstates;

public class ZBufferState extends GlobalState {

    private boolean isDepthTestEnabled;
    private boolean isZBufferWritingEnabled;

    public ZBufferState() {
        this(true, true);
    }

    public ZBufferState(boolean isDepthTestEnabled, boolean isZBufferWritingEnabled) {
        super(GlobalStateType.ZBUFFER);
        this.isDepthTestEnabled = isDepthTestEnabled;
        this.isZBufferWritingEnabled = isZBufferWritingEnabled;
    }

    public boolean isDepthTestEnabled() {
        return this.isDepthTestEnabled;
    }

    public boolean isZBufferWritingEnabled() {
        return this.isZBufferWritingEnabled;
    }

    public void setDepthTestEnabled(boolean enabled) {
        this.isDepthTestEnabled = enabled;
    }

    public void setZBufferWritingEnabled(boolean enabled) {
        this.isZBufferWritingEnabled = enabled;
    }
}
