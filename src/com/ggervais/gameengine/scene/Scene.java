package com.ggervais.gameengine.scene;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Observable;

import com.ggervais.gameengine.UninitializedSubsystemException;
import com.ggervais.gameengine.geometry.*;
import com.ggervais.gameengine.input.InputSubsystem;
import com.ggervais.gameengine.material.Material;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.material.texture.Texture;
import com.ggervais.gameengine.material.texture.TextureLoader;
import com.ggervais.gameengine.particle.*;
import com.ggervais.gameengine.physics.MotionController;
import com.ggervais.gameengine.resource.ResourceSubsystem;
import com.ggervais.gameengine.scene.scenegraph.*;
import com.ggervais.gameengine.scene.scenegraph.renderstates.WireframeState;
import com.ggervais.gameengine.scene.scenegraph.renderstates.ZBufferState;
import com.ggervais.gameengine.scene.scenegraph.visitor.PauseVisitor;
import com.ggervais.gameengine.timing.FountainEmitterController;
import net.java.games.input.Component.Identifier.Key;
import org.apache.log4j.Logger;

public class Scene extends Observable {

    private static final Logger log = Logger.getLogger(Scene.class);

	private Camera camera;
	private List<DisplayableEntity> entities;
    private List<DisplayableEntity> particles;
	private List<Texture> textures;
    private Node sceneGraphRoot;
    private boolean isPreviousSpaceDown = false;
	
	public Scene() {
		this.entities = new ArrayList<DisplayableEntity>();
		this.particles = new ArrayList<DisplayableEntity>();
        this.textures = new ArrayList<Texture>();
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

        ResourceSubsystem resourceSubsystem = ResourceSubsystem.getInstance();
        resourceSubsystem.addResource(matGuillaume);
        resourceSubsystem.addResource(matGraffiti);
        resourceSubsystem.addResource(matTerrain);
        resourceSubsystem.addResource(matSmoke);
        resourceSubsystem.addResource(matDebris);
        resourceSubsystem.addResource(matFlare);
        resourceSubsystem.addResource(matShockwave);

		float fortyFiveDegrees = (float) Math.toRadians(45);

		DisplayableEntity sphere1 = new DisplayableEntity(new Sphere());
        sphere1.setRotation(new Vector3D(0, fortyFiveDegrees, 0));
        sphere1.setPosition(new Point3D(2, 2, 2));
		//this.entities.add(sphere1);
		
		DisplayableEntity cube1 = new DisplayableEntity(new Cube());
		cube1.setPosition(new Point3D(0, 0, -1));
		cube1.setScale(new Vector3D(1, 1, 1));
		cube1.setRotation(new Vector3D(0, 0, 0));
		cube1.setMaterial(matGuillaume);
		
		DisplayableEntity cube2 = new DisplayableEntity(new Cube(), cube1);
		cube2.setPosition(new Point3D(1, 1, -2));
		cube2.setScale(new Vector3D(1, 1, 1));
		cube2.setMaterial(matGraffiti);
		
		Grid gridModel = new Grid(50, 50);
		gridModel.elevateWithHeighMap(texTerrain);
		
		this.entities.add(cube1);
		this.entities.add(cube2);
	
		Terrain terrain = new Terrain(gridModel);
		terrain.setScale(new Vector3D(50, 50, 50));
		terrain.setPosition(new Point3D(0, 0, 0));
        terrain.setMaterial(matTerrain);

		//this.entities.add(terrain);

        DisplayableEntity quad1 = new DisplayableEntity(new Quad());
        quad1.setPosition(new Point3D(0, 0, 0));
        quad1.setMaterial(matSmoke);
        this.particles.add(quad1);

        DisplayableEntity quad2 = new DisplayableEntity(new Quad());
        quad2.setPosition(new Point3D(-0.25f, -0.25f, 0));
        quad2.setMaterial(matSmoke);
        this.particles.add(quad2);

        DisplayableEntity quad3 = new DisplayableEntity(new Quad());
        quad3.setPosition(new Point3D(0.25f, 0.25f, 0));
        quad3.setMaterial(matSmoke);
        this.particles.add(quad3);

        DisplayableEntity quad4 = new DisplayableEntity(new Quad());
        quad4.setPosition(new Point3D(-0.25f, 0.35f, 0));
        quad4.setMaterial(matSmoke);
        this.particles.add(quad4);

        FireEmitter fireEmitter1 = new FireEmitter(new Point3D(-5, 0, 0), 2);
        ParticleSubsystem.getInstance().addEmitter(fireEmitter1);

        FireEmitter fireEmitter2 = new FireEmitter(new Point3D(5, 0, 0), 1);
        ParticleSubsystem.getInstance().addEmitter(fireEmitter2);

        FireEmitter fireEmitter3 = new FireEmitter(new Point3D(10, 0, 0), 0.5f);
        ParticleSubsystem.getInstance().addEmitter(fireEmitter3);

        FireEmitter fireEmitter4 = new FireEmitter(new Point3D(12.5f, 0, 0), 0.25f);
        ParticleSubsystem.getInstance().addEmitter(fireEmitter4);

        //this.camera = new TerrainFollowingFreeFlyCamera(terrain);
		this.camera = new FreeFlyCamera();
        this.camera.setPosition(new Point3D(0, 0, 10));


        // SceneGraph initialization

        WireframeState wireframeStateOn = new WireframeState(true);
        WireframeState wireframeStateOff = new WireframeState(false);
        ZBufferState zBufferState = new ZBufferState(true);
        CubeGeometry gCube1 = new CubeGeometry();
        CubeGeometry gCube2 = new CubeGeometry();
        CubeGeometry gCube3 = new CubeGeometry();

        Transformation second = new Transformation();
        second.setTranslation(1, 0, 0);

        Transformation rootTransform = new Transformation();
        rootTransform.setTranslation(0, 0, 0);
        rootTransform.setScale(2, 1, 1);
        //rootTransform.setRotation(fortyFiveDegrees, fortyFiveDegrees * 3, 0);

        Transformation third = new Transformation();
        third.setTranslation(-2, 0, 0);
        third.setRotation(fortyFiveDegrees, 0, fortyFiveDegrees);
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
        firstCubeNode.setLocalTransformation(rootTransform);

        Node quadNode = new Node();

        QuadGeometry quad = new QuadGeometry();
        quadNode.addChild(quad);
        Effect fireEffect = new Effect();

        fireEffect.setColor(new Color(255, 127, 0, 255));
        fireEffect.addTexture(texSmoke);
        quad.setEffect(fireEffect);

        Transformation qt = new Transformation();
        qt.setTranslation(-1, -1, -1);
        qt.setRotation(0, 2 * fortyFiveDegrees, 0);
        QuadGeometry gQuad2 = new QuadGeometry();
        //quadNode.addChild(gQuad2);

        Effect fireEffect2 = new Effect();
        fireEffect2.setColor(new Color(255, 127, 0, 255));
        fireEffect2.addTexture(texSmoke);
        gQuad2.setEffect(fireEffect2);
        gQuad2.setLocalTransformation(qt);

        Effect effect = new Effect();
        effect.setColor(new Color(255, 0, 255, 255));
        effect.addTexture(texFlare);
        gCube3.setEffect(effect);
        gCube3.addGlobalState(zBufferState);
        gCube3.addController(new AlphaController(gCube3, System.currentTimeMillis(), 5, 0, 1));

        MotionController controller = new MotionController(new Vector3D(0, -9.81f/2, 0), 10f, fortyFiveDegrees, 0);
        firstCubeNode.addController(controller);

        //this.sceneGraphRoot.addChild(new CubeGeometry());
        this.sceneGraphRoot.addChild(firstCubeNode);
        this.sceneGraphRoot.addChild(gCube3);
        //this.sceneGraphRoot.addChild(quadNode);

        ParticleEmitterConfiguration configuration = new ParticleEmitterConfiguration();
        configuration.texture = texSmoke;
        configuration.color = new Vector3D(1, 0.5f, 1);
        configuration.startAlpha = 1;
        configuration.particleLifeTimeInMs = 1000;
        configuration.endAlpha = 0;
        configuration.delayBetweenLaunchInMs = 1000;
        FountainEmitterController feController = new FountainEmitterController(configuration, 1, fortyFiveDegrees, 1, 5);
        Node node = new BillboardNode(this.camera);
        //Node node = new Node();
        feController.setControllerObject(node);
        node.addController(feController);
        //this.sceneGraphRoot.addChild(node);
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
            //rot.x(rot.x() + (float) Math.toRadians(1.0f));
            //rot.y(rot.y() + (float) Math.toRadians(1.0f));
            //rot.z(rot.z() + (float) Math.toRadians(1.0f));
        }
        this.camera.update();
	}
	
	public void render() {
		setChanged();
		notifyObservers();
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public Camera getCamera() {
		return camera;
	}
}
