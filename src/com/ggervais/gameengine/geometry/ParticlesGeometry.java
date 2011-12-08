package com.ggervais.gameengine.geometry;

import com.ggervais.gameengine.geometry.primitives.Face;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.geometry.primitives.Vertex;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.Camera;
import com.ggervais.gameengine.scene.scenegraph.Effect;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticlesGeometry extends Geometry {

    private static final Logger log = Logger.getLogger(ParticlesGeometry.class);
    private static final float DEFAULT_SIZE = 1;
    private List<Point3D> positions;
    private List<Float> sizes;
    private static Random random = new Random();
    private int nbActive;
    private int nbParticles;

    public ParticlesGeometry(int nbParticles, float radius) {
        super(3);
        this.positions = new ArrayList<Point3D>();
        this.sizes = new ArrayList<Float>();
        generateStartPositions(nbParticles, radius);
        generateEmptyVertices();
        this.nbActive = nbParticles;
        this.nbParticles = nbParticles;
    }

    public void setSize(int i, float size) {
        this.sizes.set(i, size);
    }

    public float getSize(int i) {
        return this.sizes.get(i);
    }

    public void addSize(int index, float size) {
        this.sizes.add(index, size);
    }

    public void removeSize(int index) {
        this.sizes.remove(index);
    }

    public int getNbParticles() {
        return nbParticles;
    }

    public int getNbActive() {
        return this.nbActive;
    }

    public void setNbActive(int nbActive) {
        this.nbActive = nbActive;
        setNbVertices(nbActive * 4);
        setNbFaces(this.nbActive * 2);
    }

    @Override
    public void draw(SceneRenderer renderer) {
        Camera camera = renderer.getScene().getCamera();
        generateParticles(camera);
        super.draw(renderer);
    }

    private void generateEmptyVertices() {
        int vertexCounter = 0;
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

            addFace(face1);
            addFace(face2);

            this.vertexBuffer.addVertex(new Vertex());
            this.vertexBuffer.addVertex(new Vertex());
            this.vertexBuffer.addVertex(new Vertex());
            this.vertexBuffer.addVertex(new Vertex());

            this.textureBuffer.addCoords(new TextureCoords());
            this.textureBuffer.addCoords(new TextureCoords());
            this.textureBuffer.addCoords(new TextureCoords());
            this.textureBuffer.addCoords(new TextureCoords());

            this.indexBuffer.addIndex(3, vertexCounter);
            this.indexBuffer.addIndex(3, vertexCounter + 1);
            this.indexBuffer.addIndex(3, vertexCounter + 2);

            this.indexBuffer.addIndex(3, vertexCounter + 2);
            this.indexBuffer.addIndex(3, vertexCounter + 3);
            this.indexBuffer.addIndex(3, vertexCounter);

            vertexCounter += 4;
        }
    }

    private void generateParticles(Camera camera) {

        this.setGeometryDirty(true);

        // Should be RotationMatrix, but this works too.
        Matrix4x4 transposedRotation = this.worldTransform.getRotationMatrix().transposed();

        Vector3D cameraUp = camera.getUp();
        Vector3D cameraDirection = camera.getDirection();

        Vector3D direction = cameraDirection.normalized();
        Vector3D right = Vector3D.crossProduct(direction, cameraUp).normalized();
        Vector3D up = Vector3D.crossProduct(right, direction).normalized();

        Vector3D directionPrime = transposedRotation.mult(direction);
        Vector3D rightPrime = transposedRotation.mult(right);
        Vector3D upPrime = transposedRotation.mult(up);

        TextureCoords texture00 = new TextureCoords(0, 0);
		TextureCoords texture10 = new TextureCoords(1, 0);
		TextureCoords texture01 = new TextureCoords(0, 1);
		TextureCoords texture11 = new TextureCoords(1, 1);

        int positionCounter = 0;
        for (int i = 0; i < positions.size(); i++) {

            Point3D position = positions.get(i);
            float size = this.sizes.get(i);

            float halfSize = size * 0.5f;

            // This is the inverse of the what is written in the WildMagic book, but it works.
            Point3D p1 = Point3D.add(position, Vector3D.sub(upPrime, rightPrime).multiplied(halfSize));
            Point3D p2 = Point3D.sub(position, Vector3D.add(upPrime, rightPrime).multiplied(halfSize));
            Point3D p3 = Point3D.sub(position, Vector3D.sub(upPrime, rightPrime).multiplied(halfSize));
            Point3D p4 = Point3D.add(position, Vector3D.add(upPrime, rightPrime).multiplied(halfSize));

            /*p1 = new Point3D((-0.5f + position.x()) * size, (0.5f + position.y()) * size, position.z() * size);
            p2 = new Point3D((-0.5f + position.x()) * size, (-0.5f + position.y()) * size , position.z() * size);
            p3 = new Point3D((0.5f + position.x()) * size, (-0.5f + position.y()) * size, position.z() * size);
            p4 = new Point3D((0.5f + position.x()) * size, (0.5f + position.y() * size), position.z() * size);*/

            Vertex vertex1 = this.vertexBuffer.getVertex(positionCounter);
            Vertex vertex2 = this.vertexBuffer.getVertex(positionCounter + 1);
            Vertex vertex3 = this.vertexBuffer.getVertex(positionCounter + 2);
            Vertex vertex4 = this.vertexBuffer.getVertex(positionCounter + 3);

            TextureCoords tc1 = this.textureBuffer.getCoords(positionCounter);
            TextureCoords tc2 = this.textureBuffer.getCoords(positionCounter + 1);
            TextureCoords tc3 = this.textureBuffer.getCoords(positionCounter + 2);
            TextureCoords tc4 = this.textureBuffer.getCoords(positionCounter + 3);

            vertex1.setPosition(p1);
            //tc1.setTu(texture00.getTextureU());
            //tc1.setTv(texture00.getTextureV());

            vertex2.setPosition(p2);
            //tc2.setTu(texture01.getTextureU());
            //tc2.setTv(texture01.getTextureV());

            vertex3.setPosition(p3);
            //tc3.setTu(texture11.getTextureU());
            //tc3.setTv(texture11.getTextureV());

            vertex4.setPosition(p4);
            //tc4.setTu(texture10.getTextureU());
            //tc4.setTv(texture10.getTextureV());

            positionCounter += 4;
        }
    }

    private void generateStartPositions(int nbPositions, float radius) {

        Point3D startPosition = Point3D.zero();

        for (int i = 0; i < nbPositions; i++) {
            float x = random.nextFloat() * radius;
            float y = random.nextFloat() * radius;
            float z = random.nextFloat() * radius;
            this.positions.add(new Point3D(x, y, z));
            this.sizes.add(DEFAULT_SIZE);
        }
    }

    public void setPosition(int i, Point3D position) {
        if (i >= 0 && i < this.positions.size()) {
            this.positions.set(i, position);
        }
    }

    @Override
    protected void generateTextureCoords(Effect effect) {
        for (int i = 0; i < effect.nbTextures(); i++) {
            Texture texture = effect.getTexture(i);

            effect.clearTextureCoordinates(i, 3);
            for (int j = 0; j < this.positions.size(); j++) {

                int cell = random.nextInt(texture.getNbCellsWidth() * texture.getNbCellsHeight());

                Vector3D min = texture.getMinBounds(cell);
                Vector3D max = texture.getMaxBounds(cell);
                float w = max.x() - min.x();
                float h = max.y() - min.y();

                float tu1 = 0;
                float tv1 = 0;

                float tu2 = 0;
                float tv2 = 1;

                float tu3 = 1;
                float tv3 = 1;

                float tu4 = 1;
                float tv4 = 0;


                effect.addTextureCoordinates(i, 3, new TextureCoords(min.x() + tu1 * w, min.y() + tv1 * h));
                effect.addTextureCoordinates(i, 3, new TextureCoords(min.x() + tu2 * w, min.y() + tv2 * h));
                effect.addTextureCoordinates(i, 3, new TextureCoords(min.x() + tu3 * w, min.y() + tv3 * h));

                effect.addTextureCoordinates(i, 3, new TextureCoords(min.x() + tu3 * w, min.y() + tv3 * h));
                effect.addTextureCoordinates(i, 3, new TextureCoords(min.x() + tu4 * w, min.y() + tv4 * h));
                effect.addTextureCoordinates(i, 3, new TextureCoords(min.x() + tu1 * w, min.y() + tv1 * h));
            }
        }
    }

    public Point3D getPosition(int i) {
        return this.positions.get(i);
    }

    public int findPosition(Point3D position) {
        return this.positions.indexOf(position);
    }

    public void removePosition(int index) {
        this.positions.remove(index);
    }

    public void addPosition(Point3D position) {
        this.positions.add(position);
    }

    public void addPosition(int index, Point3D position) {
        this.positions.add(index, position);
    }

}
