package com.ggervais.gameengine.math;

public class RotationMatrix extends Matrix4x4 {
	
	private float yaw;
	private float pitch;
	private float roll;
	
	public RotationMatrix() {
		super();
		this.yaw = 0;
		this.pitch = 0;
		this.roll = 0;
	}
	
	public RotationMatrix(float yaw, float pitch, float roll) {
		super();
		
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		
		updateMatrix();
	}
	
	public void setYawPitchRoll(float yaw, float pitch, float roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
		
		updateMatrix();
	}
	
	public void setYaw(float yaw) {
		this.yaw = yaw;
		updateMatrix();
	}
	
	public void setPitch(float pitch) {
		this.pitch = pitch;
		updateMatrix();
	}
	
	public void setRoll(float roll) {
		this.roll = roll;
		updateMatrix();
	}
	
	public static RotationMatrix createFromXYZ(float x, float y, float z) {
		return new RotationMatrix(y, x, z);
	}
	
	public static RotationMatrix createFromYawPitchRoll(float yaw, float pitch, float roll) {
		return new RotationMatrix(yaw, pitch, roll);
	}

    // Rotation matrix to apply to observee when looked by observer
    public static RotationMatrix createFromFacingPositions(Point3D observeePosition, Point3D observerPosition, Vector3D observerUp) {

        Vector3D look = Point3D.sub(observerPosition, observeePosition).normalized();
        Vector3D right = Vector3D.crossProduct(observerUp, look).normalized();
        Vector3D up = Vector3D.crossProduct(look, right).normalized();

        RotationMatrix rotation = RotationMatrix.createFromUpLookRight(up,  look, right);

        rotation.updateYawPitchRoll();

        return rotation;
    }

    public static RotationMatrix createFromUpLookRight(Vector3D up, Vector3D look, Vector3D right) {
        RotationMatrix matrix = new RotationMatrix();

        matrix.setElement(1, 1, right.x());
        matrix.setElement(2, 1, right.y());
        matrix.setElement(3, 1, right.z());

        matrix.setElement(1, 2, up.x());
        matrix.setElement(2, 2, up.y());
        matrix.setElement(3, 2, up.z());

        matrix.setElement(1, 3, look.x());
        matrix.setElement(2, 3, look.y());
        matrix.setElement(3, 3, look.z());

        return matrix;
    }
	
	public static RotationMatrix createFromAxisAndAngle(Vector3D axis, float angle) {
		// Algorithm from http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToMatrix/index.htm,
		// page viewed on May 10, 2011.
	
		RotationMatrix finalMatrix = new RotationMatrix();
		finalMatrix.identity();
		
		Vector3D normalizedAxis = axis.normalized();
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		float t = 1 - cos;
		float x = normalizedAxis.x();
		float y = normalizedAxis.y();
		float z = normalizedAxis.z();
		
		finalMatrix.setElement(1, 1, t*x*x + cos);
		finalMatrix.setElement(1, 2, t*x*y - z*sin);
		finalMatrix.setElement(1, 3, t*x*z + y*sin);
		
		finalMatrix.setElement(2, 1, t*x*y + z*sin);
		finalMatrix.setElement(2, 2, t*y*y + cos);
		finalMatrix.setElement(2, 3, t*y*z - x*sin);
		
		finalMatrix.setElement(3, 1, t*x*z - y*sin);
		finalMatrix.setElement(3, 2, t*y*z + x*sin);
		finalMatrix.setElement(3, 3, t*z*z + cos);

        finalMatrix.updateYawPitchRoll();

		return finalMatrix;
	}

    public Vector3D getYawPitchRoll() {
        return new Vector3D(this.yaw, this.pitch, this.roll);
    }

    public void updateYawPitchRoll() {

        if (Math.abs(getElement(2, 1)) == 1) {
            this.yaw = (float) Math.atan2(-getElement(1, 3), getElement(3, 3));
            this.roll = 0;
        } else {
            this.yaw = (float) Math.atan2(-getElement(3, 1), getElement(1, 1));
            this.roll = (float) Math.asin(getElement(2, 1));
        }

        this.pitch = (float) Math.atan2(-getElement(2, 3), getElement(2, 2));

    }

	private void updateMatrix() {
		
		Matrix4x4 yawMatrix = new Matrix4x4();
		Matrix4x4 pitchMatrix = new Matrix4x4();
		Matrix4x4 rollMatrix = new Matrix4x4();
		
		// Around Y (yaw)
		yawMatrix.setElement(1, 1, (float) Math.cos(this.yaw));
		yawMatrix.setElement(1, 3, (float) Math.sin(this.yaw));
		yawMatrix.setElement(3, 1, (float) -Math.sin(this.yaw));
		yawMatrix.setElement(3, 3, (float) Math.cos(this.yaw));
		
		// Around X (pitch)
		pitchMatrix.setElement(2, 2, (float) Math.cos(this.pitch));
		pitchMatrix.setElement(2, 3, (float) -Math.sin(this.pitch));
		pitchMatrix.setElement(3, 2, (float) Math.sin(this.pitch));
		pitchMatrix.setElement(3, 3, (float) Math.cos(this.pitch));
		
		// Around Z (roll)
		rollMatrix.setElement(1, 1, (float) Math.cos(this.roll));
		rollMatrix.setElement(1, 2, (float) -Math.sin(this.roll));
		rollMatrix.setElement(2, 1, (float) Math.sin(this.roll));
		rollMatrix.setElement(2, 2, (float) Math.cos(this.roll));
		

		identity();
		mult(yawMatrix);
		mult(pitchMatrix);
		mult(rollMatrix);
	}

    public static RotationMatrix createFromQuaternion(Quaternion originalQuaternion) {

        Quaternion quaternion = originalQuaternion.normalized();

        float x = quaternion.x();
        float y = quaternion.y();
        float z = quaternion.z();
        float w = quaternion.w();

        RotationMatrix matrix = new RotationMatrix();

        matrix.setElement(1, 1, (1 - 2*(y*y) - 2*(z*z)));
        matrix.setElement(1, 2, (2*x*y - 2*z*w));
        matrix.setElement(1, 3, (2*x*z + 2*y*w));

        matrix.setElement(2, 1, (2*x*y + 2*z*w));
        matrix.setElement(2, 2, (1 - 2*(x*x) - 2*(z*z)));
        matrix.setElement(2, 3, (2*y*z - 2*x*w));

        matrix.setElement(3, 1, (2*x*z - 2*y*w));
        matrix.setElement(3, 2, (2*y*z + 2*x*w));
        matrix.setElement(3, 3, (1 - 2*(x*x) - 2*(y*y)));

        return matrix;
    }
}
