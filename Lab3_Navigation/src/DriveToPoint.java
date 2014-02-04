/*
 * DPM Lab 2 - Lab2.java
 * 
 * Harris Miller 260499543
 * Joanna Halpern 260410826
 */
package src;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * This class controls the motors to drive to specific points which are the arguments given in travelTo(x, y)
 * It works by first calculating it's deltaTheta, which is the angle that the robot needs to rotate to go towards the coordinate
 * 
 * 
 * @author Joanna and Harris
 */
public class DriveToPoint extends Thread {
	private Odometer odometer;
	private static final int FWD_SPEED = 175;
	private static final double WIDTH = 14.9;
	private static final double WHEEL_RADIUS = 2.075;
	private static double xCurrent = 0;
	private static double yCurrent = 0;
	private static double thetaCurrent;
	private final static NXTRegulatedMotor leftMotor = Motor.A;
	private final static NXTRegulatedMotor rightMotor = Motor.B;
	public static double dT = 0;

	public DriveToPoint(Odometer odometer) {
		this.odometer = odometer;
	}

	public void run() {
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor,
				rightMotor }) {
			motor.stop();
			motor.setAcceleration(1500);
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}

		 travelTo(60, 30); //This is where we want robot to go
		 travelTo(30, 30);
		 travelTo(30, 60);
		 travelTo(60, 0);

		leftMotor.stop();
		rightMotor.stop();
	}

	/**
	 * This method causes the robot to travel to the absolute field location (x,
	 * y). This method continuously call turnTo(double theta) and then set the
	 * motor speed to forward(straight). This ensures that the heading is
	 * updated until goal is reached.
	 */
	public void travelTo(double xDestination, double yDestination) {
		xCurrent = odometer.getX();
		yCurrent = odometer.getY();
		thetaCurrent = odometer.getTheta();
		
		double deltaTheta;
		double deltaX = xDestination - xCurrent;
		double deltaY = yDestination - yCurrent;
		
			// this is the change in angle needed to reach the destination
			deltaTheta = calculateDeltaTheta(deltaX, deltaY); 
			if (Math.abs(deltaTheta) > 1) { // if theta has significant error
				turnBy(deltaTheta);
			}
			leftMotor.forward();
			rightMotor.forward();
			leftMotor.setAcceleration(1500);;
			rightMotor.setAcceleration(1500);
			leftMotor.setSpeed(FWD_SPEED);
			rightMotor.setSpeed(FWD_SPEED);

			// totalDistance uses pythagoras theorem to calculate the total
			// distance needed to travel
			double totalDistance = Math.sqrt((deltaX)*(deltaX) + (deltaY)*(deltaY));
			leftMotor.rotate(convertDistance(WHEEL_RADIUS, totalDistance), true);
			rightMotor.rotate(convertDistance(WHEEL_RADIUS, totalDistance), false);
			/*
			 * try { Thread.sleep(1000); } catch (InterruptedException e) { }
			 */
	}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading
	 * theta. This method should turn a MINIMAL angle to it's target.
	 */
	public void turnBy(double deltaTheta) {
		int turningAngle = (int) (deltaTheta * WIDTH / 2 / WHEEL_RADIUS);
		// TODO make the turning angle minimal
		leftMotor.setSpeed(75);
		rightMotor.setSpeed(75);
		leftMotor.rotate(-turningAngle, true);
		rightMotor.rotate(turningAngle, false); // turnTo minimal angle
	}

	/**
	 * This method returns true if another thread has called travelTo() or
	 * turnTo() and the method has yet to return; false otherwise.
	 */
	// this method converts an input between 0 and 360 to an output between -180 and 180
	public double smarterTurns(double dTheta) { 
		int theta = (int) (dTheta * 100); //mod only with ints so we multiply by 100 now and divide at the bottom so we dont lose sigfigs
		theta = (theta+36000*1000)%36000; //put theta between 0 and 360
		
		if (theta > 18000) { //puts theta between -180 and 180
			theta -= 36000;
		} 
		dTheta = (double) theta;
		dTheta /= 100;
		return dTheta;
	}

	public static boolean isNavigating() {
		return false;
	}

	private double calculateDeltaTheta(double deltaX, double deltaY) {
		// -90 b/c atan2 returns an angle relative to pos-x axis
		double thetaDestination = Math.atan2(deltaY, deltaX) * 180 / Math.PI- 90;
		dT = thetaDestination - thetaCurrent;
		dT = smarterTurns(dT);
		return dT; // THIS IS IN DEGREES
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * for testing
	 */
	public void go() {
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.setSpeed(FWD_SPEED);
		rightMotor.setSpeed(FWD_SPEED);
	}

	/**
	 * for testing
	 */
	public void rotateWheel() {
		leftMotor.setSpeed(30);
		leftMotor.rotateTo(30);
	}

	public static double getxCurrent() {
		return xCurrent;
	}

	public static double getyCurrent() {
		return yCurrent;
	}

	public static double getThetaCurrent() {
		return thetaCurrent;
	}
}
