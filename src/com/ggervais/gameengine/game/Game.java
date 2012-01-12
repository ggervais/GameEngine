package com.ggervais.gameengine.game;

import com.ggervais.gameengine.input.InputController;
import com.ggervais.gameengine.math.Point3D;
import com.ggervais.gameengine.math.Ray;
import com.ggervais.gameengine.particle.ParticleSubsystem;
import com.ggervais.gameengine.physics.boundingvolumes.BoundingBox;
import com.ggervais.gameengine.render.DisplaySubsystem;
import com.ggervais.gameengine.scene.scenegraph.Geometry;
import com.ggervais.gameengine.scene.scenegraph.Node;
import com.ggervais.gameengine.scene.scenegraph.Spatial;
import com.ggervais.gameengine.scene.scenegraph.visitor.PauseVisitor;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import com.ggervais.gameengine.UninitializedSubsystemException;
import com.ggervais.gameengine.resource.ResourceSubsystem;

import com.ggervais.gameengine.Subsystem;
import com.ggervais.gameengine.render.SceneRenderer;
import com.ggervais.gameengine.render.opengl.GLRendererFactory;
import com.ggervais.gameengine.scene.Scene;

public class Game {

    private static final Logger log = Logger.getLogger(Game.class);

	private Scene scene;
	private SceneRenderer renderer;
	private JFrame frame;
	private static final int QUANTUM = (int) (1000.0f / 60.0f); // About 16 or 17 milliseconds.
	private static int WIDTH = 1140;
	private static int HEIGHT = 900;
    private boolean isPreviousSpaceDown;
    private boolean isPreviousEscapeDown;
    private boolean isControlled;
    private InputController inputController;
    Cursor blankCursor;
	
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


        setDefaultCursor();

		Component canvas = this.renderer.getCanvas();
        log.info("Window size = " + WIDTH + "x" + HEIGHT);

		this.frame.add(canvas);
		
	}

    private void setBlankCursor() {
        if (this.blankCursor == null) {
            BufferedImage cursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            this.blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "blank cursor");
        }
        this.frame.getContentPane().setCursor(this.blankCursor);
    }

    private void setDefaultCursor() {
        this.frame.getContentPane().setCursor(Cursor.getDefaultCursor());
    }

	private void initSubsystems() {
		this.subsystems = new ArrayList<Subsystem>();
		
		// This should be injected using DI, by Spring or some other DI framework.

        this.subsystems.add(ResourceSubsystem.getInstance());
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
        this.scene.getViewport().setBounds(0, 0, WIDTH, HEIGHT);
        this.isPreviousSpaceDown = false;
        this.isPreviousEscapeDown = false;
        this.isControlled = true;

        this.inputController = new InputController();

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
                    BoundingBox box = child.getBoundingBox().copy();
                    Point3D intersect = box.intersects(ray);
                    if (intersect != null) {
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

        this.inputController.update(currentTime);

        boolean isSpaceDown = this.inputController.isKeyDown(net.java.games.input.Component.Identifier.Key.SPACE);
        boolean isEscapeDown = this.inputController.isKeyDown(net.java.games.input.Component.Identifier.Key.ESCAPE);

        if (this.isPreviousSpaceDown && !isSpaceDown) {
            this.scene.getSceneGraphRoot().visit(new PauseVisitor(currentTime));
        }

        if (this.isPreviousEscapeDown && !isEscapeDown) {
            this.isControlled = !this.isControlled;
        }

        this.isPreviousSpaceDown = isSpaceDown;
        this.isPreviousEscapeDown = isEscapeDown;

        this.scene.update(currentTime);
        Node root = this.scene.getSceneGraphRoot();
        
        root.updateGeometryState(currentTime, this.inputController, true);
        root.updateRenderState();

        pickCheck(root, this.scene.getPickingRay(this.scene.getCamera()));

        if (this.isControlled) {
            this.inputController.centerMouse();
            this.scene.getCamera().update(currentTime, inputController, this.scene.getSceneGraphRoot());
            setBlankCursor();
        } else {
            setDefaultCursor();
        }
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
