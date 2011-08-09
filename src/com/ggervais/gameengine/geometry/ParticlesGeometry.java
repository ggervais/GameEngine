package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.Camera;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticlesGeometry extends Geometry {

    private static final Logger log = Logger.getLogger(ParticlesGeometry.class);
    private List<Point3D> positions;
    private List<Float> sizes;
    private static Random random = new Random();

    public ParticlesGeometry(int nbParticles, float radius) {
        super(3);
        this.positions = new ArrayList<Point3D>();
        this.sizes = new ArrayList<Float>();
        generateStartPositions(nbParticles, radius);
        generateEmptyVertices();
    }

    @Override
    public void draw(SceneRenderer renderer) {
        Camera camera = renderer.getScene().getCamera();
        generateParticles(camera);
        super.draw(renderer);
    }

    private void generateEmptyVertices() {
        for (int i = 0; i < positions.size(); i++) {
            Face face1 = new Face();
            face1.addVertex(new Vertex());
            face1.addTextureCoords(new TextureCoords());
            face1.addVertex(new Vertex());
            face1.addTextureCoords(new TextureCoords());
            face1.addVertex(new Vertex());
            face1.addTextureCoords(new TextureCoords());

            Face face2 = new Face();
            face2.addVertex(new Vertex());
            face2.addTextureCoords(new TextureCoords());
            face2.addVertex(new Vertex());
            face2.addTextureCoords(new TextureCoords());
            face2.addVertex(new Vertex());
            face2.addTextureCoords(new TextureCoords());

            this.faces.add(face1);
            this.faces.add(face2);
        }
    }

    private void generateParticles(Camera camera) {

        TextureCoords texture00 = new TextureCoords(0, 0);
		TextureCoords texture10 = new TextureCoords(1, 0);
		TextureCoords texture01 = new TextureCoords(0, 1);
		TextureCoords texture11 = new TextureCoords(1, 1);

        int faceCounter = 0;
        for (int i = 0; i < positions.size(); i++) {

            Point3D position = positions.get(i);
            float size = this.sizes.get(i);

            Face face1 = this.faces.get(faceCounter);
            Face face2 = this.faces.get(faceCounter + 1);

            Point3D p1 = new Point3D((-0.5f + position.x()) * size, (0.5f + position.y()) * size, position.z() * size);
            Point3D p2 = new Point3D((-0.5f + position.x()) * size, (-0.5f + position.y()) * size , position.z() * size);
            Point3D p3 = new Point3D((0.5f + position.x()) * size, (-0.5f + position.y()) * size, position.z() * size);
            Point3D p4 = new Point3D((0.5f + position.x()) * size, (0.5f + position.y() * size), position.z() * size);

            face1.getVertex(0).setPosition(p1);
            face1.getTextureCoords(0).setTu(texture00.getTextureU());
            face1.getTextureCoords(0).setTv(texture00.getTextureV());

            face1.getVertex(1).setPosition(p2);
            face1.getTextureCoords(1).setTu(texture01.getTextureU());
            face1.getTextureCoords(1).setTv(texture01.getTextureV());

            face1.getVertex(2).setPosition(p4);
            face1.getTextureCoords(2).setTu(texture10.getTextureU());
            face1.getTextureCoords(2).setTv(texture10.getTextureV());


            face2.getVertex(0).setPosition(p4);
            face2.getTextureCoords(0).setTu(texture10.getTextureU());
            face2.getTextureCoords(0).setTv(texture10.getTextureV());

            face2.getVertex(1).setPosition(p2);
            face2.getTextureCoords(1).setTu(texture01.getTextureU());
            face2.getTextureCoords(1).setTv(texture01.getTextureV());

            face2.getVertex(2).setPosition(p3);
            face2.getTextureCoords(2).setTu(texture11.getTextureU());
            face2.getTextureCoords(2).setTv(texture11.getTextureV());

            faceCounter += 2;
        }
    }

    private void generateStartPositions(int nbPositions, float radius) {

        Point3D startPosition = Point3D.zero();

        for (int i = 0; i < nbPositions; i++) {
            float x = random.nextFloat() * radius;
            float y = random.nextFloat() * radius;
            float z = random.nextFloat() * radius;
            this.positions.add(new Point3D(x, y, z));
            this.sizes.add(1f);
        }
    }

}
