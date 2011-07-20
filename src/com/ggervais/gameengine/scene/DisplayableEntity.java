package com.ggervais.gameengine.scene;

import com.ggervais.gameengine.geometry.Model;
import com.ggervais.gameengine.material.Material;
import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.ScaleMatrix;
import com.ggervais.gameengine.math.TranslationMatrix;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingSphere;
import com.ggervais.gameengine.render.DepthSortableEntity;

public class DisplayableEntity implements DepthSortableEntity {
	
	private Point3D position;
	private Vector3D scale;
	private Vector3D rotation;
	private Vector3D direction;
	private DisplayableEntity parent;
	private Texture texture;
    private Material material;
	
	private Model model;
	
	public DisplayableEntity(Model model) {
		this.model = model;
		this.setPosition(Point3D.zero());
		this.setScale(new Vector3D(1, 1, 1));
		this.setRotation(new Vector3D(0, 0, 0));
		this.setDirection(new Vector3D(1, 0, 0));
		this.parent = null;
	}
	
	public DisplayableEntity(Model model, DisplayableEntity parent) {
		this(model);
		this.parent = parent;
	}
	
	public Texture getTexture() {
		return this.texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
	
	public Matrix4x4 getWorldMatrix() {
		Matrix4x4 world = new Matrix4x4();
		
		RotationMatrix rotationMatrix = RotationMatrix.createFromXYZ(this.rotation.x(), this.rotation.y(), this.rotation.z());
		TranslationMatrix translationMatrix = TranslationMatrix.createFromXYZ(this.position.x(), this.position.y(), this.position.z());
		ScaleMatrix scaleMatrix = ScaleMatrix.createFromXYZ(this.scale.x(), this.scale.y(), this.scale.z());
		
		world.identity();

		world.mult(translationMatrix);
		world.mult(rotationMatrix);
		world.mult(scaleMatrix);
		
		if (this.parent != null) {
			Matrix4x4 parentWorld = this.parent.getWorldMatrix();
			world = Matrix4x4.mult(parentWorld, world);
		}
		
		return world;
	}

    public Matrix4x4 getWorldMatrix(RotationMatrix rotationMatrix) {
		Matrix4x4 world = new Matrix4x4();

		TranslationMatrix translationMatrix = TranslationMatrix.createFromXYZ(this.position.x(), this.position.y(), this.position.z());
		ScaleMatrix scaleMatrix = ScaleMatrix.createFromXYZ(this.scale.x(), this.scale.y(), this.scale.z());

		world.identity();

		world.mult(translationMatrix);
		world.mult(rotationMatrix);
		world.mult(scaleMatrix);

		if (this.parent != null) {
			Matrix4x4 parentWorld = this.parent.getWorldMatrix();
			world = Matrix4x4.mult(parentWorld, world);
		}

		return world;
	}
	
	private void setModel(Model model) {
		this.model = model;
	}
	
	public Model getModel() {
		return this.model;
	}

    public BoundingSphere getBoundingSphere() {
        return this.model.getBoundingSphere(getWorldMatrix());
    }

    public BoundingBox getBoundingBox() {
        return this.model.getBoundingBox(getWorldMatrix());
    }

	public void setPosition(Point3D position) {
		this.position = position;
	}

	public Point3D getPosition() {
		return position;
	}

	public void setScale(Vector3D scale) {
		this.scale = scale;
	}

	public Vector3D getScale() {
		return scale;
	}

	public void setDirection(Vector3D direction) {
		this.direction = direction;
	}

	public Vector3D getDirection() {
		return direction;
	}

	public void setRotation(Vector3D rotation) {
		this.rotation = rotation;
	}

	public Vector3D getRotation() {
		return rotation;
	}


}
