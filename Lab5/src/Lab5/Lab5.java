package Lab5;


import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

public class Lab5 {
	public static int myMutex = 0; //global variable
	
	/* Create an object that can be used for synchronization across threads. */

	static class theLock extends Object {//this is a lock
	}

	static public theLock lock = new theLock();
	
	public static Colour colour = Colour.OFF;
	
	public static void main(String[] args) {
		
		LCD.clear();
		
		LCD.drawString("     LAB 5      ", 0, 0);
		LCD.drawString("Press center for", 0, 2);
		LCD.drawString(" translational  ", 0, 3);
		LCD.drawString("     scan       ", 0, 4);
		

		
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		Odometer marshmallow = new Odometer(fuzzyPinkRobot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		Navigation nav = new Navigation(odo);
		
		UltrasonicPoller usPoller = new UltrasonicPoller(us);
		USLocalizer usLocalizer = new USLocalizer(odo, us, USLocalizer.LocalizationType.RISING_EDGE, nav, usPoller);

		// perform the light sensor localization
		LightPoller lsPoller = new LightPoller( ls, nav, Colour.BLUE);
		
		initializeRConsole();
		RConsoleDisplay rcd = new RConsoleDisplay(odo, lsPoller, usPoller, usLocalizer);
//		LCDInfo lcd = new LCDInfo(odo, lsPoller, usPoller, usLocalizer);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		switch(option) {
		case Button.ID_LEFT:
			try { Thread.sleep(1000); } catch(Exception e){}
			usPoller.start();
			usLocalizer.doLocalization();
			nav.turnTo(0);
			break;
		case Button.ID_RIGHT:
			try { Thread.sleep(1000); } catch(Exception e){}
			usPoller.start();
			nav.setRotationSpeed(50);
			nav.turnTo(179);
			nav.turnTo(181);
			nav.turnTo(0);
			break;
		case Button.ID_ENTER:
			try { Thread.sleep(1000); } catch(Exception e){}
			usPoller.start();
			nav.setForward();
			nav.setForwardSpeed(75);
			nav.travelTo(0, 91.44);
			break;
		case Button.ID_ESCAPE:
//			try { Thread.sleep(1000); } catch(Exception e){}
			colour = Colour.OFF;
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

	private static void initializeRConsole() {
		RConsole.openUSB(20000);
		RConsole.println("Connected");
	}
}