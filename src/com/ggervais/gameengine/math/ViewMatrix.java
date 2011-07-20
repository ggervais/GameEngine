package com.ggervais.gameengine.math;

import com.ggervais.gameengine.scene.Camera;

public class ViewMatrix extends Matrix4x4 {
    public ViewMatrix() {
        super();
    }

    public static ViewMatrix createFromCamera(Camera camera) {
        ViewMatrix matrix = new ViewMatrix();
        Point3D position = camera.getPosition();
        Vector3D up = camera.getUp();
        Vector3D look = Point3D.sub(position, camera.getLookAt());
        Vector3D right = Vector3D.crossProduct(up, look).normalized();

        Vector3D positionVector = new Vector3D(position.x(), position.y(), position.z());
        matrix.setElement(1, 1, right.x());
        matrix.setElement(2, 1, right.y());
        matrix.setElement(3, 1, right.z());
        matrix.setElement(4, 1, -Vector3D.dotProduct(positionVector, right));

        matrix.setElement(1, 2, up.x());
        matrix.setElement(2, 2, up.y());
        matrix.setElement(3, 2, up.z());
        matrix.setElement(4, 2, -Vector3D.dotProduct(positionVector, up));

        matrix.setElement(1, 3, look.x());
        matrix.setElement(2, 3, look.y());
        matrix.setElement(3, 3, look.z());
        matrix.setElement(4, 3, -Vector3D.dotProduct(positionVector, look));

        return matrix;
    }
}
