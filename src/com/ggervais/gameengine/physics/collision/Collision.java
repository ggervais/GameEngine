package com.ggervais.gameengine.physics.collision;

import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.Entity;
import com.ggervais.gameengine.scene.scenegraph.Spatial;

public class Collision {

	private Spatial first;
	private Spatial second;
	Vector3D penetrationVector;
	
	public Collision(Spatial first, Spatial second, Vector3D penetrationVector) {
		this.first = first;
		this.second = second;
		this.penetrationVector = penetrationVector;
	}

    public Spatial getFirst() {
        return first;
    }

    public void setFirst(Spatial first) {
        this.first = first;
    }

    public Spatial getSecond() {
        return second;
    }

    public void setSecond(Spatial second) {
        this.second = second;
    }

    public Vector3D getPenetrationVector() {
        return penetrationVector;
    }

    public void setPenetrationVector(Vector3D penetrationVector) {
        this.penetrationVector = penetrationVector;
    }
}
