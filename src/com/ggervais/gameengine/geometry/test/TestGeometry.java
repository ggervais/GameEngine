package com.ggervais.gameengine.geometry.test;

import com.ggervais.gameengine.geometry.primitives.IndexBuffer;
import com.ggervais.gameengine.geometry.primitives.VertexBuffer;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

/**
 * Created by IntelliJ IDEA.
 * User: ggervais
 * Date: 26/09/11
 * Time: 10:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestGeometry extends Geometry {

    public void setBuffers(VertexBuffer vertexBuffer, IndexBuffer indexBuffer) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
    }

    @Override
    protected void generateTextureCoords(Effect effect) {

    }
}
