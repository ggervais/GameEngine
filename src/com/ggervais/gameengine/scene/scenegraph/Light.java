package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.render.SceneRenderer;

import java.awt.*;

public class Light extends Spatial {

    private LightType type;
    private Color ambient;
    private Color diffuse;
    private Color specular;
    private float intensity;
    private float constant;
    private float linear;
    private float quadratic;
    private boolean attenuate;
    private boolean on;

    // For spot lights only
    private float exponent;
    private float angle;

    public Light() {
        this.type = LightType.AMBIENT;
        this.ambient = new Color(50, 50, 50, 255);
        this.diffuse = new Color(204, 204, 204, 255);
        this.specular = new Color(255, 255, 255, 255);
        this.intensity = 1;
        this.constant = 1;
        this.linear = 0;
        this.quadratic = 0;
        this.attenuate = false;
        this.on = true;
    }

    @Override
    public void draw(SceneRenderer renderer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public LightType getType() {
        return type;
    }

    public void setType(LightType type) {
        this.type = type;
    }

    public Color getAmbient() {
        return ambient;
    }

    public void setAmbient(Color ambient) {
        this.ambient = ambient;
    }

    public Color getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Color diffuse) {
        this.diffuse = diffuse;
    }

    public Color getSpecular() {
        return specular;
    }

    public void setSpecular(Color specular) {
        this.specular = specular;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getQuadratic() {
        return quadratic;
    }

    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }

    public boolean isAttenuate() {
        return attenuate;
    }

    public void setAttenuate(boolean attenuate) {
        this.attenuate = attenuate;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
