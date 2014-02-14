/*
 * Lab4- Group 53 - Lab4
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.*;

public class Lab5 {
	public static int myMutex = 0; //global variable
	public static int counter = 0;
	
	/* Create an object that can be used for synchronization across threads. */

	static class theLock extends Object {//this is a lock
	}

	static public theLock lock = new theLock();
	
	public static enum Colour{
		RED, BLUE, GREEN, OFF
	}
	public static Colour colour = Colour.OFF;
	
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
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);
		
		Navigation nav = new Navigation(odo);
		
		USLocalizer usLocalizer = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE, nav);
		UltrasonicPoller usPoller = new UltrasonicPoller(us, usLocalizer);

		// perform the light sensor localization
		LightLocalizer lsl = new LightLocalizer(odo, marshmallow, ls, nav);
		LightPoller lsPoller = new LightPoller( ls, lsl, nav, colour);
		
		LCDInfo lcd = new LCDInfo(odo, lsPoller, usPoller);

		
		switch(option) {
		case Button.ID_LEFT:
//			try { Thread.sleep(1000); } catch(Exception e){}
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
//		int option1 = 0;
//		while(true){
//		
//			option1 = 0;
//			while (option1 == 0){
//				option1 = Button.waitForAnyPress();
//			}
//			
//				switch(option1){
//				case Button.ID_LEFT:
//					counter++;
//					if (counter%4 == 0){
//						colour = Colour.RED;
//					}
//					else if(counter%4 == 1){
//						colour = Colour.GREEN;
//					}
//					else if(counter%4 == 2){
//						colour = Colour.BLUE;
//					}
//					else{
//						colour = Colour.OFF;
//					}
//					
//					option1 = 0;
//					break;
//				case Button.ID_ESCAPE:
//		//				Button.waitForAnyPress();
//					System.exit(0);
//					option1 = 0;
//					break;
//				} }
				
		Button.waitForAnyPress();
		System.exit(0);

		
	}

	public static int getCounter() {
		return counter;
	}
}