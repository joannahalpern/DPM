package PartA_Detection;


import Lab5.*;
import lejos.nxt.*;

public class Lab5_PartA {
	public static int myMutex = 0; //global variable
	
	/* Create an object that can be used for synchronization across threads. */

	static class theLock extends Object {//this is a lock
	}

	static public theLock lock = new theLock();

	private static Colour colour = Colour.OFF;
	
	public static void main(String[] args) {
		
		LCD.clear();

		// ask the user whether the motors should drive in a square or float
		LCD.drawString("<--RED | GREEN->", 0, 0);
		LCD.drawString("     |Blue|     ", 0, 1);
		LCD.drawString("     | OFF|      ", 0, 2);
		LCD.drawString("second push exits", 0, 5);
		

		
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.B,Motor.C);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		Odometer marshmallow = new Odometer(fuzzyPinkRobot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		Navigation nav = new Navigation(odo);
		NavigationOur ourNav = new NavigationOur(odo);
		
		UltrasonicPoller usPoller = new UltrasonicPoller(us);
		ObstacleAvoidance avoider = new ObstacleAvoidance(nav, usPoller, odo);
		USLocalizer usLocalizer = new USLocalizer(odo, USLocalizer.LocalizationType.FALLING_EDGE, ourNav, usPoller);

		// perform the light sensor localization
		LightPoller lsPoller = new LightPoller( ls, Colour.BLUE);
		
		ObjectDetection objectDetection = new ObjectDetection(usPoller, lsPoller, nav);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		LCDObjectDetection lcd = new LCDObjectDetection(odo, lsPoller, usPoller, objectDetection);
		
		switch(option) {
		case Button.ID_LEFT:
			lsPoller.setFloodLight(Colour.RED);
			lsPoller.start();
			usPoller.start();
			break;
		case Button.ID_RIGHT:
			lsPoller.setFloodLight(Colour.GREEN);
			lsPoller.start();
			usPoller.start();
			break;
		case Button.ID_ENTER:
			lsPoller.setFloodLight(Colour.BLUE);
			lsPoller.start();
			usPoller.start();
			objectDetection.start();
			break;
		case Button.ID_ESCAPE:
			lsPoller.setFloodLight(Colour.OFF);
			lsPoller.start();
			usPoller.start();
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
			
		Button.waitForAnyPress();
		System.exit(0);

	}
	
	public static Colour getColour(){
		return colour;
	}
}