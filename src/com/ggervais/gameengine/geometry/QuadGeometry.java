package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;

import java.awt.*;

public class QuadGeometry extends Geometry {

    private float aspectRatio;

    public QuadGeometry(float aspectRatio) {
        super(4);
        this.aspectRatio = aspectRatio;
        create();
    }

    public QuadGeometry() {
        this(1.0f);
    }

    public void create() {

        float height = 1.0f;
        float width = aspectRatio * height;

        Vertex vertex1 = new Vertex(new Point3D(-0.5f, 0.5f, 0), Color.WHITE, 0, 0);
        Vertex vertex2 = new Vertex(new Point3D(-0.5f, -0.5f, 0), Color.WHITE, 0, 0);
        Vertex vertex3 = new Vertex(new Point3D(-0.5f + width, -0.5f, 0), Color.WHITE, 0, 0);
        Vertex vertex4 = new Vertex(new Point3D(-0.5f + width, 0.5f, 0), Color.WHITE, 0, 0);

        TextureCoords texture00 = new TextureCoords(0, 0);
		TextureCoords texture10 = new TextureCoords(1, 0);
		TextureCoords texture01 = new TextureCoords(0, 1);
		TextureCoords texture11 = new TextureCoords(1, 1);

        this.vertexBuffer.addVertex(vertex1);
        this.textureBuffer.addCoords(texture00);

        this.vertexBuffer.addVertex(vertex2);
        this.textureBuffer.addCoords(texture01);

        this.vertexBuffer.addVertex(vertex3);
        this.textureBuffer.addCoords(texture11);

        this.vertexBuffer.addVertex(vertex4);
        this.textureBuffer.addCoords(texture10);

        this.indexBuffer.addIndex(4, 0);
        this.indexBuffer.addIndex(4, 1);
        this.indexBuffer.addIndex(4, 2);
        this.indexBuffer.addIndex(4, 3);

        Face face = new Face();
        face.addVertex(vertex1);
        face.addTextureCoords(texture00);

        face.addVertex(vertex2);
        face.addTextureCoords(texture01);

        face.addVertex(vertex3);
        face.addTextureCoords(texture11);

        face.addVertex(vertex4);
        face.addTextureCoords(texture10);

        addFace(face);
    }

    @Override
    protected void generateTextureCoords(Effect effect) {
         for (int i = 0; i < effect.nbTextures(); i++) {
             effect.clearTextureCoordinates(i, 4);

             effect.addTextureCoordinates(i, 4, new TextureCoords(0, 0));
             effect.addTextureCoordinates(i, 4, new TextureCoords(0, 1));
             effect.addTextureCoordinates(i, 4, new TextureCoords(1, 1));
             effect.addTextureCoordinates(i, 4, new TextureCoords(1, 0));
        }
    }

}
