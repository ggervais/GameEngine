package com.ggervais.gameengine.physics.collision;

import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.Entity;

public class Collision {
	private Entity first;
	private Entity second;
	Vector3D penetrationVector;
	
	public Collision(Entity first, Entity second, Vector3D vector) {
		this.first = first;
		this.second = second;
		this.penetrationVector = vector;
	}
}
