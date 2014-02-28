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

/**
 * Robot follows a path (ie. goes to 4 coordinates). It does obstacle avoidance while going from one point to the next.
 * At the point, the robot does a 180 degree scan to detect all obstacles (see FollowPathAndScan class for more details)
 * If there are any blocks identified (wooden or styrofoam), the coordinates of the middle of that block are put in the
 * blockStack. The robot then goes to each block coordinate in that stack and does object detection to see if the block
 * is styrofoam (see ObjectDetection class for details). Once it confirms that it has identified a styrofoam block,
 * the robot grabs the block then travels to the drop zone where it then drops the block.
 *
 */
public class Lab5 {
	public static Stack<Point> blockStack = new Stack<Point>(); //Add element type
	public static boolean navigate = true; //true when robot is traveling
	public static boolean identifyingBlock = false; //true when robot is doing ObjectDetection
	public static boolean foamBlockFound = false; 
	public static boolean obstacleAvoidance = false; //true when robot is doing obstacleAvoidance
	public static Point blockPoint = new Point(0, 0); //calculated coordinate of block (determined in FollowPathAndScan
	
	/* Create an object that can be used for synchronization across threads. */
	static class theLock extends Object {//this is a lock
	}
	static public theLock lock = new theLock();
	
	public static void main(String[] args) {
		
		LCD.clear();
		LCD.drawString("     LAB 5      ", 0, 0);
		LCD.drawString("   Press left   ", 0, 2);
		LCD.drawString("    to begin    ", 0, 3);
		
		// setup the odometer, display, and ultrasonic and light sensor, motors ...
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.B, Motor.C);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		Navigation nav = new Navigation(odo);
		NavigationOur ourNav = new NavigationOur(odo);
		
		UltrasonicPoller usPoller = new UltrasonicPoller(us);
		LightPoller lsPoller = new LightPoller(ls, Colour.BLUE);
		
		ObstacleAvoidance avoider = new ObstacleAvoidance(nav, usPoller, odo);
		
		USLocalizer localizer = new USLocalizer(odo, USLocalizer.LocalizationType.RISING_EDGE, ourNav, usPoller);
		FollowPathAndScan followPathAndScan = new FollowPathAndScan(nav, odo, usPoller, lsPoller, avoider);
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
			usPoller.start();
			lsPoller.start();
			odo.setPosition(new double[]{30.48, 30.48, 0}, new boolean[]{true, true, true}); //set odometer to start at first square's corner
			localizer.doLocalization();
			
			avoider.start();
			followPathAndScan.start();
			
			while (blockStack.empty() && (!foamBlockFound)){
				navigate = true;
				while ( (!blockStack.empty()) && (!foamBlockFound) ){
					navigate = false;
					blockPoint = blockStack.pop();
					Point currentPoint = new Point( (float) odo.getX(),(float) odo.getY());
					double blockAngle = (double) currentPoint.angleTo(blockPoint) + 90;
					nav.turnTo(blockAngle, true);
					foamBlockFound = objectDetection.doBlockDetection();
				}
			}
		//foamBlockFound will be true now
			grabBlock();

			nav.travelTo(106.68, 228.6); //drop zone
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
	 */
	private static void dropBlock() {
		Motor.C.rotate(90);
	}

	/**
	 * Claw of robot grabs block
	 */
	private static void grabBlock() {
		Motor.C.rotate(-90);
	}

	//for testing
	private static void initializeRConsole() {
		RConsole.openUSB(20000);
		RConsole.println("Connected");
	}
}
