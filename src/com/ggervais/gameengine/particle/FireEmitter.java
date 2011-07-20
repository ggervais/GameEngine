package com.ggervais.gameengine.particle;

import com.ggervais.gameengine.material.Material;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.resource.ResourceSubsystem;
import com.ggervais.gameengine.resource.ResourceType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class FireEmitter extends ParticleEmitter {

    private static final Logger log = Logger.getLogger(FireEmitter.class);
    private ShooterEmitter flameEmitter;
    private ShooterEmitter smokeEmitter;
    private ShooterEmitter shockwaveEmitter;
    private FountainEmitter flareEmitter;
    private Point3D position;

    public FireEmitter(Point3D position, float scale) {
        this.position = position;
        initInternalEmitters(position, scale);
    }

    @Override
    public Point3D getPosition() {
        return this.position;
    }

    private void initInternalEmitters(Point3D position, float scale) {

        Material matSmoke = (Material) ResourceSubsystem.getInstance().findResourceByTypeAndName(ResourceType.MATERIAL, "smoke");
        Material matShockwave = (Material) ResourceSubsystem.getInstance().findResourceByTypeAndName(ResourceType.MATERIAL, "shockwave");
        Material matFlare = (Material) ResourceSubsystem.getInstance().findResourceByTypeAndName(ResourceType.MATERIAL, "flare");

        Texture texSmoke = matSmoke.getTexture(0);
        Texture texShockwave = matShockwave.getTexture(0);
        Texture texFlare = matFlare.getTexture(0);

        Vector3D upGravity = new Vector3D(0, 9.81f * scale, 0);

        ParticleEmitterConfiguration flameConfiguration = new ParticleEmitterConfiguration();
        flameConfiguration.position = position;
        flameConfiguration.setRotation(0, 0, (float) Math.toRadians(90));
        flameConfiguration.texture = texSmoke;
        flameConfiguration.delayBetweenLaunchInMs = 100;
        flameConfiguration.particleLifeTimeInMs = 750;
        flameConfiguration.gravity = upGravity;
        flameConfiguration.setColor(1.0f, 0.5f, 0.0f);
        flameConfiguration.setAlphaRange(0.75f, 0);
        flameConfiguration.setScaleRange(2 * scale, 7 * scale);
        this.flameEmitter = new ShooterEmitter(flameConfiguration, 1.0f * scale);

        ParticleEmitterConfiguration smokeConfiguration = new ParticleEmitterConfiguration();
        smokeConfiguration.texture = texSmoke;
        smokeConfiguration.delayBetweenLaunchInMs = 100;
        smokeConfiguration.particleLifeTimeInMs = 1500;
        smokeConfiguration.setRotation(0, 0, (float) Math.toRadians(90));
        smokeConfiguration.gravity = upGravity;
        smokeConfiguration.setColor(0.3f, 0.3f, 0.3f);
        smokeConfiguration.setAlphaRange(0.3f, 0);
        smokeConfiguration.setScaleRange(2.5f * scale, 7 * scale);
        smokeConfiguration.setPosition(position.x(), position.y() + 1.5f * scale, position.z());
        smokeConfiguration.texture = texSmoke;
        this.smokeEmitter = new ShooterEmitter(smokeConfiguration, 1.0f * scale);

        ParticleEmitterConfiguration shockwaveConfiguration = new ParticleEmitterConfiguration();
        shockwaveConfiguration.texture = texShockwave;
        shockwaveConfiguration.delayBetweenLaunchInMs = 1000;
        shockwaveConfiguration.particleLifeTimeInMs = 1000;
        shockwaveConfiguration.createBillboards = false;
        shockwaveConfiguration.setPosition(position.x(), position.y() - 0.25f * scale, position.z());
        shockwaveConfiguration.setRotation((float) Math.toRadians(90), 0 ,0);
        shockwaveConfiguration.setGravity(0, 0, 0);
        shockwaveConfiguration.setColor(1, 0.5f, 0);
        shockwaveConfiguration.setAlphaRange(1, 0);
        shockwaveConfiguration.setScaleRange(0 * scale, 5 * scale);
        this.shockwaveEmitter = new ShooterEmitter(shockwaveConfiguration, 0 * scale);

        ParticleEmitterConfiguration flareConfiguration = new ParticleEmitterConfiguration();
        flareConfiguration.position = position;
        flareConfiguration.texture = texFlare;
        flareConfiguration.particleLifeTimeInMs = 1000;
        flareConfiguration.delayBetweenLaunchInMs = 1000;
        flareConfiguration.setGravity(0, 0, 0);
        flareConfiguration.setColor(0.76f, 0.70f, 0.50f);
        flareConfiguration.createBillboards = false;
        flareConfiguration.setScaleRange(0 * scale, 0.75f * scale);
        flareConfiguration.setAlphaRange(1, 0);
        this.flareEmitter = new FountainEmitter(flareConfiguration, 1 * scale, (float) Math.toRadians(45), 0.15f, 5);
    }

    @Override
    public void update(long currentTime) {

        this.flameEmitter.update(currentTime);
        this.smokeEmitter.update(currentTime);
        this.shockwaveEmitter.update(currentTime);
        this.flareEmitter.update(currentTime);
    }

    @Override
    public int nbParticles() {
        return this.flameEmitter.nbParticles() + this.smokeEmitter.nbParticles() + this.shockwaveEmitter.nbParticles() + this.flareEmitter.nbParticles();
        //return this.flareEmitter.nbParticles();
    }

    @Override
    public Particle getParticle(int index) throws ArrayIndexOutOfBoundsException {
        if (index < 0 || index >= nbParticles()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        List<Particle> allParticles = new ArrayList<Particle>();
        for (int i = 0; i < flameEmitter.nbParticles(); i++) {
            allParticles.add(flameEmitter.getParticle(i));
        }
        for (int i = 0; i < smokeEmitter.nbParticles(); i++) {
            allParticles.add(smokeEmitter.getParticle(i));
        }
        for (int i = 0; i < shockwaveEmitter.nbParticles(); i++) {
            allParticles.add(shockwaveEmitter.getParticle(i));
        }
        for (int i = 0; i < flareEmitter.nbParticles(); i++) {
            allParticles.add(flareEmitter.getParticle(i));
        }
        return allParticles.get(index);
    }
}
