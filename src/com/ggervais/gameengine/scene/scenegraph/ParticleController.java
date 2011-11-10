package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.ParticlesGeometry;
import com.ggervais.gameengine.geometry.primitives.TextureCoords;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.MathUtils;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.physics.MotionController;
import com.ggervais.gameengine.timing.Controller;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleController extends MotionController {

    private static Random random = new Random();
    private List<Vector3D> particleVelocities;
    private List<Long> timesAdded;
    private Vector3D systemGravity;
    private int decayTimeInMs;
    private int launchDelayInMs;
    private boolean isInitialized;
    private float startAlpha;
    private float endAlpha;
    private float startSize;
    private float endSize;

    public ParticleController(Vector3D gravity, float speed, float theta, float phi, int decayTimeInMs, int launchDelayInMs, float startAlpha, float endAlpha, float startSize, float endSize) {
        super(Vector3D.zero(), speed, theta, phi, false);
        this.systemGravity = gravity;
        this.particleVelocities = new ArrayList<Vector3D>();
        this.timesAdded = new ArrayList<Long>();
        this.decayTimeInMs = decayTimeInMs;
        this.launchDelayInMs = launchDelayInMs;
        this.isInitialized = false;
        this.startAlpha = startAlpha;
        this.endAlpha = endAlpha;
        this.startSize = startSize;
        this.endSize = endSize;
    }

    @Override
    public void setControlledObject(Spatial controlledObject) {
        super.setControlledObject(controlledObject);
        if (controlledObject != null) {
            if (controlledObject instanceof ParticlesGeometry) {
                this.isInitialized = false;
                this.controlledSpatialObject = controlledObject;
                this.particleVelocities.clear();
                this.timesAdded.clear();

                ParticlesGeometry particles = (ParticlesGeometry) controlledObject;
                for (int i = 0; i < particles.getNbParticles(); i++) {
                    //float speed = this.random.nextFloat() * 5f;
                    //float theta = this.random.nextFloat() * (float) Math.toRadians(45);
                    //float phi = this.random.nextFloat() * (float) Math.toRadians(360);
                    float speed = 0;
                    float theta = 0;
                    float phi = 0;
                    this.particleVelocities.add(Vector3D.createFromPolarCoordinates(speed, theta, phi));
                    particles.setSize(i, this.startSize);

                    Effect effect = particles.getEffect();
                    if (effect != null) {
                        Color color1 = effect.getColor(4 * i);
                        Color color2 = effect.getColor(4 * i + 1);
                        Color color3 = effect.getColor(4 * i + 2);
                        Color color4 = effect.getColor(4 * i + 3);

                        effect.setColor(4 * i, new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), (int) MathUtils.clamp(this.startAlpha * 255, 0, 255)));
                        effect.setColor(4 * i + 1, new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), (int) MathUtils.clamp(this.startAlpha * 255, 0, 255)));
                        effect.setColor(4 * i + 2, new Color(color3.getRed(), color3.getGreen(), color3.getBlue(), (int) MathUtils.clamp(this.startAlpha * 255, 0, 255)));
                        effect.setColor(4 * i + 3, new Color(color4.getRed(), color4.getGreen(), color4.getBlue(), (int) MathUtils.clamp(this.startAlpha * 255, 0, 255)));
                    }
                }

            } else {
                throw new IllegalArgumentException("Controller object for a ParticleController should be of type ParticlesGeometry.");
            }
        }
    }

    public void initializeTimes(long currentTime) {
        ParticlesGeometry particles = (ParticlesGeometry) this.controlledSpatialObject;
        this.timesAdded.clear();
        for (int i = 0; i < particles.getNbParticles(); i++) {
            this.timesAdded.add(currentTime);
        }
    }

    @Override
    public void doUpdate(long currentTime, InputController inputController) {

        // This updates particle system's motion (inherited from MotionController).
        super.doUpdate(currentTime, inputController);

        if (!isInitialized) {
            initializeTimes(currentTime);
            isInitialized = true;
        }

        float diff = (currentTime - this.lastUpdateTime);
        float sizeStep = (this.endSize - this.startSize) / this.decayTimeInMs;
        float alphaStep = (this.endAlpha - this.startAlpha) / this.decayTimeInMs;

        float sizeDiff = diff * sizeStep;
        float alphaDiff = diff * alphaStep;

        // This updates each particle with its velocity.
        ParticlesGeometry particles = (ParticlesGeometry) this.controlledSpatialObject;
        int vertexIndex = 0;
        List<Point3D> positionsToDelete = new ArrayList<Point3D>();
        for (int i = 0; i < particles.getNbActive(); i++) {
            // Update particle position.
            if (i < this.particleVelocities.size()) {

                particles.setSize(i, particles.getSize(i) + sizeDiff);

                Vector3D velocity = this.particleVelocities.get(i);
                if (velocity != null) {
                    Point3D position = particles.getPosition(i);
                    position.add(velocity.multiplied(diff / 1000f));
                    velocity.add(this.systemGravity.multiplied(diff / 1000f));
                }

                if (this.getControlledObject().getEffect() != null) {

                    Color color1 = this.getControlledObject().getEffect().getColor(vertexIndex);
                    Color color2 = this.getControlledObject().getEffect().getColor(vertexIndex + 1);
                    Color color3 = this.getControlledObject().getEffect().getColor(vertexIndex + 2);
                    Color color4 = this.getControlledObject().getEffect().getColor(vertexIndex + 3);

                    int alpha1 = (int) MathUtils.clamp(color1.getAlpha() + alphaDiff * 255, 0, 255);
                    int alpha2 = (int) MathUtils.clamp(color1.getAlpha() + alphaDiff * 255, 0, 255);
                    int alpha3 = (int) MathUtils.clamp(color1.getAlpha() + alphaDiff * 255, 0, 255);
                    int alpha4 = (int) MathUtils.clamp(color1.getAlpha() + alphaDiff * 255, 0, 255);

                    boolean toDelete = false;


                    if (currentTime - (this.timesAdded.get(i) + this.pauseOffset) >= this.decayTimeInMs) {
                        positionsToDelete.add(particles.getPosition(i));
                    }

                    Color newColor1 = new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), alpha1);
                    Color newColor2 = new Color(color1.getRed(), color2.getGreen(), color2.getBlue(), alpha2);
                    Color newColor3 = new Color(color1.getRed(), color3.getGreen(), color3.getBlue(), alpha3);
                    Color newColor4 = new Color(color1.getRed(), color4.getGreen(), color4.getBlue(), alpha4);

                    this.getControlledObject().getEffect().setColor(vertexIndex, newColor1);
                    this.getControlledObject().getEffect().setColor(vertexIndex + 1, newColor2);
                    this.getControlledObject().getEffect().setColor(vertexIndex + 2, newColor3);
                    this.getControlledObject().getEffect().setColor(vertexIndex + 3, newColor4);

                }

                vertexIndex += 4;
            }
        }

        long oldestLaunch = 0;
        for (int i = 0; i < particles.getNbActive(); i++) {
            oldestLaunch = Math.max(this.timesAdded.get(i), oldestLaunch);
        }

        for (Point3D position : positionsToDelete) {
            int index = particles.findPosition(position);
            if (index > -1) {

                // Move colors at the top.
                for (int i = 0; i < 4; i++) {
                    this.getControlledObject().getEffect().removeColor(index * 4);
                    this.getControlledObject().getEffect().addColor(new Color(255, 255, 255, 255));
                }

                // Texture coordinates must be changed, too.
                for (int textureIndex = 0; textureIndex < this.getControlledObject().getEffect().nbTextures(); textureIndex++) {
                    for (int i = 0; i < 6; i++) {
                        this.getControlledObject().getEffect().removeTextureCoordinates(textureIndex, 3, index * 6);
                        this.getControlledObject().getEffect().addTextureCoordinates(textureIndex, 3, new TextureCoords(0, 0));
                    }
                }

                // Same thing.
                this.timesAdded.remove(index);
                this.timesAdded.add(currentTime);

                // Same thing.
                particles.removePosition(index);
                particles.addPosition(Point3D.zero());

                // Same thing.
                particleVelocities.remove(index);
                particleVelocities.add(Vector3D.zero());

                particles.setNbActive(particles.getNbActive() - 1);
            }
        }

        if (currentTime - (oldestLaunch + this.pauseOffset) >= this.launchDelayInMs && particles.getNbActive() < particles.getNbParticles()) {

            // Launching a new particle.

            //float speed = this.random.nextFloat() * 5f;
            //float theta = this.random.nextFloat() * (float) Math.toRadians(45);
            //float phi = this.random.nextFloat() * (float) Math.toRadians(360);
            float speed = 0;
            float theta = 0;
            float phi = 0;

            this.particleVelocities.add(0, Vector3D.createFromPolarCoordinates(speed, theta, phi));
            this.particleVelocities.remove(this.particleVelocities.size() - 1);

            particles.addPosition(0, new Point3D(0, 0, 0));
            particles.removePosition(particles.getNbParticles()); // We do NOT do "- 1" because we just added a position, and nbParticles is a constant.

            particles.addSize(0, this.startSize);
            particles.removeSize(particles.getNbParticles());

            timesAdded.add(0, currentTime - this.pauseOffset);
            timesAdded.remove(timesAdded.size() - 1);

            // 4 vertices = 4 colors.
            Color newColor = new Color(particles.getEffect().getColor().getRed(), particles.getEffect().getColor().getGreen(), particles.getEffect().getColor().getBlue(), (int) MathUtils.clamp(this.startAlpha * 255, 0, 255));
            for (int i = 0; i < 4; i++) {
                this.controlledSpatialObject.getEffect().addColor(0, newColor);
                this.controlledSpatialObject.getEffect().removeColor(this.controlledSpatialObject.getEffect().nbColors() - 1);
            }

            for (int textureIndex = 0; textureIndex < this.controlledSpatialObject.getEffect().nbTextures(); textureIndex++) {

                Texture texture = this.controlledSpatialObject.getEffect().getTexture(textureIndex);

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

                this.controlledSpatialObject.getEffect().addTextureCoordinates(textureIndex, 3, 0, new TextureCoords(min.x() + tu1 * w, min.y() + tv1 * h));
                this.controlledSpatialObject.getEffect().addTextureCoordinates(textureIndex, 3, 0, new TextureCoords(min.x() + tu2 * w, min.y() + tv2 * h));
                this.controlledSpatialObject.getEffect().addTextureCoordinates(textureIndex, 3, 0, new TextureCoords(min.x() + tu3 * w, min.y() + tv3 * h));

                this.controlledSpatialObject.getEffect().addTextureCoordinates(textureIndex, 3, 0, new TextureCoords(min.x() + tu3 * w, min.y() + tv3 * h));
                this.controlledSpatialObject.getEffect().addTextureCoordinates(textureIndex, 3, 0, new TextureCoords(min.x() + tu4 * w, min.y() + tv4 * h));
                this.controlledSpatialObject.getEffect().addTextureCoordinates(textureIndex, 3, 0, new TextureCoords(min.x() + tu1 * w, min.y() + tv1 * h));

                for (int i = 0; i < 6; i++) {
                    this.controlledSpatialObject.getEffect().removeTextureCoordinates(textureIndex, 3, this.controlledSpatialObject.getEffect().getNbTextureCoords(textureIndex, 3) - 1);
                }
            }

            particles.setNbActive(particles.getNbActive() + 1);
        }
    }
}
