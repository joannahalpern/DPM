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

public class Lab5 {
	public static Stack<?> stack = new Stack(); //Add element type
	public static int myMutex = 0; //global variable
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
		
		UltrasonicPoller usPoller = new UltrasonicPoller(us);

		// perform the light sensor localization
		LightPoller lsPoller = new LightPoller( ls, nav, Colour.BLUE);
		
		LocalizationAtOrigin localizer = new LocalizationAtOrigin(odo, nav, usPoller, lsPoller);
		FollowPathAndScan followPathAndScan = new FollowPathAndScan(nav, odo, usPoller, lsPoller);

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
			localizer.localizeFromOrigin();
		
			followPathAndScan.start();
			
			
			
			
			
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
			
		Button.waitForAnyPress();
		System.exit(0);

	}

	private static void initializeRConsole() {
		RConsole.openUSB(20000);
		RConsole.println("Connected");
	}
}
