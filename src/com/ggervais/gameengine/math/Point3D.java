package com.ggervais.gameengine.math;

public class Point3D {
	private float x;
	private float y;
	private float z;
    private float w;
	
	public Point3D() {
		this(0, 0, 0);
	}
	
	public Point3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
        this.w = 1;
	}

    public Point3D(float x, float y, float z, float w) {
        this(x, y, z);
        w(w);
    }
	
	public static Point3D zero() {
		return new Point3D(0.0f, 0.0f, 0.0f);
	}
	
	public static Point3D add(Point3D p, Vector3D v) {
		return new Point3D(p.x() + v.x(), p.y() + v.y(), p.z() + v.z());
	}
	
	public void add(Vector3D v) {
		x(x() + v.x());
		y(y() + v.y());
		z(z() + v.z());
	}
	
	public static Point3D sub(Point3D p, Vector3D v) {
		return new Point3D(p.x() - v.x(), p.y() - v.y(), p.z() - v.z());
	}
	
	public void sub(Vector3D v) {
		x(x() - v.x());
		y(y() - v.y());
		z(z() - v.z());
	}
	
	public static Vector3D sub(Point3D p1, Point3D p2) {
		return new Vector3D(p1.x() - p2.x(), p1.y() - p2.y(), p1.z() - p2.z());
	}
	
	public Vector3D sub(Point3D p) {
		return new Vector3D(x() - p.x(), y() - p.y(), z() - p.z());
	}
	
	public float distance(Point3D p) {
		return sub(p).length();
	}
	
	public static float distance(Point3D p1, Point3D p2) {
		return Point3D.sub(p1, p2).length();
	}
	
	public float x() { return this.x; }
	public float y() { return this.y; }
	public float z() { return this.z; }
    public float w() { return this.w; }

	public void x(float x) { this.x = x; }
	public void y(float y) { this.y = y; }
	public void z(float z) { this.z = z; }
    public void w(float w) { this.w = w; }
	
	public String toString() { return "(" + x() + ", " + y() + ", " + z() + ")"; }


    public boolean equals(Point3D point) {
        return this.x == point.x() && this.y == point.y() && this.z()  == point.z();
    }

    public Point3D copy() {
        return new Point3D(this.x, this.y, this.z);
    }

    public static Point3D copy(Point3D point) {
        return point.copy();
    }

    public float get(int index) {
        switch(index) {
            case 0: return x();
            case 1: return y();
            case 2: return z();
        }

        return 0;
    }

    public void set(int index, float value) {
        switch (index) {
            case 0: x(value); break;
            case 1: y(value); break;
            case 2: z(value); break;
        }
    }

    public static Point3D lerp(Point3D firstPoint, Point3D secondPoint, float ratio) {
        float t = MathUtils.clamp(ratio, 0, 1);
        Vector3D diff = secondPoint.sub(firstPoint);
        return Point3D.add(firstPoint, diff.multiplied(t));
    }
}
