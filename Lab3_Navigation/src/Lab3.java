/*
 * DPM Lab 2 - Lab2.java
 * 
 * Harris Miller 260499543
 * Joanna Halpern 260410826
 */
import lejos.nxt.*;

public class Lab3 {
	//THESE ARE THE ONLY VALUES WE TOUCHED
	public final static double WHEELRADIUS = 2.075 ; //Measured with a caliper
	public final static double WIDTH = 14.9; //Measured with a caliper

	
	public static void main(String[] args) {
		int buttonChoice;

		// some objects that need to be instantiated
		Odometer odometer = new Odometer();
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);

		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left | Right >", 0, 0);
			LCD.drawString("       |        ", 0, 1);
			LCD.drawString(" Float | Drive  ", 0, 2);
			LCD.drawString("motors |        ", 0, 3);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { Motor.A, Motor.B, Motor.C }) {
				motor.forward();
				motor.flt();
			}

			// start only the odometer and the odometry display
			odometer.start();
			odometryDisplay.start();
		} else {
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			odometer.start();
			odometryDisplay.start();

			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			(new Thread() {
				public void run() {
					Odometer odometer = new Odometer();
					DriveToPoint p1 = new DriveToPoint(odometer);
//					p1.travelTo(30, 60);
					p1.rotateWheel();
//					p1.go();
//					p1.turnTo(30);
				}
			}).start();
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}