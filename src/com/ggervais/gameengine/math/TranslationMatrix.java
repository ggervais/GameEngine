package com.ggervais.gameengine.math;

public class TranslationMatrix extends Matrix4x4 {
	
	public TranslationMatrix() {
		super();
	}
	
	public TranslationMatrix(float dx, float dy, float dz) {
		this();
		setElement(1, 4, dx);
		setElement(2, 4, dy);
		setElement(3, 4, dz);
	}
	
	public static TranslationMatrix createFromXYZ(float x, float y, float z) {
		return new TranslationMatrix(x, y, z);
	}
	
	public void setTranslationX(float dx) {
		setElement(1, 4, dx);
	}
	
	public void setTranslationY(float dy) {
		setElement(2, 4, dy);
	}
	
	public void setTranslationZ(float dz) {
		setElement(3, 4, dz);
	}

    public Point3D getTranslation() {
        Point3D point = new Point3D();

        point.x(getElement(1, 4));
        point.y(getElement(2, 4));
        point.z(getElement(3, 4));

        return point;
    }
}
