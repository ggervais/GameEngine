package com.ggervais.gameengine.geometry.primitives;

import java.awt.Color;

import com.ggervais.gameengine.math.Point3D;

public class Vertex {
	private Point3D position;
	private Color color;
	private float textureU;
	private float textureV;
	
	public Vertex(Point3D position, Color color, float textureU, float textureV) {
		this.position = position;
		this.color = color;
		this.textureU = textureU;
		this.textureV = textureV;
	}
	
	public Vertex() {
		this(Point3D.zero(), Color.WHITE, 0, 0);
	}
	
	public void setPosition(Point3D position) {
		this.position = position;
	}
	public Point3D getPosition() {
		return position;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	public void setTextureU(float textureU) {
		this.textureU = textureU;
	}
	public float getTextureU() {
		return textureU;
	}
	public void setTextureV(float textureV) {
		this.textureV = textureV;
	}
	public float getTextureV() {
		return textureV;
	}
}
