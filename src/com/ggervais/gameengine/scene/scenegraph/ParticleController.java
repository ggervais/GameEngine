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

    public static Random random = new Random();
    public List<Vector3D> particleVelocities;

    public ParticleController(Vector3D gravity, float speed, float theta, float phi) {
        super(gravity, speed, theta, phi, false);
        this.particleVelocities = new ArrayList<Vector3D>();
    }

    @Override
    public void setControlledObject(Spatial controlledObject) {
        if (controlledObject != null) {
            if (controlledObject instanceof ParticlesGeometry) {
                this.controlledSpatialObject = controlledObject;
                this.particleVelocities.clear();

                ParticlesGeometry particles = (ParticlesGeometry) controlledObject;
                for (int i = 0; i < particles.getNbParticles(); i++) {
                    float speed = this.random.nextFloat() * 5f;
                    float theta = this.random.nextFloat() * (float) Math.toRadians(45);
                    float phi = this.random.nextFloat() * (float) Math.toRadians(45);
                    this.particleVelocities.add(Vector3D.createFromPolarCoordinates(speed, theta, phi));
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

        // This updates each particle with its velocity.
        ParticlesGeometry particles = (ParticlesGeometry) this.controlledSpatialObject;
        for (int i = 0; i < particles.getNbActive(); i++) {
            // Update particle position.
            Point3D position = particles.getPosition(i);
        }
    }
}
