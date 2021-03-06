package com.ggervais.gameengine.scene;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Observable;

import com.ggervais.gameengine.geometry.*;
import com.ggervais.gameengine.geometry.loader.ObjFileLoader;
import com.ggervais.gameengine.geometry.loader.XFileLoader;
import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.material.Material;
import com.ggervais.gameengine.math.*;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.material.texture.TextureLoader;
import com.ggervais.gameengine.particle.*;
import com.ggervais.gameengine.physics.InputControlledController;
import com.ggervais.gameengine.physics.MotionController;
import com.ggervais.gameengine.render.Viewport;
import com.ggervais.gameengine.resource.ResourceSubsystem;
import com.ggervais.gameengine.scene.scenegraph.*;
import com.ggervais.gameengine.scene.scenegraph.renderstates.*;
import com.ggervais.gameengine.timing.BezierCurveController;
import org.apache.log4j.Logger;

public class Scene extends Observable {

    private static final Logger log = Logger.getLogger(Scene.class);

	private Camera camera;
	private List<DisplayableEntity> entities;
    private List<DisplayableEntity> particles;
	private List<Texture> textures;
    private Node sceneGraphRoot;
    private boolean isPreviousSpaceDown = false;
    private Viewport viewport;
    private Matrix4x4 modelViewMatrix;
    private Matrix4x4 projectionMatrix;
	
	public Scene() {
		this.entities = new ArrayList<DisplayableEntity>();
		this.particles = new ArrayList<DisplayableEntity>();
        this.textures = new ArrayList<Texture>();
        this.viewport = new Viewport(0, 0, 1440, 900);
        this.modelViewMatrix = Matrix4x4.createIdentity();
        this.projectionMatrix = Matrix4x4.createIdentity();
	}

    public Node getSceneGraphRoot() {
        return this.sceneGraphRoot;
    }
	
	public void addTexture(Texture texture) {
		this.textures.add(texture);
	}
	
	public void init() {

        Texture texGuillaume = TextureLoader.loadTexture("assets/textures/gg.jpg");
        Texture texGraffiti =  TextureLoader.loadTexture("assets/textures/textura_paralelipied.png");
        Texture texSmoke = TextureLoader.loadTexture("assets/textures/smoke.png", 2, 2);
        Texture texDebris = TextureLoader.loadTexture("assets/textures/debris.png", 3, 3);
        Texture texTerrain = TextureLoader.loadTexture("assets/textures/heightmap.jpg", true);
        Texture texFlare = TextureLoader.loadTexture("assets/textures/flare.png", 1, 3);
        Texture texShockwave = TextureLoader.loadTexture("assets/textures/shockwave.png");
        Texture texTitle = TextureLoader.loadTextAsTexture("Guillaume Gervais' Test Game Engine");
        Texture texAscii = TextureLoader.loadTexture("assets/textures/font.png");//TextureLoader.loadDefaultFontAsciiTexture();
        Texture texWarrior = TextureLoader.loadTexture("assets/textures/Warrior.jpg");
        Texture texCylinder = TextureLoader.loadTexture("assets/textures/cylinder.jpg");
        //Texture texAscii = TextureLoader.loadDefaultFontAsciiTexture();

        Material matGuillaume = new Material();
        matGuillaume.setName("gg");
        matGuillaume.addTexture(texGuillaume);

        Material matGraffiti = new Material();
        matGraffiti.setName("graf");
        matGraffiti.addTexture(texGraffiti);

        Material matTerrain = new Material();
        matTerrain.setName("terrain");
        matTerrain.addTexture(texTerrain);

        Material matSmoke = new Material();
        matSmoke.setName("smoke");
        matSmoke.addTexture(texSmoke);

        Material matDebris = new Material();
        matDebris.setName("debris");
        matDebris.addTexture(texDebris);

        Material matFlare = new Material();
        matFlare.setName("flare");
        matFlare.addTexture(texFlare);

        Material matShockwave = new Material();
        matShockwave.setName("shockwave");
        matShockwave.addTexture(texShockwave);

        Material matTitle = new Material();
        matTitle.setName("title");
        matTitle.addTexture(texTitle);

        Material matAscii = new Material();
        matAscii.setName("ascii");
        matAscii.addTexture(texAscii);

        Material matWarrior = new Material();
        matWarrior.setName("warrior");
        matWarrior.addTexture(texWarrior);

        Material matCylinder = new Material();
        matWarrior.setName("cylinder");
        matWarrior.addTexture(texCylinder);

        ResourceSubsystem resourceSubsystem = ResourceSubsystem.getInstance();
        resourceSubsystem.addResource(matGuillaume);
        resourceSubsystem.addResource(matGraffiti);
        resourceSubsystem.addResource(matTerrain);
        resourceSubsystem.addResource(matSmoke);
        resourceSubsystem.addResource(matDebris);
        resourceSubsystem.addResource(matFlare);
        resourceSubsystem.addResource(matShockwave);
        resourceSubsystem.addResource(matTitle);
        resourceSubsystem.addResource(matAscii);
        resourceSubsystem.addResource(matWarrior);
        resourceSubsystem.addResource(matCylinder);

		float fortyFiveDegrees = (float) Math.toRadians(45);


        // SceneGraph initialization


        GlobalState lightingOn = new LightingState(true);
        GlobalState lightingOff = new LightingState(false);
        WireframeState wireframeStateOn = new WireframeState(true);
        WireframeState wireframeStateOff = new WireframeState(false);
        ZBufferState zBufferState = new ZBufferState(true, true);
        CubeGeometry gCube1 = new CubeGeometry();
        CubeGeometry gCube2 = new CubeGeometry();
        CubeGeometry gCube3 = new CubeGeometry();


        Effect cubeEffect = new Effect();
        //cubeEffect.addTexture(texAscii);
       // cubeEffect.addTexture(texGraffiti);
        cubeEffect.addTexture(texGraffiti);
        Transformation gCube1Transformation = new Transformation();
        gCube1Transformation.setTranslation(0, 0, -5);
        gCube1.setLocalTransformation(gCube1Transformation);
        gCube1.setEffect(cubeEffect);

        Transformation second = new Transformation();
        second.setTranslation(1, 0, 0);

        Transformation rootTransform = new Transformation();
        rootTransform.setTranslation(0, 0, 0);
        rootTransform.setScale(2, 1, 1);
        //rootTransform.setRotation(fortyFiveDegrees, fortyFiveDegrees * 3, 0);

        Transformation third = new Transformation();
        third.setTranslation(-2, 0, 0);
        //third.setRotation(fortyFiveDegrees, 0, fortyFiveDegrees);
        gCube3.setLocalTransformation(third);

        this.sceneGraphRoot = new Node();
        //this.sceneGraphRoot.setLocalTransformation(rootTransform);

        Node firstCubeNode = new Node();
        Node secondCubeNode = new Node();
        secondCubeNode.addChild(gCube2);
        secondCubeNode.addGlobalState(wireframeStateOn);
        secondCubeNode.setLocalTransformation(second);

        firstCubeNode.addChild(gCube1);
        //firstCubeNode.addChild(secondCubeNode);
        //firstCubeNode.setLocalTransformation(rootTransform);

        Effect effect = new Effect();
        //effect.setColor(new Color(255, 0, 255, 255));
        effect.addTexture(texTitle);

        third.setScale(10, 1, 1);

        //gCube3.setEffect(effect);
        gCube3.addGlobalState(zBufferState);

        MotionController controller = new MotionController(new Vector3D(0, -9.81f/2f, 0), 10f, fortyFiveDegrees, 0, true);
        //firstCubeNode.addController(controller);

        Node fireNode = new Node();
        fireNode.addGlobalState(new AlphaBlendingState(true));
        fireNode.addGlobalState(new ZBufferState(true, false));

        Effect fireEffect = new Effect();
        fireEffect.setColor(new Color(255, 127, 0));
        fireEffect.addTexture(texSmoke);
        ParticlesGeometry fireParticles = new ParticlesGeometry(200, 0);
        fireParticles.setNbActive(0);
        fireParticles.setEffect(fireEffect);
        fireParticles.setCheckCollisionsWhenMoving(false);
        Transformation particlesTransformation = new Transformation();
        fireParticles.setLocalTransformation(particlesTransformation);
        ParticleController fireController = new ParticleController(new Vector3D(0, 9.81f, 0), 0, 0, 0, 850, 100, 0.75f, 0, 2, 7);


        Effect smokeEffect = new Effect();
        smokeEffect.setColor(new Color(77, 77, 77));
        smokeEffect.addTexture(texSmoke);
        ParticlesGeometry smokeParticles = new ParticlesGeometry(200, 0);
        smokeParticles.setNbActive(0);
        smokeParticles.setEffect(smokeEffect);
        smokeParticles.setCheckCollisionsWhenMoving(false);
        Transformation smokeTransformation = new Transformation();
        smokeTransformation.setTranslation(0, 1.5f * particlesTransformation.getScale().x(), 0);
        smokeParticles.setLocalTransformation(smokeTransformation);
        ParticleController smokeController = new ParticleController(new Vector3D(0, 9.81f, 0), 0, 0, 0, 1600, 100, 0.35f, 0, 2.5f, 7);


        fireNode.addChild(fireParticles);
        fireNode.addChild(smokeParticles);
        Transformation fireNodeTransformation = new Transformation();
        fireNodeTransformation.setTranslation(-10, 0, 0);
        fireNode.setLocalTransformation(fireNodeTransformation);

        CubeGeometry bezierCube = new CubeGeometry();
        Transformation bezierCubeTransformation = new Transformation();
        bezierCubeTransformation.setTranslation(0, 0, -10f);
        //bezierCubeTransformation.setScale(0.1f, 0.1f, 0.1f);
        bezierCube.setLocalTransformation(bezierCubeTransformation);
        BezierCurveController bezierCurveController = new BezierCurveController(1f);
        List<Point3D> controlPoints = new ArrayList<Point3D>();
        controlPoints.add(new Point3D(-2.5f, -2.5f, -10));
        controlPoints.add(new Point3D(2.5f, 2.5f, -10));
        controlPoints.add(new Point3D(4f, -8f, -15));
        BezierCurve bezierCurve = new BezierCurve(new Point3D(-5, 0, -10), new Point3D(5, 0, -10), controlPoints, 500);
        bezierCube.addGlobalState(lightingOn);
        bezierCurveController.setBezierCurve(bezierCurve);


        Transformation sphereTransformation = new Transformation();
        sphereTransformation.setTranslation(0, 0, 0);
        sphereTransformation.setRotation(0, fortyFiveDegrees, fortyFiveDegrees);
        Effect blueEffect = new Effect();
        blueEffect.setColor(new Color(0, 0, 255));
        SphereGeometry sphereGeometry = new SphereGeometry(50, 50);
        sphereGeometry.setEffect(blueEffect);
        sphereTransformation.setTranslation(0, 0, 0);
        sphereGeometry.setLocalTransformation(sphereTransformation);
        sphereGeometry.addGlobalState(lightingOn);

        Transformation immobileCubeTransformation = new Transformation();
        CubeGeometry immobileCube = new CubeGeometry();
        Effect immobileCubeEffect = new Effect();
        immobileCubeEffect.addTexture(texGuillaume);
        immobileCubeTransformation.setTranslation(20, 0, 0);
        immobileCube.setLocalTransformation(immobileCubeTransformation);
        immobileCube.setEffect(immobileCubeEffect);


        Effect colorEffect = new Effect();
        colorEffect.setColor(new Color(0, 255, 0));
        bezierCube.setEffect(colorEffect);

        Light light = new Light();
        Transformation lightTransformation = new Transformation();
        lightTransformation.setTranslation(-2, 0, 0);
        light.setLocalTransformation(lightTransformation);
        light.setType(LightType.SPOT);
        light.setSpotCutoff(10);
        light.setSpecular(new Color(255, 0, 0));

        sphereGeometry.addLight(light);

        Light light2 = new Light();
        light2.setSpecular(new Color(0, 255, 0));

        MotionController sphereController = new MotionController(Vector3D.zero(), 1, 0, 0);


        Geometry spaceship = ObjFileLoader.loadFile("assets/models/Spaceship.obj");
        Transformation spaceshipTransformation = new Transformation();
        spaceshipTransformation.setScale(0.25f, 0.25f, 0.25f);
        spaceshipTransformation.setTranslation(5, 0, 0);
        spaceship.setLocalTransformation(spaceshipTransformation);
        //spaceship.getEffect().addTexture(texSpaceship);
        spaceship.addGlobalState(lightingOn);

        log.info(this.sceneGraphRoot);
        Node lastNode = null;
        for (int i = 0; i < 10; i++) {
            Node cubeNode = new Node();
            CubeGeometry cube = new CubeGeometry();
            Transformation cubeTransformation = new Transformation();
            cubeTransformation.setTranslation(1, 1, -1);
            cubeNode.setLocalTransformation(cubeTransformation);
            cubeNode.addChild(cube);
            if (lastNode != null) {
                lastNode.addChild(cubeNode);
            }
            lastNode = cubeNode;
            if (i == 0) {
                //this.sceneGraphRoot.addChild(cubeNode);
            }
        }


        Spatial warrior = XFileLoader.loadFile("assets/models/warrior.x");
        Transformation warriorTransformation = new Transformation();
        warriorTransformation.setScale(0.1f);
        warrior.setLocalTransformation(warriorTransformation);
        warrior.addGlobalState(lightingOn);
        warrior.addController(new AnimationController());
        this.sceneGraphRoot.addChild(warrior);

        Spatial cylinder = XFileLoader.loadFile("assets/models/bouncy_thing.x");
        Transformation cylinderTransformation = new Transformation();
        cylinderTransformation.setScale(0.1f);
        cylinderTransformation.setTranslation(10, 0, 0);
        cylinder.setLocalTransformation(cylinderTransformation);
        cylinder.addGlobalState(lightingOn);
        cylinder.addController(new AnimationController(5000));
        this.sceneGraphRoot.addChild(cylinder);

        Spatial cochDanse = XFileLoader.loadFile("assets/models/cochdanse.x");
        Transformation cochDanseTransformation = new Transformation();
        cochDanseTransformation.setTranslation(-5, 0, 0);
        cochDanseTransformation.setRotation((float) Math.toRadians(90), (float) Math.toRadians(180), 0);
        cochDanseTransformation.setScale(0.1f);
        cochDanse.setLocalTransformation(cochDanseTransformation);
        cochDanse.addGlobalState(lightingOn);
        cochDanse.addController(new AnimationController(5000, true));
        this.sceneGraphRoot.addChild(cochDanse);


        //this.sceneGraphRoot.addChild(sphereGeometry);
        //this.sceneGraphRoot.addChild(gCube1);
        fireNode.addGlobalState(lightingOff);
        this.sceneGraphRoot.addChild(fireNode);
        this.sceneGraphRoot.addChild(bezierCube);
        this.sceneGraphRoot.addChild(spaceship);
        //this.sceneGraphRoot.addChild(immobileCube);

        /*Effect ef = new Effect();
        ef.addTexture(texGuillaume);
        Spatial cube = new CubeGeometry();
        Transformation temp = new Transformation();
        temp.setScale(10, 10, 10);
        cube.setLocalTransformation(temp);
        cube.setEffect(ef);
        this.sceneGraphRoot.addChild(cube);

        Effect ef2 = new Effect();
        ef2.addTexture(texGuillaume);
        Spatial anotherCube = new CubeGeometry();
        Transformation temp2 = new Transformation();
        temp2.setScale(10, 20, 10);
        temp2.setTranslation(10, 5, 0);
        anotherCube.setLocalTransformation(temp2);
        anotherCube.setEffect(ef2);
        this.sceneGraphRoot.addChild(anotherCube);          */

        gCube1.addController(controller);
        gCube3.addController(new AlphaController(gCube3, System.currentTimeMillis(), 5000, 0, 1));
        fireParticles.addController(fireController);
        smokeParticles.addController(smokeController);
        bezierCube.addController(bezierCurveController);
        sphereGeometry.addController(sphereController);
        this.sceneGraphRoot.addChild(sphereGeometry);
        //spaceship.addController(new InputControlledController());

        this.sceneGraphRoot.addChild(gCube1);

        //this.camera = new TerrainFollowingFreeFlyCamera(terrain);
		this.camera = new FreeFlyCamera();
        //this.camera.setPosition(new Point3D(0, 0, 10));
        //this.camera = new SpatialFollowingCamera(spaceship, new Vector3D(0, 0, 30));
    }
	
	public List<Texture> getTextures() {
		return this.textures;
	}
	
	public List<DisplayableEntity> getEntities() {
		return this.entities;
	}

    public List<DisplayableEntity> getParticles() {
		return this.particles;
	}
	
	public void update(long currentTime) {

        for (DisplayableEntity e : this.entities) {
            Vector3D rot = e.getRotation();
        }
	}
	
	public void render() {
		setChanged();
		notifyObservers();
	}

    public Ray getPickingRay(Camera camera) {
        Ray pickingRay = new Ray();

        Point3D nearPoint = Point3D.copy(this.viewport.getCenter());
        Point3D farPoint = Point3D.copy(this.viewport.getCenter());
        nearPoint.z(0);
        farPoint.z(1);

        Point3D transformedNearPoint = this.viewport.unproject(nearPoint, this.modelViewMatrix, this.projectionMatrix);
        Point3D transformedFarPoint = this.viewport.unproject(farPoint, this.modelViewMatrix, this.projectionMatrix);

        //log.info(transformedNearPoint + " " + transformedFarPoint);
        //log.info(transformedFarPoint.sub(transformedNearPoint).normalized());
        //log.info("=====");

        Point3D rayOrigin = transformedNearPoint.copy();
        Vector3D rayDirection = Point3D.sub(transformedFarPoint, transformedNearPoint).normalized();

        pickingRay.setOrigin(rayOrigin);
        pickingRay.setDirection(rayDirection);

        return pickingRay;
    }

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Camera getCamera() {
		return camera;
	}

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public Matrix4x4 getModelViewMatrix() {
        return modelViewMatrix;
    }

    public void setModelViewMatrix(Matrix4x4 modelViewMatrix) {
        this.modelViewMatrix = modelViewMatrix;
    }

    public Matrix4x4 getProjectionMatrix() {
        return projectionMatrix;
    }

    public void setProjectionMatrix(Matrix4x4 projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    public List<Plane> getFrustumPlanes() {
        return this.camera.getPlanes(this.viewport);
    }

    public List<Point3D> getFrustumPoints() {
        return this.camera.getFrustumPoints(this.viewport);
    }

    public boolean isPointInViewFrustum(Point3D point3D) {
        return this.camera.isPointInFrustum(this.viewport, point3D);
    }
}
