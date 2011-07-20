package com.ggervais.gameengine.testapp;

import junit.runner.Version;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;

public class JInputTester {
	public static void main(String[] args)
	{
		System.out.println("JInput version: " + Version.id());
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		
		Mouse mouse = null;
		for (Controller controller : ce.getControllers()) {
			Controller.Type type = controller.getType();
			
			if (type == Controller.Type.MOUSE) {
				System.out.println("It's a mouse!");
				mouse = (Mouse) controller;
			}
		}
		
		Component mouseXAxis = mouse.getX();
		Component mouseYAxis = mouse.getY();
		System.out.println("Relative: " + mouseXAxis.isRelative() + ", analog: " + mouseXAxis.isAnalog());
		System.out.println("Relative: " + mouseYAxis.isRelative() + ", analog: " + mouseYAxis.isAnalog());
		System.out.println("DeadZone X: " + mouseXAxis.getDeadZone());
		System.out.println("DeadZone Y: " + mouseYAxis.getDeadZone());
		while(true) {
			boolean polled = mouse.poll();
			
			if (polled) {
				float xValue = mouseXAxis.getPollData();
				float yValue = mouseYAxis.getPollData();
				System.out.println("(" + xValue + ", " + yValue + ")");
			}
			try {
				Thread.sleep((int) (1000 / 60));
			} catch(InterruptedException ie) {
				
			}
		}
	}

}
