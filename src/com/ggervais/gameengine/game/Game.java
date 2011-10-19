package com.ggervais.gameengine.game;

import com.ggervais.gameengine.math.Matrix4x4;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Ray;
import com.ggervais.gameengine.math.Vector3D;
import com.ggervais.gameengine.particle.ParticleSubsystem;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.render.DisplaySubsystem;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import com.ggervais.gameengine.scene.scenegraph.Node;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.scene.scenegraph.visitor.PauseVisitor;
import net.java.games.input.*;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import com.ggervais.gameengine.UninitializedSubsystemException;
import com.ggervais.gameengine.resource.ResourceSubsystem;

import com.ggervais.gameengine.Subsystem;
import com.ggervais.gameengine.input.InputSubsystem;
import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.render.opengl.GLRendererFactory;
import com.ggervais.gameengine.scene.Scene;

public class Game {

    private static final Logger log = Logger.getLogger(Game.class);

	private Scene scene;
	private SceneRenderer renderer;
	private JFrame frame;
	private static final int QUANTUM = (int) (1000.0f / 60.0f); // About 16 or 17 milliseconds.
	private static int WIDTH = 1024;
	private static int HEIGHT = 768;
    private boolean isPreviousSpaceDown;
	
	private List<Subsystem> subsystems;


    public Game() {
		init();
	}
	
	public int getWidth() {
		return WIDTH;
	}
	
	public int getHeight() {
		return HEIGHT;
	}
	
	private void initGUI() {
		this.frame = new JFrame("Test Game Engine");
        this.frame.setSize(WIDTH, HEIGHT);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setUndecorated(false);
		this.frame.setFocusable(true);

        BufferedImage cursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "blank cursor");
        this.frame.getContentPane().setCursor(blankCursor);

		Component canvas = this.renderer.getCanvas();
		
		this.frame.add(canvas);
		
	}
	
	private void initSubsystems() {
		this.subsystems = new ArrayList<Subsystem>();
		
		// This should be injected using DI, by Spring or some other DI framework.

        this.subsystems.add(ResourceSubsystem.getInstance());
		this.subsystems.add(InputSubsystem.getInstance());
        this.subsystems.add(ParticleSubsystem.getInstance());
        this.subsystems.add(DisplaySubsystem.getInstance());
		
		for (Subsystem system: this.subsystems) {
			system.init();
		}
	}
	
	public void init() {
		this.scene = new Scene();
        this.renderer = new GLRendererFactory().buildRenderer(this.scene);
        this.scene.init();
        this.isPreviousSpaceDown = false;

        initSubsystems();
        initGUI();
    }
	
	public void start() {
		this.frame.setVisible(true);
		this.renderer.getCanvas().requestFocus();
		mainLoop();
	}

    public void pickCheck(Node nodeToCheck, Ray ray) {
        if (nodeToCheck.getBoundingBox().intersects(ray) != null) {

            nodeToCheck.setPickedInCurrentUpdate(true);

            Iterator<Spatial> children = nodeToCheck.getChildrenIterator();
            while(children.hasNext()) {
                Spatial child = children.next();
                if (child instanceof Geometry) {
                    BoundingBox box = ((Geometry) child).getBoundingBox().copy();
                    //box.transform(child.getWorldTransformation());
                    Point3D intersect = box.intersects(ray);
                    if (intersect != null) {
                        //log.info(child + " intersects with ray at " + intersect);
                        child.setPickedInCurrentUpdate(true);
                    } else {
                        child.setPickedInCurrentUpdate(false);
                    }
                } else if(child instanceof Node) {
                    pickCheck((Node) child, ray);
                }
            }
        } else {
            nodeToCheck.setPickedInCurrentUpdate(false);
        }

    }
	
	public void update(long currentTime) {
		
		for (Subsystem system: this.subsystems) {
            try {
			    system.update(currentTime);
            } catch (UninitializedSubsystemException use) {
                log.fatal("Subsystem '" + system.getClass().getName() + "' is not initialized!");
                System.exit(-1);
            }

		}
        //DisplaySubsystem.getInstance().getPickingRay(this.scene.getCamera());
        //log.info(DisplaySubsystem.getInstance().getPickingRay(this.scene.getCamera()));

        try {
            boolean isSpaceDown = InputSubsystem.getInstance().isKeyDown(net.java.games.input.Component.Identifier.Key.SPACE);
            if (this.isPreviousSpaceDown && !isSpaceDown) {
                this.scene.getSceneGraphRoot().visit(new PauseVisitor(currentTime));
            }
            this.isPreviousSpaceDown = isSpaceDown;
        } catch(UninitializedSubsystemException use) {
            log.fatal("InputSubsystem is uninitialized!");
        }

        this.scene.update(currentTime);
        Node root = this.scene.getSceneGraphRoot();
        root.updateGeometryState(currentTime, true);
        root.updateRenderState();

        pickCheck(root, this.scene.getPickingRay(this.scene.getCamera()));
	}
	
	private void mainLoop() {
		long currentTime = System.currentTimeMillis();
		long previousTime = currentTime;

        // TODO: devise a better timing algorithm.
        boolean done = false;
		while (!done) {
			currentTime = System.currentTimeMillis();
            long timeDifference = currentTime - previousTime;
			if (timeDifference > QUANTUM) {
                update(currentTime);
                try {
                    //this.renderer.drawSceneGraph(this.scene.getSceneGraphRoot());
                    this.scene.render();
                } catch (Exception e) {
                    log.error("And error occured: " + e.getMessage());
                }
				previousTime = currentTime;
			} else {
				long sleepTime = QUANTUM - timeDifference;
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException ie) {
					
				}
			}
		}
	}
	
	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Scene getScene() {
		return scene;
	}
}
