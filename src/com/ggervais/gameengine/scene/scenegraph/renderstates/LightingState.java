package com.ggervais.gameengine.scene.scenegraph.renderstates;

public class LightingState extends GlobalState {

    private boolean enabled;

    public LightingState() {
        this(true);
    }

    public LightingState(boolean enabled) {
        super(GlobalStateType.LIGHTING);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
