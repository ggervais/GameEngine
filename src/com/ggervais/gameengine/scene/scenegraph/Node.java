package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.physics.collision.Collision;
import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.GlobalStateType;
import com.ggervais.gameengine.scene.scenegraph.visitor.SpatialVisitor;
import org.apache.log4j.Logger;

import java.util.*;

public class Node extends Spatial {

    private static final Logger log = Logger.getLogger(Node.class);

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
    public void updateWorldData(long currentTime, InputController inputController) {
        super.updateWorldData(currentTime, inputController);

        for (Spatial child : this.children) {
            child.updateGeometryState(currentTime, inputController, false);
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

        //renderer.drawBoundingBox(this.boundingBox, isPickedInCurrentUpdate());
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
        super.visit(visitor);
        for (Spatial child : this.children) {
            child.visit(visitor);
        }
    }

    @Override
    public List<Collision> doIntersectsWithUnderlyingGeometry(Spatial spatial) {

        // First check if the current node intersects the given spatial object.
        List<Collision> collisions = super.doIntersectsWithUnderlyingGeometry(spatial);

        // If it does, reset the result (collisions.clear()), and check the children.
        if (collisions.size() > 0) {
            collisions.clear();
            for (Spatial child : this.children) {
                collisions.addAll(child.intersectsWithUnderlyingGeometry(spatial));
                Iterator<Collision> collisionIterator = collisions.iterator();
                while(collisionIterator.hasNext()) {
                    Collision collision = collisionIterator.next();
                    if (collision.getFirst() == collision.getSecond() /*||
                        (collision.getFirst().getBoundingBox().getMinCorner().x() == 0 && collision.getFirst().getBoundingBox().getMinCorner().y() == 0 && collision.getFirst().getBoundingBox().getMinCorner().z() == 0 &&
                         collision.getFirst().getBoundingBox().getMaxCorner().x() == 0 && collision.getFirst().getBoundingBox().getMaxCorner().y() == 0 && collision.getFirst().getBoundingBox().getMaxCorner().z() == 0 &&
                         collision.getSecond().getBoundingBox().getMinCorner().x() == 0 && collision.getSecond().getBoundingBox().getMinCorner().y() == 0 && collision.getSecond().getBoundingBox().getMinCorner().z() == 0 &&
                         collision.getSecond().getBoundingBox().getMaxCorner().x() == 0 && collision.getSecond().getBoundingBox().getMaxCorner().y() == 0 && collision.getSecond().getBoundingBox().getMaxCorner().z() == 0)*/) {
                        collisionIterator.remove();
                    }
                }
            }
        }

        return collisions;
    }
}
