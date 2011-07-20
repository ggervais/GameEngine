package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.scenegraph.renderstates.*;
import com.ggervais.gameengine.timing.Controller;
import com.sun.xml.internal.messaging.saaj.util.transform.EfficientStreamingTransformer;

import java.util.*;

public abstract class Spatial {

    protected Spatial parent;
    protected Transformation worldTransform;
    protected Transformation localTransform;
    Map<GlobalStateType, GlobalState> globalStates;
    private List<Controller> controllers;
    private Effect effect;

    public Spatial() {
        this.globalStates = new HashMap<GlobalStateType, GlobalState>();
        this.worldTransform = new Transformation();
        this.localTransform = new Transformation();
        this.controllers = new ArrayList<Controller>();
        this.effect = null; // Effect object is optional.
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public Effect getEffect() {
        return this.effect;
    }

    public void setParent(Spatial parent) {
        this.parent = parent;
    }

    public void updateControllers(long currentTime) {
        for (Controller controller : this.controllers) {
            controller.update(currentTime);
        }
    }

    public void updateGeometryState(long currentTime, boolean isInitiator) {
        updateWorldData(currentTime);
        updateWorldBound();
        if (isInitiator) {
            propagateBoundToRoot();
        }
    }

    public void updateBoundState() {
        updateWorldBound();
        propagateBoundToRoot();
    }

    public void updateRenderState() {
        updateRenderState(null);
    }

    public void updateRenderState(Map<GlobalStateType, Stack<GlobalState>> stateMap) {

        boolean isInitiator = (stateMap == null || (stateMap != null && stateMap.size() == 0));

        if (isInitiator) {
            stateMap = new HashMap<GlobalStateType, Stack<GlobalState>>();
            stateMap.put(GlobalStateType.ALPHA_BLENDING, new Stack<GlobalState>());
            stateMap.put(GlobalStateType.WIREFRAME, new Stack<GlobalState>());
            stateMap.put(GlobalStateType.ZBUFFER, new Stack<GlobalState>());

            stateMap.get(GlobalStateType.ALPHA_BLENDING).push(new AlphaBlendingState());
            stateMap.get(GlobalStateType.WIREFRAME).push(new WireframeState());
            stateMap.get(GlobalStateType.ZBUFFER).push(new ZBufferState());

            propagateStateFromRoot(stateMap);
        } else {
            pushState(stateMap);
        }

        updateState(stateMap);

        if (isInitiator) {
            stateMap.clear();
        } else {
            popState(stateMap);
        }
    }

    protected void pushState(Map<GlobalStateType, Stack<GlobalState>> stateMap) {
        Set<GlobalStateType> keys = this.globalStates.keySet();
        for (GlobalStateType stateType : keys) {
            stateMap.get(stateType).push(this.globalStates.get(stateType));
        }
    }

    protected void updateState(Map<GlobalStateType, Stack<GlobalState>> stateMap) {
        // This is implemented in the subclasses.
        // TODO: make Spatial abstract.
    }

    protected void popState(Map<GlobalStateType, Stack<GlobalState>> stateMap) {
        Set<GlobalStateType> keys = this.globalStates.keySet();
        for (GlobalStateType stateType : keys) {
            stateMap.get(stateType).pop();
        }
    }

    protected void propagateStateFromRoot(Map<GlobalStateType, Stack<GlobalState>> stateMap) {
        if (this.parent != null) {
            this.parent.propagateStateFromRoot(stateMap);
        }
        pushState(stateMap);
    }

    protected void updateWorldBound() {
        // TODO implement the method.
    }

    protected void propagateBoundToRoot() {
        if (this.parent != null) {
            this.parent.updateWorldBound();
            this.parent.propagateBoundToRoot();
        }
    }

    protected void updateWorldData(long currentTime) {
        updateControllers(currentTime);

        if (this.parent != null) {
            this.worldTransform = Transformation.product(this.parent.getWorldTransformation(), this.localTransform);
        } else {
            // TODO make a copy
            this.worldTransform = this.localTransform;
        }
    }

    public GlobalState getGlobalState(GlobalStateType type) {
        return this.globalStates.get(type);
    }

    public void addGlobalState(GlobalState state) {
        this.globalStates.put(state.getType(), state);
    }

    public void removeGlobalState(GlobalStateType type) {
        this.globalStates.remove(type);
    }

    public  void clearGlobalStates() {
        this.globalStates.clear();
    }

    public abstract void draw(SceneRenderer renderer);

    public void onDraw(SceneRenderer renderer) {
        boolean culled = false;
        if (!culled) {
            draw(renderer);
        }
    }

    public void setLocalTransformation(Transformation localTransform) {
        this.localTransform = localTransform;
    }

    public Transformation getWorldTransformation() {
        return this.worldTransform;
    }

    public void addController(Controller controller) {
        if (controller.getControlledObject() == null) {
            controller.setControllerObject(this);
        }
        this.controllers.add(controller);
    }

    public void removeController(Controller controller) {
        this.controllers.remove(controller);
    }

    public void clearControllers() {
        this.controllers.clear();
    }

    public List<Controller> getControllers() {
        return this.controllers;
    }
}