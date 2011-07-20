package com.ggervais.gameengine.scene.scenegraph.renderstates;

public class WireframeState extends GlobalState {
    private boolean enabled;

    public WireframeState() {
        this(false);
    }

    public WireframeState(boolean enabled) {
        super(GlobalStateType.WIREFRAME);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
