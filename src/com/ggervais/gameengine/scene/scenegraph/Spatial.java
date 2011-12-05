package com.ggervais.gameengine.scene.scenegraph;

import com.ggervais.gameengine.geometry.MeshGeometry;
import com.ggervais.gameengine.geometry.ParticlesGeometry;
import com.ggervais.gameengine.geometry.SphereGeometry;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.math.Plane;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingSphere;
import com.ggervais.gameengine.physics.collision.Collision;
import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.scene.scenegraph.renderstates.*;
import com.ggervais.gameengine.scene.scenegraph.visitor.SpatialVisitor;
import com.ggervais.gameengine.timing.Controller;

import java.util.*;
import org.apache.log4j.Logger;

public abstract class Spatial {

    private static final Logger log = Logger.getLogger(Spatial.class);
    protected Spatial parent;
    protected Transformation worldTransform;
    protected Transformation localTransform;
    Map<GlobalStateType, GlobalState> globalStates;
    private List<Controller> controllers;
    private Effect effect;
    protected BoundingBox boundingBox;
    protected BoundingSphere boundingSphere;
    private boolean pickedInCurrentUpdate; // TODO temporary code.
    protected List<Light> lights;
    private boolean checkCollisionsWhenMoving;

    public Spatial() {
        this.globalStates = new HashMap<GlobalStateType, GlobalState>();
        this.worldTransform = new Transformation();
        this.localTransform = new Transformation();
        this.controllers = new ArrayList<Controller>();
        this.effect = null; // Effect object is optional.
        this.boundingBox = new BoundingBox(Point3D.zero(), Point3D.zero());
        this.boundingSphere = new BoundingSphere(Point3D.zero(), 0);
        this.pickedInCurrentUpdate = false;
        this.lights = new ArrayList<Light>();
        checkCollisionsWhenMoving = true;
    }

    public Spatial getTopParent() {

        Spatial current = this;
        Spatial parent = current.getParent();

        while (parent != null) {
            current = parent;
            parent = current.getParent();
        }

        return current;
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public BoundingSphere getBoundingSphere() {
        return this.boundingSphere;
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

    public Spatial getParent() {
        return this.parent;
    }

    public void updateControllers(long currentTime, InputController inputController) {
        for (Controller controller : this.controllers) {
            controller.update(currentTime, inputController);
        }
    }

    public void updateGeometryState(long currentTime, InputController inputController, boolean isInitiator) {
        updateWorldData(currentTime, inputController);
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
            stateMap.put(GlobalStateType.LIGHTING, new Stack<GlobalState>());
            stateMap.put(GlobalStateType.WIREFRAME, new Stack<GlobalState>());
            stateMap.put(GlobalStateType.ZBUFFER, new Stack<GlobalState>());

            stateMap.get(GlobalStateType.ALPHA_BLENDING).push(new AlphaBlendingState());
            stateMap.get(GlobalStateType.LIGHTING).push(new LightingState());
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

    protected void updateWorldData(long currentTime, InputController inputController) {

        updateControllers(currentTime, inputController);

        if (this.parent != null) {
            this.worldTransform = Transformation.product(this.parent.getWorldTransformation(), this.localTransform);
        } else {
            // TODO make a copy
            this.worldTransform = this.localTransform;
        }

        for (Light light : this.lights) {
            light.updateWorldData(currentTime, inputController);
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

        List<Plane> planes = renderer.getScene().getFrustumPlanes();
        int offendedIndex = 0;
        Plane offendedPlane = null;
        List<Point3D> frustumPoints = renderer.getScene().getFrustumPoints();
        for (Plane plane : planes) {
            if (!getBoundingBox().intersectsOrIsInside(plane)) {
                culled = true;
                offendedPlane = plane;
                break;
            }
            offendedIndex++;
        }

        if (!culled) {
            //renderer.setWorldTransformations(this.localTransform);
            draw(renderer);
            //renderer.restoreWorldTransformations();
        }/* else {
            Spatial parent = getTopParent();
            if (parent == this) {
                log.info("Node is culled! i:" + offendedIndex + " p: " + offendedPlane + " bb: " + getBoundingBox());
                int i = 0;
                for (Point3D p : frustumPoints) {
                    log.info("Point #" + i + ": " + p);
                    i++;
                }
                log.info("======");
            }
        }*/
    }

    public Transformation getLocalTransformation() {
        return this.localTransform;
    }

    public void setLocalTransformation(Transformation localTransform) {
        this.localTransform = localTransform;
    }

    public Transformation getWorldTransformation() {
        return this.worldTransform;
    }

    public void addController(Controller controller) {
        if (controller.getControlledObject() == null) {
            controller.setControlledObject(this);
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

    // TODO temporary code.
    public boolean isPickedInCurrentUpdate() {
        return pickedInCurrentUpdate;
    }

    public void setPickedInCurrentUpdate(boolean pickedInCurrentUpdate) {
        this.pickedInCurrentUpdate = pickedInCurrentUpdate;
    }

    public void visit(SpatialVisitor visitor) {
        visitor.visit(this);
    }

    public void addLight(Light light) {
        if (light != null && this.lights.indexOf(light) == -1) {
            this.lights.add(light);
            light.setParent(this);
        }
    }

    public void removeLight(Light light) {
        if (light != null) {
            this.lights.remove(light);
        }
    }

    public void clearLights() {
        this.lights.clear();
    }

    public int getNbLights() {
        return this.lights.size();
    }

    public Light getLight(int i) {
        Light light = null;
        if (i >= 0 && i < this.lights.size()) {
            light = this.lights.get(i);
        }
        return light;
    }

    protected List<Collision> doIntersectsWithUnderlyingGeometry(Spatial spatial) {
        Vector3D penetrationVector = getBoundingBox().intersects(spatial.getBoundingBox());
        List<Collision> collisions = new ArrayList<Collision>();

        if (penetrationVector != null) {
            collisions.add(new Collision(this, spatial, penetrationVector));
        }

        return collisions;
    }

    public List<Collision> intersectsWithUnderlyingGeometry(Spatial spatial) {
        return spatial.doIntersectsWithUnderlyingGeometry(this);
    }

    public boolean isCheckCollisionsWhenMoving() {
        return checkCollisionsWhenMoving;
    }

    public void setCheckCollisionsWhenMoving(boolean checkCollisionsWhenMoving) {
        this.checkCollisionsWhenMoving = checkCollisionsWhenMoving;
    }
}
