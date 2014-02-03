/*
 * Hey Harris!
 * 
 * What I've done:
 * 		-Put everything into the thread which starts when Lab3 calls driveToPoint.start() 
 * 		 where driveToPoint is the instance of DriveToPoint
 * 		-It was very very jolty and didn't work well with the while loop so I commented out that (and a sleeper)
 * 		 and instead it calculates the totalDistance it needs to go to and uses rotate to get to that total distance
 * 		 (so it's basically now just calculating the vector)
 * 
 * What's not working:
 * 		-The deltaTheta seems to be negative
 * 		-The turnTo() method is turning too far
 * 
 * Good luck!!!!!!
 */

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * Given a cartesian coordinate, this class controls the motors to drive to that point.
 * @author Joanna and Harris
 *
 */
public class DriveToPoint extends Thread{
	private Odometer odometer;
	private static final int FWD_SPEED = 150;
	private static final double WIDTH = 14.9;
	private static final double WHEEL_RADIUS = 2.075;
	private static double xCurrent = 0;
	private static double yCurrent = 0;
	private static double thetaCurrent;
	private final static NXTRegulatedMotor leftMotor = Motor.A;
	private final static NXTRegulatedMotor rightMotor = Motor.B;
	public static int dT = 0;
	
	
	
	public DriveToPoint(Odometer odometer) {
		this.odometer = odometer;
	}
	
	public void run(){
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor, rightMotor }) {
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
	 * y). This method continuously call turnTo(double theta) and then
	 * set the motor speed to forward(straight). This ensures that the
	 * heading is updated until goal is reached.
	 */
	public void travelTo(double xDestination, double yDestination) {
		double deltaTheta;
//		while ( (Math.abs(xCurrent - xDestination) > 2) && (Math.abs(yCurrent - yDestination) > 2)){//while the x and y values are not within a threshold of 2 to the desired x and y
			xCurrent = odometer.getX();
			yCurrent = odometer.getY();
			thetaCurrent = odometer.getTheta();
			deltaTheta = calculateDeltaTheta(xDestination, yDestination); //this is the change in angle needed to get to the destination
			if(Math.abs(deltaTheta) > 1){ // if theta has significant error
				turnTo(deltaTheta);
			}
			leftMotor.forward();
			rightMotor.forward();
			leftMotor.setSpeed(FWD_SPEED);
			rightMotor.setSpeed(FWD_SPEED);
			
			//totalDistance uses pythagoras theorem to calculate the total distance needed to travel
			double totalDistance = Math.sqrt((xDestination - xCurrent)*(xDestination - xCurrent) + (yDestination - yCurrent)*(yDestination - yCurrent));
			leftMotor.rotate(convertDistance(WHEEL_RADIUS, totalDistance), true);
			rightMotor.rotate(convertDistance(WHEEL_RADIUS, totalDistance), false);
/*		
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
*/
			
//			xCurrent = odometer.getX();
//			yCurrent = odometer.getY();
//			thetaCurrent = odometer.getTheta();
		}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading
	 * theta. This method should turn a MINIMAL angle to it's target.
	 */
	public void turnTo(double deltaTheta) {
		deltaTheta = ((360+deltaTheta)%360)-180; //converts deltaTheta to an angle between -180 and 180
		int turningAngle = (int) (deltaTheta*WIDTH/2/WHEEL_RADIUS);
		//TODO make the turning angle minimal
		leftMotor.setSpeed(75);
		rightMotor.setSpeed(75);
		leftMotor.rotate(-turningAngle, true);
		rightMotor.rotate(turningAngle, false); //turnTo minimal angle
	}

	/**
	 * This method returns true if another thread has called travelTo() or
	 * turnTo() and the method has yet to return; false otherwise.
	 */
	public static boolean isNavigating() {
		return false;
	}


	private double calculateDeltaTheta(double xDes, double yDes) {//TODO: if problems: test angle function plz
		double thetaDestination = Math.atan2(yDes-yCurrent, xDes-xCurrent)*180/Math.PI - 90;// -90 b/c atan2 returns an angle relative to pos-x axis
		double deltaTheta = thetaDestination - thetaCurrent;
		dT = (int) deltaTheta;
		return deltaTheta;
	}
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * for testing
	 */
	public void go(){
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.setSpeed(FWD_SPEED);
		rightMotor.setSpeed(FWD_SPEED);
	}
	/**
	 * for testing
	 */
	public void rotateWheel(){
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
