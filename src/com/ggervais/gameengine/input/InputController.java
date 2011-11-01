package com.ggervais.gameengine.input;

import com.ggervais.gameengine.math.Point3D;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Keyboard;
import net.java.games.input.Mouse;
import net.java.games.input.Component.Identifier.Key;
import org.apache.log4j.Logger;

import java.awt.*;

public class InputController {

    private static final Logger log = Logger.getLogger(InputController.class);
	private Mouse mouse;
	private Keyboard keyboard;
	
	public InputController() {
	    init();
    }

    private void init() {
		ControllerEnvironment environment =	ControllerEnvironment.getDefaultEnvironment();
		for (Controller controller : environment.getControllers()) {
			if (controller.getType() == Controller.Type.MOUSE) {
				this.mouse = (Mouse) controller;
                log.info("Found mouse: " + this.mouse);
			}
			// The "Power Button" is a hack caused by my Microsoft keyboard which, apparently, generates two keyboard controllers.
			// TODO: fix the hack.
			if (controller.getType() == Controller.Type.KEYBOARD && controller.getName().toLowerCase().endsWith("keyboard")) {
				this.keyboard = (Keyboard) controller;
			    log.info("Found keyboard: " + this.keyboard);
            }
			if (mouse != null && keyboard != null) {
				break;
			}
		}
	}

    public void centerMouse() {
        Point3D position = getWindowPosition();
        Dimension dimension = getWindowDimensions();

        int centerX = (int) (position.x() + dimension.getWidth() / 2);
        int centerY = (int) (position.y() + dimension.getHeight() / 2);
        try {
            Robot robot = new Robot();
            robot.mouseMove(centerX, centerY);
        } catch(Exception e) {}
    }

	public void update(long currentTime) {

        // Poll the controllers.
        this.mouse.poll();
		this.keyboard.poll();
	}
	
	public float getMouseMovementX() {

		// TODO: support absolute movement.
		float movement = 0;
		Component mouseXAxis = this.mouse.getX();
		if (mouseXAxis.isRelative()) {
			movement = mouseXAxis.getPollData();
		}
		return movement;
	}
	
	public float getMouseMovementY() {

		// TODO: support absolute movement.
		float movement = 0;
		Component mouseYAxis = this.mouse.getY();
		if (mouseYAxis.isRelative()) {
			movement = mouseYAxis.getPollData();
		}
		return movement;
	}
	
	public boolean isKeyDown(Key key) {
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
