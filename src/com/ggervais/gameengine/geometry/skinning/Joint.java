package com.ggervais.gameengine.geometry.skinning;

import com.ggervais.gameengine.math.Point3D;

public class Joint {

    private Point3D position;
    private Joint parent;

    public Joint() {
        this(null, null);
    }

    public Joint(Point3D position, Joint parent) {
        this.position = position;
        this.parent = parent;
    }

    public Point3D getPosition() {

        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public Joint getParent() {
        return parent;
    }

    public void setParent(Joint parent) {
        this.parent = parent;
    }
}
