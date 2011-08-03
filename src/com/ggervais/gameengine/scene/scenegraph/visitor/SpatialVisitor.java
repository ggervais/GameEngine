package com.ggervais.gameengine.scene.scenegraph.visitor;

import com.ggervais.gameengine.scene.scenegraph.Spatial;

public interface SpatialVisitor {
    public void visit(Spatial spatial);
}
