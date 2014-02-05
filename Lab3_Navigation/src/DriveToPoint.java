/*
 * DPM Lab 2 - Lab2.java
 * 
 * Harris Miller 260499543
 * Joanna Halpern 260410826
 */

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * This class controls the motors to drive to specific points which are the arguments given in travelTo(x, y)
 * It works by entering a loop which occurs as long as the robot is not at it's destination (or within a threshold of 1cm).
 * In this loop:
 * 		It first calculating it's deltaTheta, which is the angle that the robot needs to rotate to go towards the coordinate.
 * 		If delta theta is NOT almost zero (meaning it needs to correct it's direction)
 * 			it calls turnBy(deltaTheta) which rotates the robot to that angle
 * 		It then sets the speeds of the motors so that the robot moves forward
 * 
 * This loop continues until it gets to the proper coordinates and then the motor speeds go to 0
 * ***Note* we changed the name of turnTo(double theta) to turnBy(double deltaTheta)
 * @author Joanna and Harris
 */
public class DriveToPoint extends Thread {
	private Odometer odometer;
	private static final int FWD_SPEED = 175;
	private static final int ROTATING_SPEED = 75;
	private static final double WIDTH = 14.9; //width between wheels of robot
	private static final double WHEEL_RADIUS = 2.075;
	private static double xCurrent; //this will be the x that the odometer reads
	private static double yCurrent;// """" y """""
	private static double thetaCurrent;//"""" theta """""
	private final static NXTRegulatedMotor leftMotor = Motor.A;
	private final static NXTRegulatedMotor rightMotor = Motor.B;
	public static double dT = 0; //this dt was only used for testing to display the delta theta right after it's calculated

	//constructor takes odometer
	public DriveToPoint(Odometer odometer) {
		this.odometer = odometer;
	}

	//where thread starts
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
	 * y). This method continuously call turnBy(double deltaTheta) and then set the
	 * motor speed to forward (straight). This ensures that the heading is
	 * updated until goal is reached.
	 */
	public void travelTo(double xDestination, double yDestination) {
		xCurrent = odometer.getX();
		yCurrent = odometer.getY();
		thetaCurrent = odometer.getTheta();
		
		double deltaTheta;
		double deltaX = xDestination - xCurrent;
		double deltaY = yDestination - yCurrent;
		while((Math.abs(xCurrent - xDestination) > 1)||(Math.abs(yCurrent - yDestination) > 1) ){// while the robot has not reach within 1 cm of it's destination
			
			deltaX = xDestination - xCurrent;
			deltaY = yDestination - yCurrent;
			
			// this is the change in angle needed to reach the destination. It is an angle between -180 and 180 (the minimal turning angle)
			deltaTheta = calculateDeltaTheta(deltaX, deltaY); 
			
			if (Math.abs(deltaTheta) > 1) { // if deltaTheta has significant error
				turnBy(deltaTheta);
			}
			
			leftMotor.forward();
			rightMotor.forward();
			leftMotor.setAcceleration(1500);;
			rightMotor.setAcceleration(1500);
			leftMotor.setSpeed(FWD_SPEED);
			rightMotor.setSpeed(FWD_SPEED);
		}
		leftMotor.setSpeed(0); // once it reaches it's destination it should stop
		rightMotor.setSpeed(0);
	}

	/**
	 * *Note* we changed the name of turnTo(double theta) to turnBy(double deltaTheta)
	 * This method causes the robot to turn (on point) to the absolute heading
	 * theta.
	 */
	public void turnBy(double deltaTheta) {
		int turningAngle = (int) (deltaTheta * WIDTH / 2 / WHEEL_RADIUS);
		// TODO make the turning angle minimal
		leftMotor.setSpeed(ROTATING_SPEED);
		rightMotor.setSpeed(ROTATING_SPEED);
		leftMotor.rotate(-turningAngle, true);
		rightMotor.rotate(turningAngle, false); // turnTo minimal angle
	}

	// this method converts any input angle to an equivelent angle between -180 and 180
	public double smarterTurns(double dTheta) { 
		int theta = (int) (dTheta * 100); //mod only with ints so we multiply by 100 now and divide at the bottom so we dont lose sigfigs
		while (theta<0){ //Make theta positive
				theta += 36000;
		}
		theta = (theta+36000)%36000; //put theta between 0 and 360
		
		if (theta > 18000) { //puts theta between -180 and 180
			theta -= 36000;
		} 
		dTheta = (double) theta;
		dTheta /= 100; //put back to double
		return dTheta;
	}
/**
 * This method is not useful for how our code works so it is set to always return false
 */
	public static boolean isNavigating() {
		return false;
	}

	/**
	 * calculates how much robot needs to turn by and gives angle betwenn -180 and 180
	 */
	private double calculateDeltaTheta(double deltaX, double deltaY) {
		// -90 b/c atan2 returns an angle relative to pos-x axis
		double thetaDestination = Math.atan2(deltaY, deltaX) * 180 / Math.PI- 90;
		dT = thetaDestination - thetaCurrent; 
		dT = smarterTurns(dT); // puts angle between -180 and 180
		return dT; // THIS IS IN DEGREES
	}

	//not used
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
