package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
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

}
