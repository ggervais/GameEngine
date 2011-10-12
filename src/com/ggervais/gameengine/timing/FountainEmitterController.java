package com.ggervais.gameengine.timing;

import com.ggervais.gameengine.geometry.QuadGeometry;
import com.ggervais.gameengine.math.MathUtils;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.particle.Particle;
import com.ggervais.gameengine.particle.ParticleEmitterConfiguration;
import com.ggervais.gameengine.scene.scenegraph.*;
import com.ggervais.gameengine.scene.scenegraph.renderstates.AlphaBlendingState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.ZBufferState;

import java.awt.*;
import java.util.Iterator;

public class FountainEmitterController extends ParticleEmitterController {

    private float initialSpeed;
    private float theta;
    private float distanceFromCenter;
    private int ringSize;

    public FountainEmitterController(ParticleEmitterConfiguration configuration, float initialSpeed, float theta, float distanceFromCenter, int ringSize) {
        this(null, configuration, initialSpeed, theta, distanceFromCenter, ringSize);
    }

    public FountainEmitterController(Node controlledSpatialObject, ParticleEmitterConfiguration configuration, float initialSpeed, float theta, float distanceFromCenter, int ringSize) {
        super(controlledSpatialObject, configuration);
        this.initialSpeed = initialSpeed;
        this.theta = theta;
        this.distanceFromCenter = distanceFromCenter;
        this.ringSize = ringSize;
    }

    @Override
    public void doUpdate(long currentTime) {
        Node controlledNode = (Node) this.controlledSpatialObject;

        if (currentTime - this.lastLaunch >= this.configuration.delayBetweenLaunchInMs) {
            float angleStep = (float) 2 * (float) Math.PI / (float) this.ringSize;
            RotationMatrix rotationMatrix = this.controlledSpatialObject.getWorldTransformation().getRotationMatrix();
            for (int i = 0; i < this.ringSize; i++) {
                float phi = i * angleStep;

                Vector3D particlePosition = Vector3D.zero();

                particlePosition.x(particlePosition.x() + this.distanceFromCenter * (float) Math.cos(phi));
                particlePosition.z(particlePosition.z() + this.distanceFromCenter * (float) Math.sin(phi));

                particlePosition = rotationMatrix.mult(particlePosition);

                Vector3D baseVelocity = Vector3D.createFromPolarCoordinates(this.initialSpeed, this.theta, phi);
                Vector3D adjustedVelocity = rotationMatrix.mult(baseVelocity);

                QuadGeometry particleQuad = new QuadGeometry();
                Transformation baseParticleTransformation = new Transformation();
                baseParticleTransformation.setTranslation(particlePosition.x(), particlePosition.y(), particlePosition.z());
                baseParticleTransformation.setRotationMatrix(RotationMatrix.createFromXYZ(0, -phi, this.theta));

                Effect effect = new Effect();
                effect.addTexture(this.configuration.texture);
                Vector3D color = this.configuration.color;
                effect.setColor(new Color(255, 127, 0, 127));
                particleQuad.setEffect(effect);
                particleQuad.addGlobalState(new AlphaBlendingState(true));
                particleQuad.addGlobalState(new ZBufferState(false, true));

                particleQuad.setLocalTransformation(baseParticleTransformation);
                particleQuad.addController(new LifeController(currentTime, this.configuration.particleLifeTimeInMs));
                particleQuad.addController(new AlphaController(particleQuad, currentTime, this.configuration.particleLifeTimeInMs, this.configuration.startAlpha, this.configuration.endAlpha));
                // TODO add motion controller
                synchronized (controlledNode.getChildrenLock()) {
                    controlledNode.addChild(particleQuad);
                }
                this.lastLaunch = currentTime;
            }
        }

        Iterator<Spatial> childrenIterator = controlledNode.getChildrenIterator();
        while(childrenIterator.hasNext()) {
            Spatial child = childrenIterator.next();
            for (Controller controller : child.getControllers()) {
                if (controller instanceof LifeController) {
                    LifeController lifeController = (LifeController) controller;
                    if (lifeController.isDead()) {
                        childrenIterator.remove();
                    }
                }
            }
        }
    }
}
