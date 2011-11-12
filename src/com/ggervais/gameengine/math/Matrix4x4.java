package com.ggervais.gameengine.math;

import org.apache.log4j.Logger;

public class Matrix4x4 {
    private static final Logger log = Logger.getLogger(Matrix4x4.class);
	private float elements[];

	public Matrix4x4() {
		this.elements = new float[16];
		identity();
	}

	public Matrix4x4(Matrix4x4 matrix) {
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				setElement(i, j, matrix.getElement(i, j));
			}
		}
	}

	public void identity() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int index = i * 4 + j;
				if (i == j) {
					this.elements[index] = 1;
				} else {
					this.elements[index] = 0;
				}
			}
		}
	}

    public static Matrix4x4 createFromFloatArray(float[] elements, boolean isColumnMajorMatrix) {
        Matrix4x4 matrix = null;
        if (elements.length >= 16) {
            matrix = new Matrix4x4();

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    int index = i * 4 + j;
                    if (isColumnMajorMatrix) {
                        matrix.setElement(j + 1, i + 1, elements[index]);
                    } else {
                        matrix.setElement(i + 1, j + 1, elements[index]);
                    }
                }
            }

        }
        return matrix;
    }

	public static Matrix4x4 createIdentity() {
		return new Matrix4x4();
	}

	public float getElement(int i, int j) { // i and j go from 1..N, as real matrices do.
		int index = ((i - 1) * 4 + (j - 1));
		return this.elements[index];
	}

	public void setElement(int i, int j, float element) { // i and j go from 1..N, as real matrices do.
		int index = ((i - 1) * 4 + (j - 1));
		this.elements[index] = element;
	}

	public void mult(Matrix4x4 other) {
		Matrix4x4 temp = Matrix4x4.mult(this, other);

		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				setElement(i, j, temp.getElement(i, j));
			}
		}
	}

	public static Matrix4x4 mult(Matrix4x4 a, Matrix4x4 b) {
		Matrix4x4 result = new Matrix4x4();

		// M11
		result.setElement(1, 1, a.getElement(1, 1) * b.getElement(1, 1) +
						   		a.getElement(1, 2) * b.getElement(2, 1) +
						   		a.getElement(1, 3) * b.getElement(3, 1) +
						   		a.getElement(1, 4) * b.getElement(4, 1));
		// M12
		result.setElement(1, 2, a.getElement(1, 1) * b.getElement(1, 2) +
		   						a.getElement(1, 2) * b.getElement(2, 2) +
		   						a.getElement(1, 3) * b.getElement(3, 2) +
		   						a.getElement(1, 4) * b.getElement(4, 2));
		// M13
		result.setElement(1, 3, a.getElement(1, 1) * b.getElement(1, 3) +
		   						a.getElement(1, 2) * b.getElement(2, 3) +
		   						a.getElement(1, 3) * b.getElement(3, 3) +
		   						a.getElement(1, 4) * b.getElement(4, 3));
		// M14
		result.setElement(1, 4, a.getElement(1, 1) * b.getElement(1, 4) +
		   						a.getElement(1, 2) * b.getElement(2, 4) +
		   						a.getElement(1, 3) * b.getElement(3, 4) +
		   						a.getElement(1, 4) * b.getElement(4, 4));

		// M21
		result.setElement(2, 1, a.getElement(2, 1) * b.getElement(1, 1) +
						   		a.getElement(2, 2) * b.getElement(2, 1) +
						   		a.getElement(2, 3) * b.getElement(3, 1) +
						   		a.getElement(2, 4) * b.getElement(4, 1));
		// M22
		result.setElement(2, 2, a.getElement(2, 1) * b.getElement(1, 2) +
		   						a.getElement(2, 2) * b.getElement(2, 2) +
		   						a.getElement(2, 3) * b.getElement(3, 2) +
		   						a.getElement(2, 4) * b.getElement(4, 2));
		// M23
		result.setElement(2, 3, a.getElement(2, 1) * b.getElement(1, 3) +
		   						a.getElement(2, 2) * b.getElement(2, 3) +
		   						a.getElement(2, 3) * b.getElement(3, 3) +
		   						a.getElement(2, 4) * b.getElement(4, 3));
		// M24
		result.setElement(2, 4, a.getElement(2, 1) * b.getElement(1, 4) +
		   						a.getElement(2, 2) * b.getElement(2, 4) +
		   						a.getElement(2, 3) * b.getElement(3, 4) +
		   						a.getElement(2, 4) * b.getElement(4, 4));

		// M31
		result.setElement(3, 1, a.getElement(3, 1) * b.getElement(1, 1) +
						   		a.getElement(3, 2) * b.getElement(2, 1) +
						   		a.getElement(3, 3) * b.getElement(3, 1) +
						   		a.getElement(3, 4) * b.getElement(4, 1));
		// M32
		result.setElement(3, 2, a.getElement(3, 1) * b.getElement(1, 2) +
		   						a.getElement(3, 2) * b.getElement(2, 2) +
		   						a.getElement(3, 3) * b.getElement(3, 2) +
		   						a.getElement(3, 4) * b.getElement(4, 2));
		// M33
		result.setElement(3, 3, a.getElement(3, 1) * b.getElement(1, 3) +
		   						a.getElement(3, 2) * b.getElement(2, 3) +
		   						a.getElement(3, 3) * b.getElement(3, 3) +
		   						a.getElement(3, 4) * b.getElement(4, 3));
		// M34
		result.setElement(3, 4, a.getElement(3, 1) * b.getElement(1, 4) +
		   						a.getElement(3, 2) * b.getElement(2, 4) +
		   						a.getElement(3, 3) * b.getElement(3, 4) +
		   						a.getElement(3, 4) * b.getElement(4, 4));

		// M41
		result.setElement(4, 1, a.getElement(4, 1) * b.getElement(1, 1) +
						   		a.getElement(4, 2) * b.getElement(2, 1) +
						   		a.getElement(4, 3) * b.getElement(3, 1) +
						   		a.getElement(4, 4) * b.getElement(4, 1));
		// M42
		result.setElement(4, 2, a.getElement(4, 1) * b.getElement(1, 2) +
		   						a.getElement(4, 2) * b.getElement(2, 2) +
		   						a.getElement(4, 3) * b.getElement(3, 2) +
		   						a.getElement(4, 4) * b.getElement(4, 2));
		// M43
		result.setElement(4, 3, a.getElement(4, 1) * b.getElement(1, 3) +
		   						a.getElement(4, 2) * b.getElement(2, 3) +
		   						a.getElement(4, 3) * b.getElement(3, 3) +
		   						a.getElement(4, 4) * b.getElement(4, 3));
		// M44
		result.setElement(4, 4, a.getElement(4, 1) * b.getElement(1, 4) +
		   						a.getElement(4, 2) * b.getElement(2, 4) +
		   						a.getElement(4, 3) * b.getElement(3, 4) +
		   						a.getElement(4, 4) * b.getElement(4, 4));

		return result;
	}

    public Vector3D mult(Vector3D vector) {
		float x = vector.x() * getElement(1, 1) + vector.y() * getElement(1, 2) + vector.z() * getElement(1, 3) + vector.w() * getElement(1, 4);
		float y = vector.x() * getElement(2, 1) + vector.y() * getElement(2, 2) + vector.z() * getElement(2, 3) + vector.w() * getElement(2, 4);
		float z = vector.x() * getElement(3, 1) + vector.y() * getElement(3, 2) + vector.z() * getElement(3, 3) + vector.w() * getElement(3, 4);
        float w = vector.x() * getElement(4, 1) + vector.y() * getElement(4, 2) + vector.z() * getElement(4, 3) + vector.w() * getElement(4, 4);
		return new Vector3D(x, y, z, w);
	}

	public Point3D mult(Point3D point) {
		float x = point.x() * getElement(1, 1) + point.y() * getElement(1, 2) + point.z() * getElement(1, 3) + point.w() * getElement(1, 4);
		float y = point.x() * getElement(2, 1) + point.y() * getElement(2, 2) + point.z() * getElement(2, 3) + point.w() * getElement(2, 4);
		float z = point.x() * getElement(3, 1) + point.y() * getElement(3, 2) + point.z() * getElement(3, 3) + point.w() * getElement(3, 4);
        float w = point.x() * getElement(4, 1) + point.y() * getElement(4, 2) + point.z() * getElement(4, 3) + point.w() * getElement(4, 4);
		return new Point3D(x, y, z, w);
	}

	public void mult(float k) {
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				setElement(i, j, getElement(i, j) * k);
			}
		}
	}

	public static Matrix4x4 mult(Matrix4x4 matrix, float k) {
		Matrix4x4 result = new Matrix4x4();
		for (int i = 1; i <= 4; i++) {
			for (int j = 1; j <= 4; j++) {
				result.setElement(i, j, matrix.getElement(i, j) * k);
			}
		}
		return result;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("");

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int index = i * 4 + j;
				sb.append(this.elements[index]);
				sb.append(j < 3 ? ", " : "");
			}
			sb.append("\n");
		}

		return sb.toString();
	}

	public float[] toRowMajorArray() {
		return this.elements;
	}

	public float[] toColumnMajorArray() {
		float[] matrixArray = new float[16];

		matrixArray[0]  = getElement(1, 1);
		matrixArray[1]  = getElement(2, 1);
		matrixArray[2]  = getElement(3, 1);
		matrixArray[3]  = getElement(4, 1);

		matrixArray[4]  = getElement(1, 2);
		matrixArray[5]  = getElement(2, 2);
		matrixArray[6]  = getElement(3, 2);
		matrixArray[7]  = getElement(4, 2);

		matrixArray[8]  = getElement(1, 3);
		matrixArray[9]  = getElement(2, 3);
		matrixArray[10] = getElement(3, 3);
		matrixArray[11] = getElement(4, 3);

		matrixArray[12] = getElement(1, 4);
		matrixArray[13] = getElement(2, 4);
		matrixArray[14] = getElement(3, 4);
		matrixArray[15] = getElement(4, 4);

		return matrixArray;
	}

    public Matrix4x4 transposed() {

        float[] c1 = new float[4];
        c1[0] = getElement(1, 1);
        c1[1] = getElement(1, 2);
        c1[2] = getElement(1, 3);
        c1[3] = getElement(1, 4);

        float[] c2 = new float[4];
        c2[0] = getElement(2, 1);
        c2[1] = getElement(2, 2);
        c2[2] = getElement(2, 3);
        c2[3] = getElement(2, 4);

        float[] c3 = new float[4];
        c3[0] = getElement(3, 1);
        c3[1] = getElement(3, 2);
        c3[2] = getElement(3, 3);
        c3[3] = getElement(3, 4);

        float[] c4 = new float[4];
        c4[0] = getElement(4, 1);
        c4[1] = getElement(4, 2);
        c4[2] = getElement(4, 3);
        c4[3] = getElement(4, 4);

        Matrix4x4 newMatrix = new Matrix4x4();

        newMatrix.setElement(1, 1, c1[0]);
        newMatrix.setElement(2, 1, c1[1]);
        newMatrix.setElement(3, 1, c1[2]);
        newMatrix.setElement(4, 1, c1[3]);

        newMatrix.setElement(1, 2, c2[0]);
        newMatrix.setElement(2, 2, c2[1]);
        newMatrix.setElement(3, 2, c2[2]);
        newMatrix.setElement(4, 2, c2[3]);

        newMatrix.setElement(1, 3, c3[0]);
        newMatrix.setElement(2, 3, c3[1]);
        newMatrix.setElement(3, 3, c3[2]);
        newMatrix.setElement(4, 3, c3[3]);

        newMatrix.setElement(1, 4, c4[0]);
        newMatrix.setElement(2, 4, c4[1]);
        newMatrix.setElement(3, 4, c4[2]);
        newMatrix.setElement(4, 4, c4[3]);

        return newMatrix;
    }

    public void transpose() {

        float[] c1 = new float[4];
        c1[0] = getElement(1, 1);
        c1[1] = getElement(1, 2);
        c1[2] = getElement(1, 3);
        c1[3] = getElement(1, 4);

        float[] c2 = new float[4];
        c2[0] = getElement(2, 1);
        c2[1] = getElement(2, 2);
        c2[2] = getElement(2, 3);
        c2[3] = getElement(2, 4);

        float[] c3 = new float[4];
        c3[0] = getElement(3, 1);
        c3[1] = getElement(3, 2);
        c3[2] = getElement(3, 3);
        c3[3] = getElement(3, 4);

        float[] c4 = new float[4];
        c4[0] = getElement(4, 1);
        c4[1] = getElement(4, 2);
        c4[2] = getElement(4, 3);
        c4[3] = getElement(4, 4);

        setElement(1, 1, c1[0]);
        setElement(2, 1, c1[1]);
        setElement(3, 1, c1[2]);
        setElement(4, 1, c1[3]);

        setElement(1, 2, c2[0]);
        setElement(2, 2, c2[1]);
        setElement(3, 2, c2[2]);
        setElement(4, 2, c2[3]);

        setElement(1, 3, c3[0]);
        setElement(2, 3, c3[1]);
        setElement(3, 3, c3[2]);
        setElement(4, 3, c3[3]);

        setElement(1, 4, c4[0]);
        setElement(2, 4, c4[1]);
        setElement(3, 4, c4[2]);
        setElement(4, 4, c4[3]);
    }

    public Matrix4x4 copy() {
        Matrix4x4 newMatrix = new Matrix4x4();
        for (int i = 1; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                newMatrix.setElement(i, j, getElement(i, j));
            }
        }
        return newMatrix;
    }

    public Matrix4x4 inverse() {
        float a11 = getElement(1, 1);
        float a12 = getElement(1, 2);
        float a13 = getElement(1, 3);
        float a14 = getElement(1, 4);
        float a21 = getElement(2, 1);
        float a22 = getElement(2, 2);
        float a23 = getElement(2, 3);
        float a24 = getElement(2, 4);
        float a31 = getElement(3, 1);
        float a32 = getElement(3, 2);
        float a33 = getElement(3, 3);
        float a34 = getElement(3, 4);
        float a41 = getElement(4, 1);
        float a42 = getElement(4, 2);
        float a43 = getElement(4, 3);
        float a44 = getElement(4, 4);

        float det = a11*a22*a33*a44 + a11*a23*a34*a42 + a11*a24*a32*a43
                  + a12*a21*a34*a43 + a12*a23*a31*a44 + a12*a24*a33*a41
                  + a13*a21*a32*a44 + a13*a22*a34*a41 + a13*a24*a31*a42
                  + a14*a21*a33*a42 + a14*a22*a31*a43 + a14*a23*a32*a41
                  - a11*a22*a34*a43 - a11*a23*a32*a44 - a11*a24*a33*a42
                  - a12*a21*a33*a44 - a12*a23*a34*a41 - a12*a24*a31*a43
                  - a13*a21*a34*a42 - a13*a22*a31*a44 - a13*a24*a32*a41
                  - a14*a21*a32*a43 - a14*a22*a33*a41 - a14*a23*a31*a42;

        Matrix4x4 inverse = new Matrix4x4();

        if (det == 0) {
            return null;
        }


        float b11 = a22*a33*a44 + a23*a34*a42 + a24*a32*a43 - a22*a34*a43 - a23*a32*a44 - a24*a33*a42;
        float b12 = a12*a34*a43 + a13*a32*a44 + a14*a33*a42 - a12*a33*a44 - a13*a34*a42 - a14*a32*a43;
        float b13 = a12*a23*a44 + a13*a24*a42 + a14*a22*a43 - a12*a24*a43 - a13*a22*a44 - a14*a23*a42;
        float b14 = a12*a24*a33 + a13*a22*a34 + a14*a23*a32 - a12*a23*a34 - a13*a24*a32 - a14*a22*a33;

        float b21 = a21*a34*a43 + a23*a31*a44 + a24*a33*a41 - a21*a33*a44 - a23*a34*a41 - a24*a31*a43;
        float b22 = a11*a33*a44 + a13*a34*a41 + a14*a31*a43 - a11*a34*a43 - a13*a31*a44 - a14*a33*a41;
        float b23 = a11*a24*a43 + a13*a21*a44 + a14*a23*a41 - a11*a23*a44 - a13*a24*a41 - a14*a21*a43;
        float b24 = a11*a23*a34 + a13*a24*a31 + a14*a21*a33 - a11*a24*a33 - a13*a21*a34 - a14*a23*a31;

        float b31 = a21*a32*a44 + a22*a34*a41 + a24*a31*a42 - a21*a34*a42 - a22*a31*a44 - a24*a32*a41;
        float b32 = a11*a34*a42 + a12*a31*a44 + a14*a32*a41 - a11*a32*a44 - a12*a34*a41 - a14*a31*a42;
        float b33 = a11*a22*a44 + a12*a24*a41 + a14*a21*a42 - a11*a24*a42 - a12*a21*a44 - a14*a22*a41;
        float b34 = a11*a24*a32 + a12*a21*a34 + a14*a22*a31 - a11*a22*a34 - a12*a24*a31 - a14*a21*a32;

        float b41 = a21*a33*a42 + a22*a31*a43 + a23*a32*a41 - a21*a32*a43 - a22*a33*a41 - a23*a31*a42;
        float b42 = a11*a32*a43 + a12*a33*a41 + a13*a31*a42 - a11*a33*a42 - a12*a31*a43 - a13*a32*a41;
        float b43 = a11*a23*a42 + a12*a21*a43 + a13*a22*a41 - a11*a22*a43 - a12*a23*a41 - a13*a21*a42;
        float b44 = a11*a22*a33 + a12*a23*a31 + a13*a21*a32 - a11*a23*a32 - a12*a21*a33 - a13*a22*a31;

        b11 *= (1 / det);
        b12 *= (1 / det);
        b13 *= (1 / det);
        b14 *= (1 / det);
        b21 *= (1 / det);
        b22 *= (1 / det);
        b23 *= (1 / det);
        b24 *= (1 / det);
        b31 *= (1 / det);
        b32 *= (1 / det);
        b33 *= (1 / det);
        b34 *= (1 / det);
        b41 *= (1 / det);
        b42 *= (1 / det);
        b43 *= (1 / det);
        b44 *= (1 / det);

        inverse.setElement(1, 1, b11);
        inverse.setElement(1, 2, b12);
        inverse.setElement(1, 3, b13);
        inverse.setElement(1, 4, b14);

        inverse.setElement(2, 1, b21);
        inverse.setElement(2, 2, b22);
        inverse.setElement(2, 3, b23);
        inverse.setElement(2, 4, b24);

        inverse.setElement(3, 1, b31);
        inverse.setElement(3, 2, b32);
        inverse.setElement(3, 3, b33);
        inverse.setElement(3, 4, b34);

        inverse.setElement(4, 1, b41);
        inverse.setElement(4, 2, b42);
        inverse.setElement(4, 3, b43);
        inverse.setElement(4, 4, b44);

        return inverse;
    }

    public static void main(String[] args) {
        Matrix4x4 testMatrix = new Matrix4x4();
        testMatrix.setElement(1, 1, 2);
        testMatrix.setElement(1, 2, 3);
        testMatrix.setElement(1, 3, 4);
        testMatrix.setElement(1, 4, 5);

        testMatrix.setElement(2, 1, 0);
        testMatrix.setElement(2, 2, -1);
        testMatrix.setElement(2, 3, 2);
        testMatrix.setElement(2, 4, 1);

        testMatrix.setElement(3, 1, 0);
        testMatrix.setElement(3, 2, 0);
        testMatrix.setElement(3, 3, 2);
        testMatrix.setElement(3, 4, 4);

        testMatrix.setElement(4, 1, 0);
        testMatrix.setElement(4, 2, 3);
        testMatrix.setElement(4, 3, -6);
        testMatrix.setElement(4, 4, 0);

        log.info(testMatrix.inverse());
    }

    public float[] getElements() {
        return this.elements;
    }
}
