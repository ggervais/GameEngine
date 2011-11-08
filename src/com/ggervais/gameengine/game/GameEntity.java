package com.ggervais.gameengine.game;

import com.ggervais.gameengine.scene.scenegraph.Spatial;

public class GameEntity {

    private Spatial sceneGraphObject;

    public GameEntity() {
        this(null);
    }

    public GameEntity(Spatial sceneGraphObject) {
        this.sceneGraphObject = sceneGraphObject;
    }

    public Spatial getSceneGraphObject() {
        return sceneGraphObject;
    }

    public void setSceneGraphObject(Spatial sceneGraphObject) {
        this.sceneGraphObject = sceneGraphObject;
    }
}
