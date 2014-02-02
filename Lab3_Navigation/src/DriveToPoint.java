import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * Given a cartesian coordinate, this class controls the motors to drive to that point.
 * @author Joanna and Harris
 *
 */
public class DriveToPoint extends Thread{
	private static final int FWD_SPEED = 150;
	private static final double WIDTH = 14.9;
	private static final double WHEEL_RADIUS = 2.075;
	private static Odometer odometer;
	private static double xCurrent;
	private static double yCurrent;
	private static double thetaCurrent;
	private final static NXTRegulatedMotor leftMotor = Motor.A;
	private final static NXTRegulatedMotor rightMotor = Motor.B;
	public static int dT = 0;
	
	
	public DriveToPoint() {
	}
	
	/**
	 * This method causes the robot to travel to the absolute field location (x,
	 * y). This method continuously call turnTo(double theta) and then
	 * set the motor speed to forward(straight). This ensures that the
	 * heading is updated until goal is reached.
	 */
	public static void travelTo(double xDestination, double yDestination, Odometer odometer) {
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(1500);
			xCurrent = odometer.getX();
			yCurrent = odometer.getY();
		}
		double deltaTheta;
		while ( (xCurrent != xDestination) && (yCurrent != yDestination)){//TODO: add acceptable bandwidth around destination
			deltaTheta = calculateDeltaTheta(xDestination, yDestination); //this is the change in angle needed to get to the destination
			if(Math.abs(deltaTheta) > 1){ // if theta has significant error
				
				turnTo(deltaTheta);
			}
			leftMotor.forward();
			rightMotor.forward();
			leftMotor.setSpeed(FWD_SPEED);
			rightMotor.setSpeed(FWD_SPEED);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// there is nothing to be done here because it is not expected that
				// the odometer will be interrupted by another thread
			}
			
			xCurrent = odometer.getX();
			yCurrent = odometer.getY();
		}
	}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading
	 * theta. This method should turn a MINIMAL angle to it's target.
	 */
	public static void turnTo(double deltaTheta) {
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


	private static double calculateDeltaTheta(double xDes, double yDes) {//TODO: if problems: test angle function plz
		double thetaDestination = Math.atan2(yDes-yCurrent, xDes-xCurrent) - 90;// -90 b/c atan2 returns an angle relative to pos-x axis
		double deltaTheta = thetaDestination - thetaCurrent;
		dT = (int) deltaTheta;
		return deltaTheta;
	}
	/**
	 * for testing
	 */
	public static void go(){
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
}
