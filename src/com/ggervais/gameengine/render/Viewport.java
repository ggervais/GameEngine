package com.ggervais.gameengine.render;

import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;

public class Viewport {

    private float x;
    private float y;
    private float width;
    private float height;

    public Viewport() {
        this(0, 0, 0, 0);
    }

    public Viewport(float x, float y, float width, float height) {
        setBounds(x, y, width, height);
    }

    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public Point3D getCenter() {
        return new Point3D((this.x + this.width) / 2, (this.y + this.height) / 2, 0);
    }

    public Point3D unproject(Point3D point, Matrix4x4 modelViewMatrix, Matrix4x4 projectionMatrix) {
        Point3D result = Point3D.zero();

        Point3D normalizedCoordinates = Point3D.zero();
        normalizedCoordinates.x(2 * (point.x()  - this.x) / this.width - 1);
        normalizedCoordinates.y(2 * (point.y()  - this.y) / this.height - 1);
        normalizedCoordinates.z(2 * (point.z()) - 1);

        //log.info(point + " -> " + normalizedCoordinates);

        Matrix4x4 modelViewMultipliedByProjection = Matrix4x4.mult(projectionMatrix, modelViewMatrix);
        Matrix4x4 inverse = modelViewMultipliedByProjection.inverse();

        result = inverse.mult(normalizedCoordinates);
        result.w(1.0f / result.w());
        result.x(result.x() * result.w());
        result.y(result.y() * result.w());
        result.z(result.z() * result.w());

        return result;
    }

    public String toString() {
        return new Point3D(this.x, this.y, 0) + " -> " + new Vector3D(this.width, this.height, 0);
    }
}
