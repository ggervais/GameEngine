package com.ggervais.gameengine.scene.scenegraph.visitor;

import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.timing.Controller;

public class PauseVisitor implements SpatialVisitor {

    private long visitTime;

    public PauseVisitor(long currentTime) {
        this.visitTime = currentTime;
    }

    public void visit(Spatial spatial) {
        for (Controller controller : spatial.getControllers()) {
            if (controller.isPaused()) {
                controller.unpause(this.visitTime);
            } else {
                controller.pause(this.visitTime);
            }
        }
    }

    public void setVisitTime(long visitTime) {
        this.visitTime = visitTime;
    }
}
