package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.RotationMatrix;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.scene.Camera;

public class BillboardNode extends Node {

    private Camera camera;

    public BillboardNode(Camera camera) {
        super();
        this.camera = camera;
    }

    @Override
    public void updateWorldData(long currentTime) {
        // First call will propagate parent's world transformation to current node.
        super.updateWorldData(currentTime);

        Vector3D translation = this.worldTransform.getTranslation();
        Point3D position = new Point3D(translation.x(), translation.y(), translation.z());
        RotationMatrix rotationMatrix = RotationMatrix.createFromFacingPositions(position, this.camera.getPosition(), this.camera.getUp());

        Transformation cameraOrientedTransformation = new Transformation();
        cameraOrientedTransformation.setRotationMatrix(rotationMatrix);

        this.worldTransform = Transformation.product(this.worldTransform, cameraOrientedTransformation);

        // This call will update children.
        // TODO this is code duplication: remove it. Ideas: boolean flag.
        for (Spatial child : this.children) {
            child.updateGeometryState(currentTime, false);
        }
    }
}
