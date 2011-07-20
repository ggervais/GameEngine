package com.ggervais.gameengine.scene.scenegraph.renderstates;

public class GlobalState {

    private GlobalStateType type;

    public GlobalStateType getType() {
        return this.type;
    }

    public void setType(GlobalStateType type) {
        this.type = type;
    }

    public GlobalState(GlobalStateType type) {
        this.type = type;
    }
}
