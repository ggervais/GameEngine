package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

// This class represents a base geometry (a mesh).
public class MeshGeometry extends Geometry {

    @Override
    protected void generateTextureCoords(Effect effect) {
        // Do nothing, as we don't know the actual geometry.
    }
}
