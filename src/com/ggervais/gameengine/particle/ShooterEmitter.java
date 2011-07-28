package com.ggervais.gameengine.particle;

import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;

public class ShooterEmitter extends ParticleEmitter {

    private static final Logger log = Logger.getLogger(ShooterEmitter.class);

    private float initialSpeed;
    private long lastLaunch;

    public ShooterEmitter(ParticleEmitterConfiguration configuration, float initialSpeed) {
        super(configuration);
        this.initialSpeed = initialSpeed;
        this.lastLaunch = 0;
    }

    @Override
    public void update(long currentTime) {

        if (currentTime - this.lastLaunch >= this.configuration.delayBetweenLaunchInMs) {
            float theta = this.configuration.rotation.z();
            float phi = this.configuration.rotation.y();
            Particle particle = new Particle(this.configuration.position.copy(),
                                             this.configuration.startScale,
                                             Vector3D.createFromPolarCoordinates(initialSpeed, theta, phi),
                                             this.configuration.gravity.copy(),
                                             this.configuration.color.copy(),
                                             this.configuration.particleLifeTimeInMs,
                                             this.configuration.texture,
                                             this.configuration.useAdditiveBlending,
                                             this.configuration.createBillboards);

            int textureIndex = this.random.nextInt(this.configuration.getNbTextureIndices());
            particle.setTextureIndex(textureIndex);

            if (!this.configuration.createBillboards) {
                particle.setRotation(this.configuration.rotation);
            }

            particle.setAlphaRange(this.configuration.startAlpha, this.configuration.endAlpha);
            particle.setScaleRange(this.configuration.startScale, this.configuration.endScale);
            particle.activate(currentTime);

            this.particles.add(particle);
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
