package com.ggervais.gameengine.input;

import com.ggervais.gameengine.Subsystem;
import com.ggervais.gameengine.UninitializedSubsystemException;
import com.ggervais.gameengine.math.Point3D;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Keyboard;
import net.java.games.input.Mouse;
import net.java.games.input.Component.Identifier.Key;

import java.awt.*;
import java.rmi.UnexpectedException;

public class InputSubsystem implements Subsystem {


    private boolean initialized;
	private Mouse mouse;
	private Keyboard keyboard;
	private static InputSubsystem instance;
	
	private InputSubsystem() {
        this.initialized = false;
	}
	
	public static InputSubsystem getInstance() {
		if (instance == null) {
			instance = new InputSubsystem();
		}
		return instance;
	}

	public void destroy() throws UninitializedSubsystemException {
        if (!this.initialized) {
            throw new UninitializedSubsystemException();
        }

		// These objects will probably be garbage-collected, so this in just placeholder code.
		this.mouse = null;
		this.keyboard = null;
	}

    public boolean isInitialized() {
        return this.initialized;
    }

    public void init() {
		ControllerEnvironment environment =	ControllerEnvironment.getDefaultEnvironment();
		for (Controller controller : environment.getControllers()) {
			if (controller.getType() == Controller.Type.MOUSE) {
				this.mouse = (Mouse) controller;
			}
			// The "Power Button" is a hack caused by my Microsoft keyboard which, apparently, generates two keyboard controllers.
			// TODO: fix the hack.
			if (controller.getType() == Controller.Type.KEYBOARD && !controller.getName().equals("Power Button")) {
				this.keyboard = (Keyboard) controller;
			}
			if (mouse != null && keyboard != null) {
				break;
			}
			
			// TODO: implement support for game controllers.
		}
		this.initialized = true;
	}

    private void centerMouse() {
        Point3D position = getWindowPosition();
        Dimension dimension = getWindowDimensions();

        int centerX = (int) (position.x() + dimension.getWidth() / 2);
        int centerY = (int) (position.y() + dimension.getHeight() / 2);
        try {
            Robot robot = new Robot();
            robot.mouseMove(centerX, centerY);
        } catch(Exception e) {}
    }

	public void update(long currentTime) throws UninitializedSubsystemException {

        if (!this.initialized) {
            throw new UninitializedSubsystemException();
        }

        // Poll the controllers.
        this.mouse.poll();
		this.keyboard.poll();

        centerMouse();
	}
	
	public float getMouseMovementX() throws UninitializedSubsystemException {

        if (!this.initialized) {
            throw new UninitializedSubsystemException();
        }

		// TODO: support absolute movement.
		float movement = 0;
		Component mouseXAxis = this.mouse.getX();
		if (mouseXAxis.isRelative()) {
			movement = mouseXAxis.getPollData();
		}
		return movement;
	}
	
	public float getMouseMovementY() throws UninitializedSubsystemException {

        if (!this.initialized) {
            throw new UninitializedSubsystemException();
        }

		// TODO: support absolute movement.
		float movement = 0;
		Component mouseYAxis = this.mouse.getY();
		if (mouseYAxis.isRelative()) {
			movement = mouseYAxis.getPollData();
		}
		return movement;
	}
	
	public boolean isKeyDown(Key key) throws UninitializedSubsystemException {

        if (!this.initialized) {
            throw new UninitializedSubsystemException();
        }

		return this.keyboard.isKeyDown(key);
	}

    public Point3D getMousePosition() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point mouseLocation = pointerInfo.getLocation();

        return new Point3D((float) mouseLocation.getX(), (float) mouseLocation.getY(), 0);
    }

    public Point3D getWindowPosition() {
        Window[] windows = Window.getWindows();
        Point3D windowPosition = Point3D.zero();
        if (windows.length > 0) {
            // TODO assume 1 window.
            Window currentWindow = windows[0];
            windowPosition.x(currentWindow.getX());
            windowPosition.y(currentWindow.getY());
        }

        return windowPosition;
    }

    public Dimension getWindowDimensions() {
        Dimension windowDimension = new Dimension(0, 0);
        Window[] windows = Window.getWindows();
        if (windows.length > 0) {
            // TODO assume 1 window.
            Window currentWindow = windows[0];
            windowDimension.setSize(currentWindow.getWidth(), currentWindow.getHeight());
        }
        return windowDimension;
    }
}
