//Robot can detect if there is an object and display message. 
//We still need to: 
//change the threshold values 
//test mean and median method 
//put median and mean into ultrasonic poller as well

import lejos.nxt.*;

public class Lab5Detection {
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
		TwoWheeledRobot fuzzyPinkRobot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(fuzzyPinkRobot, true);
		Odometer marshmallow = new Odometer(fuzzyPinkRobot, true);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		Navigation nav = new Navigation(odo);
		
		USLocalizer usLocalizer = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE, nav);
		UltrasonicPoller usPoller = new UltrasonicPoller(us, usLocalizer);

		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, marshmallow, ls, nav);
		LightPoller lsPoller = new LightPoller( ls, lsl, nav);
		
		ObjectDetection objectDetection = new ObjectDetection(usPoller, lsPoller, nav);

		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
			
		LCDObjectDetection lcd = new LCDObjectDetection(odo, lsPoller, usPoller, objectDetection);
		
		switch(option) {
		case Button.ID_LEFT:
			colour = Colour.RED;
			lsPoller.start();
			usPoller.start();
			break;
		case Button.ID_RIGHT:
//			try { Thread.sleep(1000); } catch(Exception e){}
			colour = Colour.GREEN;
			lsPoller.start();
			usPoller.start();
			break;
		case Button.ID_ENTER:
//			try { Thread.sleep(1000); } catch(Exception e){}
			colour = Colour.BLUE;
			lsPoller.start();
			usPoller.start();
			objectDetection.start();
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
	
	public static Colour getColour(){
		return colour;
	}
}