package com.ggervais.gameengine.particle;

import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.timing.FloatStepper;
import com.ggervais.gameengine.timing.Point3DStepper;
import org.apache.log4j.Logger;

import java.util.Random;

public class Particle {


    private static final Logger log = Logger.getLogger(Particle.class);
    private static final Vector3D DEFAULT_GRAVITY = new Vector3D(0, 0, 0);

    private Point3D position;
    private Vector3D velocity;
    private Vector3D gravity;
    private Vector3D rotation;
    private float life;
    private int lifeInMs;
    private boolean active;
    private int textureIndex;
    private long startTime;
    private long lastUpdate;
    private FloatStepper scaleStepper;
    private Point3DStepper positionStepper;
    private FloatStepper rotationStepper;
    private FloatStepper alphaStepper;
    private Vector3D color;
    private float scale;

    private RotationMatrix rotationMatrix;

    private static final Random random = new Random();
    private float startScale;
    private float endScale;

    private float startAlpha;
    private float endAlpha;
    private float alpha;

    private boolean useAdditiveBlending;
    private boolean isBillboard;
    private Texture texture;

    public Particle() {
        this(DEFAULT_GRAVITY);
    }

    public Particle(Vector3D gravity) {
        this.position = Point3D.zero();
        this.gravity = gravity;
        this.lifeInMs = Integer.MAX_VALUE;
        this.life = 1;
        this.active = false;
        this.rotation = Vector3D.zero();
        this.rotationMatrix = new RotationMatrix();
        this.useAdditiveBlending = true;
        this.isBillboard = true;
        setScaleRange(1, 1);
        setAlphaRange(1, 0);
    }

    public Particle(Point3D position, float scale, Vector3D velocity, Vector3D gravity, Vector3D color, int lifeInMs, Texture texture, boolean useAdditiveBlending, boolean isBillboard) {
        this.position = position;
        this.velocity = velocity;
        this.gravity = gravity;
        this.lifeInMs = lifeInMs;
        this.color = color;
        this.life = 1;
        // TODO: change transition times to integers.
        this.scaleStepper = new FloatStepper(scale, scale, scale, Float.MAX_VALUE);
        this.rotation = Vector3D.zero();
        this.rotationMatrix = new RotationMatrix();
        setScaleRange(scale, scale);
        setAlphaRange(1, 0);
        this.useAdditiveBlending = useAdditiveBlending;
        this.isBillboard = isBillboard;
        this.texture = texture;
    }

    public Vector3D getColor() {
        return this.color;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setRotation(Vector3D rotation) {
        this.rotation = rotation;
        this.rotationMatrix = RotationMatrix.createFromXYZ(rotation.x(), rotation.y(), rotation.z());
    }

    public void activate(long currentTime) {
        this.active = true;
        this.startTime = currentTime;
        this.lastUpdate = currentTime;
        this.life = 1;

        if (this.scaleStepper != null) {
            this.scaleStepper.activate(currentTime);
        }

        if (this.positionStepper != null) {
            this.positionStepper.activate(currentTime);
        }

        if (this.rotationStepper != null) {
            this.rotationStepper.activate(currentTime);
        }

        if (this.alphaStepper != null) {
            this.alphaStepper.activate(currentTime);
        }

        this.scale = this.startScale;
        this.alpha = this.startAlpha;
    }

    public void deactivate() {
        this.active = false;

        if (this.scaleStepper != null) {
            this.scaleStepper.deactivate();
        }

        if (this.positionStepper != null) {
            this.positionStepper.deactivate();
        }

        if (this.rotationStepper != null) {
            this.rotationStepper.deactivate();
        }

        if (this.alphaStepper != null) {
            this.alphaStepper.deactivate();
        }
    }

    public float getLife() {
        return this.life;
    }

    public boolean isDead() {
        return this.life <= 0;
    }

    public void update(long currentTime) {

        float deltaT = currentTime - this.lastUpdate;

        float scaleStep = (endScale - startScale) / (float) this.lifeInMs;
        float alphaStep = (endAlpha - startAlpha) / (float) this.lifeInMs;

        // Update speed.
        this.velocity.x(this.velocity.x() + (deltaT / 1000.0f) * this.gravity.x());
        this.velocity.y(this.velocity.y() + (deltaT / 1000.0f) * this.gravity.y());
        this.velocity.z(this.velocity.z() + (deltaT / 1000.0f) * this.gravity.z());

        // Update position;
        this.position.x(this.position.x() + (deltaT / 1000.0f) * this.velocity.x());
        this.position.y(this.position.y() + (deltaT / 1000.0f) * this.velocity.y());
        this.position.z(this.position.z() + (deltaT / 1000.0f) * this.velocity.z());

        long diff = currentTime - this.startTime;

        this.life = 1 - ((float) diff) / this.lifeInMs;
        if (this.life > 1) {
            this.life = 1;
        }
        if (this.life < 0) {
            this.life = 0;
        }

        this.lastUpdate = currentTime;

        this.scale += deltaT * scaleStep;
        this.alpha += deltaT * alphaStep;

        if (this.scaleStepper != null) {
            this.scaleStepper.update(currentTime);
        }

        if (this.positionStepper != null) {
            this.positionStepper.update(currentTime);
        }

        if (this.rotationStepper != null) {
            this.rotationStepper.update(currentTime);
        }

        if (this.alphaStepper != null) {
            this.alphaStepper.update(currentTime);
        }
    }

    public float getScale() {
        /*if (this.scaleStepper != null) {
            return this.scaleStepper.getValue();
        } else {
            return this.scale;
        }*/
        return this.scale;
    }

    public Point3D getPosition() {
        if (this.positionStepper != null) {
            return this.positionStepper.getValue();
        } else {
            return this.position;
        }
    }

    public float getAlpha() {
        if (this.alphaStepper != null) {
            return this.alphaStepper.getValue();
        } else {
            return this.alpha;
        }
    }

    public void setPosition(Point3D position) {
        this.position = position.copy();
    }

    public void setVelocity(Vector3D velocity) {
        this.velocity = velocity.copy();
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public int getTextureIndex() {
        return this.textureIndex;
    }

    public void setRotationMatrix(RotationMatrix matrix) {
        this.rotationMatrix = matrix;
    }

    public RotationMatrix getRotationMatrix() {
        return this.rotationMatrix;
    }

    public void setScaleRange(float startScale, float endScale) {
        this.startScale = startScale;
        this.endScale = endScale;
    }

    public void setAlphaRange(float startAlpha, float endAlpha) {
        this.startAlpha = startAlpha;
        this.endAlpha = endAlpha;
    }

    public boolean isBillboard() {
        return this.isBillboard;
    }

    public boolean useAdditiveBlending() {
        return this.useAdditiveBlending;
    }

    public Texture getTexture() {
        return this.texture;
    }
}
