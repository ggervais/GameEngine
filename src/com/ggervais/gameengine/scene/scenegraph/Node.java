package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalStateType;

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
        synchronized (getChildrenLock()) {
            for (Spatial child : this.children) {
                child.onDraw(renderer);
            }
        }
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
}
