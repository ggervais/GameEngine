package com.ggervais.gameengine.testapp;

import junit.runner.Version;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;
import org.apache.log4j.Logger;

public class JInputTester {

    private static final Logger log = Logger.getLogger(JInputTester.class);

	public static void main(String[] args)
	{
		log.info("JInput version: " + Version.id());
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		
		Mouse mouse = null;
		for (Controller controller : ce.getControllers()) {
			Controller.Type type = controller.getType();
			
			if (type == Controller.Type.MOUSE) {
				log.info("It's a mouse!");
				mouse = (Mouse) controller;
			}
		}
		
		Component mouseXAxis = mouse.getX();
		Component mouseYAxis = mouse.getY();
		log.info("Relative: " + mouseXAxis.isRelative() + ", analog: " + mouseXAxis.isAnalog());
		log.info("Relative: " + mouseYAxis.isRelative() + ", analog: " + mouseYAxis.isAnalog());
		log.info("DeadZone X: " + mouseXAxis.getDeadZone());
		log.info("DeadZone Y: " + mouseYAxis.getDeadZone());
		while(true) {
			boolean polled = mouse.poll();
			
			if (polled) {
				float xValue = mouseXAxis.getPollData();
				float yValue = mouseYAxis.getPollData();
				log.info("(" + xValue + ", " + yValue + ")");
			}
			try {
				Thread.sleep((int) (1000 / 60));
			} catch(InterruptedException ie) {
				
			}
		}
	}

}
