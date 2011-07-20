package com.ggervais.gameengine.particle;

import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.Vector3D;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class FountainEmitter extends ParticleEmitter {

    private static final Logger log = Logger.getLogger(FountainEmitter.class);

    private float initialSpeed;
    private float theta;
    private float distanceFromCenter;
    private int ringSize;
    private long lastLaunch;

    public FountainEmitter(ParticleEmitterConfiguration configuration, float initialSpeed, float theta, float distanceFromCenter, int ringSize) {
        super(configuration);
        this.initialSpeed = initialSpeed;
        this.theta = theta;
        this.distanceFromCenter = distanceFromCenter;
        this.ringSize = ringSize;
        this.lastLaunch = 0;
    }

    // TODO Setup particle alongside emitter rotation plane.
    @Override
    public void update(long currentTime) {

        if (currentTime - this.lastLaunch >= this.configuration.delayBetweenLaunchInMs) {

            float angleStep = (float) 2 * (float) Math.PI / (float) this.ringSize;
            RotationMatrix emitterRotationMatrix = RotationMatrix.createFromXYZ(this.configuration.rotation.x(), this.configuration.rotation.y(), this.configuration.rotation.z());
            for (int i = 0; i < this.ringSize; i++) {
                float phi = i * angleStep;

                Point3D particlePosition = this.configuration.position.copy();

                particlePosition.x(particlePosition.x() + this.distanceFromCenter * (float) Math.cos(phi));
                particlePosition.z(particlePosition.z() + this.distanceFromCenter * (float) Math.sin(phi));

                particlePosition = emitterRotationMatrix.mult(particlePosition);

                Vector3D baseVelocity = Vector3D.createFromPolarCoordinates(this.initialSpeed, this.theta, phi);
                Vector3D adjustedVelocity = emitterRotationMatrix.mult(baseVelocity);
                Particle particle = new Particle(particlePosition,
                                                 this.configuration.startScale,
                                                 adjustedVelocity,
                                                 this.configuration.gravity.copy(),
                                                 this.configuration.color,
                                                 this.configuration.particleLifeTimeInMs,
                                                 this.configuration.texture,
                                                 this.configuration.useAdditiveBlending,
                                                 this.configuration.createBillboards);

                int textureIndex = this.random.nextInt(this.configuration.getNbTextureIndices());
                particle.setTextureIndex(textureIndex);

                if (!this.configuration.createBillboards) {
                    particle.getRotationMatrix().mult(emitterRotationMatrix);
                    particle.getRotationMatrix().mult(RotationMatrix.createFromXYZ(0, -phi, this.theta));
                }

                particle.setAlphaRange(this.configuration.startAlpha, this.configuration.endAlpha);
                particle.setScaleRange(this.configuration.startScale, this.configuration.endScale);
                particle.activate(currentTime);

                this.particles.add(particle);
            }

            this.lastLaunch = currentTime;
        }

        Iterator<Particle> particleIterator = this.particles.iterator();
        while (particleIterator.hasNext()) {
            Particle particle = particleIterator.next();
            particle.update(currentTime);
            if (particle.isDead()) {
                particle.deactivate();
                particleIterator.remove();
            }
        }
    }
}
