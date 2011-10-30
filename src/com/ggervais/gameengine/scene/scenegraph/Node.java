package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalStateType;
import com.ggervais.gameengine.scene.scenegraph.visitor.SpatialVisitor;

import java.util.*;

public class Node extends Spatial {
    protected List<Spatial> children;

    public Node() {
        this.children = new ArrayList<Spatial>();
    }

    public void addChild(Spatial child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void removeChild(Spatial child) {
        this.children.remove(child);
    }

    public void clearChildren() {
        this.children.clear();
    }

    @Override
    protected void updateWorldBound() {
        boolean isFirstChild = true;
        for (Spatial child : this.children) {
            if (child != null) {
                if (isFirstChild) {
                    this.boundingBox = child.getBoundingBox().copy();
                    isFirstChild = false;
                } else {
                    this.boundingBox.grow(child.getBoundingBox());
                }
            }
        }
    }

    @Override
    protected void updateState(Map<GlobalStateType, Stack<GlobalState>> stateMap) {
        for (Spatial child : this.children) {
            child.updateRenderState(stateMap);
        }
    }

    @Override
    public void updateWorldData(long currentTime) {
        super.updateWorldData(currentTime);

        for (Spatial child : this.children) {
            child.updateGeometryState(currentTime, false);
        }
    }

    @Override
    public void draw(SceneRenderer renderer) {

        for (Light light : this.lights) {
            if (light.isOn()) {
                renderer.enableLight(light);
            }
        }

        synchronized (getChildrenLock()) {
            for (Spatial child : this.children) {
                child.onDraw(renderer);
            }
        }

        renderer.drawBoundingBox(this.boundingBox, isPickedInCurrentUpdate());
    }

    public Object getChildrenLock() {
        return this.children;
    }

    public Iterator<Spatial> getChildrenIterator() {
        return this.children.iterator();
    }

    public int nbChildren() {
        return this.children.size();
    }

    @Override
    public void setPickedInCurrentUpdate(boolean value) {
        super.setPickedInCurrentUpdate(value);
        for (Spatial child : this.children) {
            child.setPickedInCurrentUpdate(value);
        }
    }

    @Override
    public void visit(SpatialVisitor visitor) {
        super.visit(visitor);;
        for (Spatial child : this.children) {
            child.visit(visitor);
        }
    }

    @Override
    public boolean doIntersectsWithUnderlyingGeometry(Spatial spatial) {

        // First check if the current node intersects the given spatial object.
        boolean intersects = super.doIntersectsWithUnderlyingGeometry(spatial);

        // If it does, reset the result (intersects = false), and check the children.
        if (intersects) {
            intersects = false;
            for (Spatial child : this.children) {
                if (child.intersectsWithUnderlyingGeometry(spatial)) {
                    intersects = true;
                    break;
                }
            }
        }

        return intersects;
    }
}
