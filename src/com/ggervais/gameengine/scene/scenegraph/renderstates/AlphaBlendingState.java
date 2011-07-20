package com.ggervais.gameengine.scene.scenegraph.renderstates;

public class AlphaBlendingState extends GlobalState {

    private boolean enabled;

    public AlphaBlendingState() {
        this(true);
    }

    public AlphaBlendingState(boolean enabled) {
        super(GlobalStateType.ALPHA_BLENDING);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
