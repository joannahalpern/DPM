/*
 * Lab4- Group 53 - Lab4
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.*;

public class Lab4 {
	public static int myMutex = 0; //global variable
	
	/* Create an object that can be used for synchronization across threads. */

	static class theLock extends Object {//this is a lock
	}

	static public theLock lock = new theLock();
	
	public static void main(String[] args) {

		LCD.clear();

		// ask the user whether the motors should drive in a square or float
		LCD.drawString("< Left | Right >", 0, 0);
		LCD.drawString("       |        ", 0, 1);
		LCD.drawString("Falling| Rising ", 0, 2);
		LCD.drawString(" Edge  | Edge   ", 0, 3);
		LCD.drawString("       |        ", 0, 4);
		
		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
		
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		Odometer marshmallow = new Odometer(fuzzyPinkRobot, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		Navigation nav = new Navigation(odo);

		// perform the ultrasonic localization
		USLocalizer uslFallingEdge = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE, nav);
		UltrasonicPoller usPollerFallingEdge = new UltrasonicPoller(us, uslFallingEdge);
		USLocalizer uslRisingEdge = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE, nav);
		UltrasonicPoller usPollerRisingEdge = new UltrasonicPoller(us, uslRisingEdge);

		
		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, marshmallow, ls, nav);
		LightPoller lsPoller = new LightPoller( ls, lsl, nav);
		
		
		switch(option) {
		case Button.ID_LEFT:
			try { Thread.sleep(1000); } catch(Exception e){}
			usPollerFallingEdge.start();
			lsPoller.start();
			break;
		case Button.ID_RIGHT:
			try { Thread.sleep(1000); } catch(Exception e){}
			usPollerRisingEdge.start();
			lsPoller.start();
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		
		Button.waitForAnyPress();
		System.exit(0);
	}
}