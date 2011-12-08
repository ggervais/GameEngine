package com.ggervais.gameengine.math;

public class ScaleMatrix extends Matrix4x4 {

	public ScaleMatrix() {
		super();
	}
	
	public ScaleMatrix(float sx, float sy, float sz) {
		this();
		setElement(1, 1, sx);
		setElement(2, 2, sy);
		setElement(3, 3, sz);
	}
	
	public static ScaleMatrix createFromXYZ(float x, float y, float z) {
		return new ScaleMatrix(x, y, z);
	}
	
	public void setScaleX(float sx) {
		setElement(1, 1, sx);
	}
	
	public void setScaleY(float sy) {
		setElement(2, 2, sy);
	}
	
	public void setScaleZ(float sz) {
		setElement(3, 3, sz);
	}

    public Point3D getScale() {
        Point3D point = new Point3D();
        point.x(getElement(1, 1));
        point.y(getElement(2, 2));
        point.z(getElement(3, 3));
        return point;
    }
}
