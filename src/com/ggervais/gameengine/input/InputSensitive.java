package com.ggervais.gameengine.input;

import com.ggervais.gameengine.scene.scenegraph.Spatial;

public interface InputSensitive {
    public void update(InputController inputState, Spatial sceneGraphRoot);
}
