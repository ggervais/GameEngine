package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.ParticlesGeometry;
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
    private int initialParticlesStartIndex;
    private int decayTimeInMs;

    public ParticleController(Vector3D gravity, float speed, float theta, float phi, int decayTimeInMs) {
        super(Vector3D.zero(), speed, theta, phi, false);
        this.systemGravity = gravity;
        this.particleVelocities = new ArrayList<Vector3D>();
        this.timesAdded = new ArrayList<Long>();
        this.initialParticlesStartIndex = 0;
        this.decayTimeInMs = decayTimeInMs;
    }

    public ParticleController(Vector3D gravity, float speed, float theta, float phi) {
        super(Vector3D.zero(), speed, theta, phi, false);
        this.systemGravity = gravity;
        this.particleVelocities = new ArrayList<Vector3D>();
        this.initialParticlesStartIndex = 0;
        this.timesAdded = new ArrayList<Long>();
        this.decayTimeInMs = 5000;
    }

    @Override
    public void setControlledObject(Spatial controlledObject) {
        if (controlledObject != null) {
            if (controlledObject instanceof ParticlesGeometry) {
                this.controlledSpatialObject = controlledObject;
                this.particleVelocities.clear();
                this.timesAdded.clear();

                ParticlesGeometry particles = (ParticlesGeometry) controlledObject;
                for (int i = 0; i < particles.getNbParticles(); i++) {
                    float speed = this.random.nextFloat() * 5f;
                    float theta = this.random.nextFloat() * (float) Math.toRadians(45);
                    float phi = this.random.nextFloat() * (float) Math.toRadians(360);
                    this.particleVelocities.add(Vector3D.createFromPolarCoordinates(speed, theta, phi));
                    this.timesAdded.add(System.currentTimeMillis()); // This should do it for now. Eventually we should have a centralized time source.
                }

            } else {
                throw new IllegalArgumentException("Controller object for a ParticleController should be of type ParticlesGeometry.");
            }
        }
    }

    @Override
    public void doUpdate(long currentTime) {
        // This updates particle system's motion (inherited from MotionController).
        super.doUpdate(currentTime);

        float diffInSeconds = (currentTime - this.lastUpdateTime) / 1000f;
        float alphaStep = diffInSeconds * 255 / (this.decayTimeInMs / 1000f);

        // This updates each particle with its velocity.
        ParticlesGeometry particles = (ParticlesGeometry) this.controlledSpatialObject;
        int vertexIndex = 0;
        for (int i = 0; i < particles.getNbActive(); i++) {
            // Update particle position.
            if (i < this.particleVelocities.size()) {
                Vector3D velocity = this.particleVelocities.get(i);
                if (velocity != null) {
                    Point3D position = particles.getPosition(i);
                    position.add(velocity.multiplied(diffInSeconds));
                    velocity.add(this.systemGravity.multiplied(diffInSeconds));
                }

                if (this.getControlledObject().getEffect() != null) {
                    Color color1 = this.getControlledObject().getEffect().getColor(vertexIndex);
                    Color color2 = this.getControlledObject().getEffect().getColor(vertexIndex + 1);
                    Color color3 = this.getControlledObject().getEffect().getColor(vertexIndex + 2);
                    Color color4 = this.getControlledObject().getEffect().getColor(vertexIndex + 3);

                    int alpha1 = (int) MathUtils.clamp(color1.getAlpha() - alphaStep, 0, 255);
                    int alpha2 = (int) MathUtils.clamp(color1.getAlpha() - alphaStep, 0, 255);
                    int alpha3 = (int) MathUtils.clamp(color1.getAlpha() - alphaStep, 0, 255);
                    int alpha4 = (int) MathUtils.clamp(color1.getAlpha() - alphaStep, 0, 255);
                    if (alpha1 <= 0) {
                        alpha1 = 255;
                    }
                    if (alpha2 <= 0) {
                        alpha2 = 255;
                    }
                    if (alpha3 <= 0) {
                        alpha3 = 255;
                    }
                    if (alpha4 <= 0) {
                        alpha4 = 255;
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
    }
}
