package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.ParticlesGeometry;
import com.ggervais.gameengine.math.MathUtils;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.physics.MotionController;
import com.ggervais.gameengine.timing.Controller;

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

        // This updates each particle with its velocity.
        ParticlesGeometry particles = (ParticlesGeometry) this.controlledSpatialObject;
        for (int i = 0; i < particles.getNbActive(); i++) {
            // Update particle position.
            if (i < this.particleVelocities.size()) {
                Vector3D velocity = this.particleVelocities.get(i);
                if (velocity != null) {
                    Point3D position = particles.getPosition(i);
                    position.add(velocity.multiplied(diffInSeconds));

                    velocity.add(this.systemGravity.multiplied(diffInSeconds));
                }
            }
        }
    }
}
