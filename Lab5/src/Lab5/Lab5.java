package Lab5;

import java.util.Stack;
import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.nxt.*;
import lejos.geom.Point;
import PartA_Detection.*;

public class Lab5 {
	public static Stack<Point> stack = new Stack<Point>(); //Add element type
	public static boolean navigate = true; //global variable
	public static boolean identifyingBlock = false;
	public static boolean foamBlockFound = false;
	public static Point blockPoint = new Point(0, 0);
	/* Create an object that can be used for synchronization across threads. */
	static class theLock extends Object {//this is a lock
	}
	static public theLock lock = new theLock();
	
	public static void main(String[] args) {
		
		LCD.clear();
		LCD.drawString("     LAB 5      ", 0, 0);
		LCD.drawString("   Press left   ", 0, 2);
		LCD.drawString("    to begin    ", 0, 3);
		
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		TouchSensor ts = new TouchSensor(SensorPort.S3);
		
		Navigation nav = new Navigation(odo);
		NavigationOur ourNav = new NavigationOur(odo);
		
		UltrasonicPoller usPoller = new UltrasonicPoller(us);

		// perform the light sensor localization
		LightPoller lsPoller = new LightPoller( ls, nav, Colour.BLUE);
		
		USLocalizer localizer = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE, ourNav, usPoller);
		FollowPathAndScan followPathAndScan = new FollowPathAndScan(nav, odo, usPoller, lsPoller);
		ObjectDetection objectDetection = new ObjectDetection(usPoller, lsPoller, nav);
		
		initializeRConsole();
		RConsoleDisplay rcd = new RConsoleDisplay(odo, lsPoller, usPoller);
//		LCDInfo lcd = new LCDInfo(odo, lsPoller, usPoller, usLocalizer);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		switch(option) {
		case Button.ID_LEFT:
			try { Thread.sleep(1000); } catch(Exception e){}
			usPoller.start(); //TODO: change pollers to implement TimerListenner. Have them start in their constructors
			lsPoller.start();
			odo.setPosition(new double[]{30.48, 30.48, 0}, new boolean[]{true, true, true}); //set odometer to start at first square's corner
			localizer.doLocalization();
			
			followPathAndScan.start();
			
			while (stack.empty() && (!foamBlockFound)){
				navigate = true;
				while ( (!stack.empty()) && (!foamBlockFound) ){
					navigate = false;
					blockPoint = stack.pop();
					Point currentPoint = new Point( (float) odo.getX(),(float) odo.getY());
					double blockAngle = (double) currentPoint.angleTo(blockPoint);
					nav.turnTo(blockAngle, true);
					foamBlockFound = objectDetection.doBlockDetection();
				}
			}
		//foamBlockFound will be true now
			grabBlock();
			//TODO: start object avoidance
			nav.travelTo(105, 225);
			dropBlock();
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
			
		Button.waitForAnyPress();
		System.exit(0);

	}
	
	/**
	 * claw releases block
	 *
	 *TODO: fil in dropBlock()
	 */
	private static void dropBlock() {
	}

	/**
	 * The robot needs to turn around and grab the block with the claw
	 * 
	 * TODO: fill in grabBlock()
	 */
	private static void grabBlock() {
	}

	private static void initializeRConsole() {
		RConsole.openUSB(20000);
		RConsole.println("Connected");
	}
}
