package com.ggervais.gameengine.particle;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.render.DepthSortableEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ParticleEmitter implements DepthSortableEntity {

    protected static final Random random = new Random();
    protected ParticleEmitterConfiguration configuration;
    protected List<Particle> particles;

    public Point3D getPosition() {
        return this.configuration.position;
    }

    public ParticleEmitter() {
        this.particles = new ArrayList<Particle>();
    }

    public ParticleEmitter(ParticleEmitterConfiguration configuration) {
        this();
        setConfiguration(configuration);
    }

    public void setConfiguration(ParticleEmitterConfiguration configuration) {
        this.configuration = configuration;
    }

    public ParticleEmitterConfiguration getConfiguration() {
        return this.configuration;
    }

    public abstract void update(long currentTime);

    public int nbParticles() {
        return this.particles.size();
    }

    public Particle getParticle(int index) {
        return this.particles.get(index);
    }
}
