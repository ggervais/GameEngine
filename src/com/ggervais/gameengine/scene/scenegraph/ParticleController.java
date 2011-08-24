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
    private int launchDelayInMs = 100;
    private boolean isInitialized;

    public ParticleController(Vector3D gravity, float speed, float theta, float phi, int decayTimeInMs) {
        super(Vector3D.zero(), speed, theta, phi, false);
        this.systemGravity = gravity;
        this.particleVelocities = new ArrayList<Vector3D>();
        this.timesAdded = new ArrayList<Long>();
        this.initialParticlesStartIndex = 0;
        this.decayTimeInMs = decayTimeInMs;
        this.isInitialized = false;
    }

    public ParticleController(Vector3D gravity, float speed, float theta, float phi) {
        super(Vector3D.zero(), speed, theta, phi, false);
        this.systemGravity = gravity;
        this.particleVelocities = new ArrayList<Vector3D>();
        this.initialParticlesStartIndex = 0;
        this.timesAdded = new ArrayList<Long>();
        this.decayTimeInMs = 1000;
        this.isInitialized = false;
    }

    @Override
    public void setControlledObject(Spatial controlledObject) {
        if (controlledObject != null) {
            if (controlledObject instanceof ParticlesGeometry) {
                this.isInitialized = false;
                this.controlledSpatialObject = controlledObject;
                this.particleVelocities.clear();
                this.timesAdded.clear();

                ParticlesGeometry particles = (ParticlesGeometry) controlledObject;
                for (int i = 0; i < particles.getNbParticles(); i++) {
                    float speed = this.random.nextFloat() * 5f;
                    float theta = this.random.nextFloat() * (float) Math.toRadians(45);
                    float phi = this.random.nextFloat() * (float) Math.toRadians(360);
                    this.particleVelocities.add(Vector3D.createFromPolarCoordinates(speed, theta, phi));
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
    public void doUpdate(long currentTime) {

        // This updates particle system's motion (inherited from MotionController).
        super.doUpdate(currentTime);

        if (!isInitialized) {
            initializeTimes(currentTime);
            isInitialized = true;
        }

        float diffInSeconds = (currentTime - this.lastUpdateTime) / 1000f;
        float alphaStep = diffInSeconds * 255 / (this.decayTimeInMs / 1000f);

        // This updates each particle with its velocity.
         ParticlesGeometry particles = (ParticlesGeometry) this.controlledSpatialObject;
        int vertexIndex = 0;
        List<Point3D> positionsToDelete = new ArrayList<Point3D>();
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

                // For each element we remove, we must add it at the end to keep the number of elements constant.
                Color color1 = this.getControlledObject().getEffect().getColor(index * 4);
                Color color2 = this.getControlledObject().getEffect().getColor(index * 4 + 1);
                Color color3 = this.getControlledObject().getEffect().getColor(index * 4 + 2);
                Color color4 = this.getControlledObject().getEffect().getColor(index * 4 + 3);

                // Removing colors in their current positions.
                this.getControlledObject().getEffect().removeColor(color1);
                this.getControlledObject().getEffect().removeColor(color2);
                this.getControlledObject().getEffect().removeColor(color3);
                this.getControlledObject().getEffect().removeColor(color4);

                // Adding color at the end of the array.
                this.getControlledObject().getEffect().addColor(color1);
                this.getControlledObject().getEffect().addColor(color2);
                this.getControlledObject().getEffect().addColor(color3);
                this.getControlledObject().getEffect().addColor(color4);

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

                System.out.println("Deleting " + position + ", nb. active: " + particles.getNbActive());
            }
        }

        if (currentTime - (oldestLaunch + this.pauseOffset) >= this.launchDelayInMs && particles.getNbActive() < particles.getNbParticles()) {
            float speed = this.random.nextFloat() * 5f;
            float theta = this.random.nextFloat() * (float) Math.toRadians(45);
            float phi = this.random.nextFloat() * (float) Math.toRadians(360);
            this.particleVelocities.add(0, Vector3D.createFromPolarCoordinates(speed, theta, phi));
            this.particleVelocities.remove(this.particleVelocities.size() - 1);

            particles.addPosition(0, Point3D.zero());
            particles.removePosition(particles.getNbParticles()); // We do NOT do "- 1" because we just added a position.

            timesAdded.add(0, currentTime - this.pauseOffset);
            timesAdded.remove(timesAdded.size() - 1);

            // 4 vertices = 4 colors.
            this.controlledSpatialObject.getEffect().addColor(0, new Color(255, 255, 255, 255));
            this.controlledSpatialObject.getEffect().addColor(0, new Color(255, 255, 255, 255));
            this.controlledSpatialObject.getEffect().addColor(0, new Color(255, 255, 255, 255));
            this.controlledSpatialObject.getEffect().addColor(0, new Color(255, 255, 255, 255));

            this.controlledSpatialObject.getEffect().removeColor(this.controlledSpatialObject.getEffect().nbColors() - 1);
            this.controlledSpatialObject.getEffect().removeColor(this.controlledSpatialObject.getEffect().nbColors() - 1);
            this.controlledSpatialObject.getEffect().removeColor(this.controlledSpatialObject.getEffect().nbColors() - 1);
            this.controlledSpatialObject.getEffect().removeColor(this.controlledSpatialObject.getEffect().nbColors() - 1);

            particles.setNbActive(particles.getNbActive() + 1);
            System.out.println("Creating new particle.");
        }
    }
}
