package com.ggervais.gameengine.render;

import com.ggervais.gameengine.scene.Camera;
import com.ggervais.gameengine.scene.DisplayableEntity;

import java.util.Comparator;

public class CameraDistanceComparator implements Comparator<DepthSortableEntity> {

    private Camera camera;

    public CameraDistanceComparator(Camera camera) {
        this.camera = camera;
    }

    public int compare(DepthSortableEntity first, DepthSortableEntity second) {
        float distanceFirst = first.getPosition().distance(this.camera.getPosition());
        float distanceSecond = second.getPosition().distance(this.camera.getPosition());
        return new Float(distanceSecond).compareTo(new Float(distanceFirst));
    }
}
