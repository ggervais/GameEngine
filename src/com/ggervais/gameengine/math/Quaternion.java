package com.ggervais.gameengine.math;

public class Quaternion {
    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion() {
        this(0, 0, 0, 1);
    }

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Quaternion createFromRotationMatrix(RotationMatrix matrix) {

        Quaternion quaternion = new Quaternion();

        float m11 = matrix.getElement(1, 1);
        float m22 = matrix.getElement(2, 2);
        float m33 = matrix.getElement(3, 3);

        float m32 = matrix.getElement(3, 2);
        float m23 = matrix.getElement(2, 3);

        float m13 = matrix.getElement(1, 3);
        float m31 = matrix.getElement(3, 1);

        float m21 = matrix.getElement(2, 1);
        float m12 = matrix.getElement(1, 2);

        float trace = m11 + m22 + m33;

        if (trace > 0) {
            float s = 2 * (float) Math.sqrt(trace + 1);
            quaternion.w(0.25f * s);
            quaternion.x((m32 - m23) / s);
            quaternion.y((m13 - m31) / s);
            quaternion.z((m21 - m12) / s);
        } else if (m11 > m22 && m11 > m33) {
            float s = 2 * (float) Math.sqrt(1 + m11 - m22 - m33);
            quaternion.w((m32 - m23) / s);
            quaternion.x(0.25f * s);
            quaternion.y((m21 + m12) / s);
            quaternion.z((m13 + m31) / s);
        } else if (m22 > m33) {
            float s = 2 * (float) Math.sqrt(1 + m22 - m11 - m33);
            quaternion.w((m13 - m31) / s);
            quaternion.x((m12 + m21) / s);
            quaternion.y(0.25f * s);
            quaternion.z((m23 + m32) / s);
        } else {
            float s = 2 * (float) Math.sqrt(1 + m33 - m11 - m22);
            quaternion.w((m21 - m12) / s);
            quaternion.x((m13 + m31) / s);
            quaternion.y((m23 + m32) / s);
            quaternion.z(0.25f * s);
        }

        return quaternion;
    }

    public float x() {
        return x;
    }

    public void x(float x) {
        this.x = x;
    }

    public float y() {
        return y;
    }

    public void y(float y) {
        this.y = y;
    }

    public float z() {
        return z;
    }

    public void z(float z) {
        this.z = z;
    }

    public float w() {
        return w;
    }

    public void w(float w) {
        this.w = w;
    }

    public void normalize() {
        float square = (float) Math.sqrt(x*x + y*y + z*z + w*w);
        x(x() / square);
        y(y() / square);
        z(z() / square);
        w(w() / square);
    }

    public Quaternion normalized() {
        Quaternion quaternion = new Quaternion(x(), y(), z(), w());
        quaternion.normalize();
        return quaternion;
    }

    public static Quaternion slerp(Quaternion firstQuaternion, Quaternion secondQuaternion, float ratio) {
        Quaternion qa = firstQuaternion.normalized();
        Quaternion qb = secondQuaternion.normalized();
        float t = MathUtils.clamp(ratio, 0, 1);

        Quaternion qm = new Quaternion();

        float cosHalfTheta = qa.x()*qb.x() + qa.y()*qb.y() + qa.z()*qb.z() + qa.w()*qb.w();
        if (Math.abs(cosHalfTheta) >= 1.0f) {
            qm.x(qa.x());
            qm.y(qa.y());
            qm.z(qa.z());
            qm.w(qa.w());
        } else {
            float halfTheta = (float) Math.acos(cosHalfTheta);
            float sinHalfTheta = (float) Math.sqrt(1 - cosHalfTheta*cosHalfTheta);
            if (Math.abs(sinHalfTheta) < 0.001f) {
                qm.x(qa.x()*0.5f + qb.x()*0.5f);
                qm.y(qa.y()*0.5f + qb.y()*0.5f);
                qm.z(qa.z()*0.5f + qb.z()*0.5f);
                qm.w(qa.w()*0.5f + qb.w()*0.5f);
            } else {
                float ratioA = (float) Math.sin((1 - t) * halfTheta) / sinHalfTheta;
                float ratioB = (float) Math.sin(t * halfTheta) / sinHalfTheta;
                qm.x(qa.x() * ratioA + qb.x() * ratioB);
                qm.y(qa.y() * ratioA + qb.y() * ratioB);
                qm.z(qa.z() * ratioA + qb.z() * ratioB);
                qm.w(qa.w() * ratioA + qb.w() * ratioB);
            }
        }

        return qm;
    }
}
