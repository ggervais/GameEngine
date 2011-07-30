package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.math.*;

public class Transformation {
    private RotationMatrix rotationMatrix;
    private TranslationMatrix translationMatrix;
    private ScaleMatrix scaleMatrix;
    private Matrix4x4 worldMatrix;

    private Vector3D rotation;

    public Transformation() {
        this.rotationMatrix = new RotationMatrix();
        this.translationMatrix = new TranslationMatrix();
        this.scaleMatrix = new ScaleMatrix();
        this.worldMatrix = new Matrix4x4();
        this.rotation = Vector3D.zero();
        updateWorldMatrix();
    }

    public static Transformation product(Transformation first, Transformation second) {
        Transformation workingTransformation = new Transformation();
        workingTransformation.product(first);
        workingTransformation.product(second);
        return workingTransformation;
    }

    // TODO make unit tests for this function.
    public void product(Transformation transformation) {
        this.worldMatrix.mult(transformation.getMatrix());
    }


    public void setRotation(float x, float y, float z) {
        this.rotationMatrix = RotationMatrix.createFromXYZ(x, y, z);
        this.rotation = new Vector3D(x, y, z);
        updateWorldMatrix();
    }

    public void setRotationMatrix(RotationMatrix matrix) {
        this.rotationMatrix = matrix;
        updateWorldMatrix();
    }

    public Vector3D getRotation() {
        return this.rotation;
    }

    public Vector3D getTranslation() {
        return new Vector3D(this.translationMatrix.getElement(1, 4), this.translationMatrix.getElement(2, 4), this.translationMatrix.getElement(3, 4));
    }

    public Vector3D getScale() {
        return new Vector3D(this.scaleMatrix.getElement(1, 1), this.scaleMatrix.getElement(2, 2), this.scaleMatrix.getElement(3, 3));
    }

    public void setTranslation(float x, float y, float z) {
        this.translationMatrix.setElement(1, 4, x);
        this.translationMatrix.setElement(2, 4, y);
        this.translationMatrix.setElement(3, 4, z);
        updateWorldMatrix();
    }

    public void setTranslation(Vector3D translation) {
        this.translationMatrix.setElement(1, 4, translation.x());
        this.translationMatrix.setElement(2, 4, translation.y());
        this.translationMatrix.setElement(3, 4, translation.z());
        updateWorldMatrix();
    }

    public void setScale(float x, float y, float z) {
        this.scaleMatrix.setElement(1, 1, x);
        this.scaleMatrix.setElement(2, 2, y);
        this.scaleMatrix.setElement(3, 3, z);
        updateWorldMatrix();
    }

    public void setScale(Vector3D scale) {
        this.scaleMatrix.setElement(1, 1, scale.x());
        this.scaleMatrix.setElement(2, 2, scale.y());
        this.scaleMatrix.setElement(3, 3, scale.z());
        updateWorldMatrix();
    }

    private void updateWorldMatrix() {
        this.worldMatrix = new Matrix4x4();
        this.worldMatrix.mult(this.translationMatrix);
        this.worldMatrix.mult(this.rotationMatrix);
        this.worldMatrix.mult(this.scaleMatrix);
    }

    public RotationMatrix getRotationMatrix() {
        return this.rotationMatrix;
    }

    public Matrix4x4 getMatrix() {
        return this.worldMatrix;
    }
}
