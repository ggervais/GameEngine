package com.ggervais.gameengine.particle;

import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;

public class ParticleEmitterConfiguration {

    // This is a pure data object, so every member is public.

    public Point3D position;
    public Vector3D rotation;
    public Vector3D gravity;
    public Vector3D color;

    public boolean useAdditiveBlending;
    public boolean createBillboards;
    public int particleLifeTimeInMs;
    public int delayBetweenLaunchInMs;
    public float startAlpha;
    public float endAlpha;
    public float startScale;
    public float endScale;

    public Texture texture;

    public ParticleEmitterConfiguration() {
        this.position = Point3D.zero();
        this.gravity = Vector3D.zero();
        this.rotation = Vector3D.zero();
        this.useAdditiveBlending = true;
        this.createBillboards = true;
        this.particleLifeTimeInMs = 1000;
        this.delayBetweenLaunchInMs = 1000;
        this.color = new Vector3D(1, 1, 1);
        this.startAlpha = 1;
        this.endAlpha = 1;
        this.startScale = 1;
        this.endScale = 1;
    }

    // Provide basic setters for Point3D and Vector3D objects (to simplify assignments).
    // Also provide setters for alpha and scale ranges (to simplify assignments).

    public void setPosition(float x, float y, float z) {
        this.position = new Point3D(x, y, z);
    }

    public void setGravity(float x, float y, float z) {
        this.gravity = new Vector3D(x, y, z);
    }

    public void setRotation(float x, float y, float z) {
        this.rotation = new Vector3D(x, y, z);
    }

    public void setColor(float r, float g, float b) {
        this.color = new Vector3D(r, g, b);
    }

    public void setAdditiveBlending(boolean use) {
        this.useAdditiveBlending = use;
    }

    public void setAlphaRange(float start, float end) {
        this.startAlpha = start;
        this.endAlpha = end;
    }

    public void setScaleRange(float start, float end) {
        this.startScale = start;
        this.endScale = end;
    }

    public int getNbTextureIndices() {
        return this.texture.getNbCellsWidth() * this.texture.getNbCellsHeight();
    }
}
