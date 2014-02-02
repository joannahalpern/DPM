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
	private Odometer odometer;
	private double xCurrent;
	private double yCurrent;
	private double thetaCurrent;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	
	
	public DriveToPoint(Odometer odometer) {
		this.odometer = odometer;
		this.xCurrent = odometer.getX();
		this.yCurrent = odometer.getY();
		this.thetaCurrent = odometer.getTheta();
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(1500);
		}
	}
	
	/**
	 * This method causes the robot to travel to the absolute field location (x,
	 * y). This method continuously call turnTo(double theta) and then
	 * set the motor speed to forward(straight). This ensures that the
	 * heading is updated until goal is reached.
	 */
	public void travelTo(double xDestination, double yDestination) {
		while ( (xCurrent != xDestination) && (yCurrent != yDestination)){//TODO: add acceptable bandwidth around destination
			double deltaTheta = calculateDeltaTheta(xDestination, yDestination); //this is the change in angle needed to get to the destination
			if(Math.abs(deltaTheta) > 1){ // if theta has significant error
				turnTo(deltaTheta);
			}
			leftMotor.forward();
			rightMotor.forward();
			leftMotor.setSpeed(FWD_SPEED);
			rightMotor.setSpeed(FWD_SPEED);
			
			xCurrent = odometer.getX();
			yCurrent = odometer.getY();
		}
	}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading
	 * theta. This method should turn a MINIMAL angle to it's target.
	 */
	public void turnTo(double deltaTheta) {
		int turningAngle = (int) (deltaTheta*WIDTH/2/WHEEL_RADIUS*180/Math.PI);
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		leftMotor.rotate(30);
//		leftMotor.rotate(turningAngle, true);
//		rightMotor.rotate(-turningAngle, false); 
	}

	/**
	 * This method returns true if another thread has called travelTo() or
	 * turnTo() and the method has yet to return; false otherwise.
	 */
	public static boolean isNavigating() {
		return false;
	}


	private double calculateDeltaTheta(double xDes, double yDes) {//TODO: if problems: test angle function plz
		double thetaDestination = Math.atan2(yDes-yCurrent, xDes-xCurrent) - 90;// -90 b/c atan2 returns an angle relative to pos-x axis
		double deltaTheta = thetaDestination - thetaCurrent;
		return deltaTheta;
	}

	public void go(){
		leftMotor.forward();
		rightMotor.forward();
		leftMotor.setSpeed(FWD_SPEED);
		rightMotor.setSpeed(FWD_SPEED);
	}
	
	public void rotateWheel(){
		leftMotor.setSpeed(0);
		leftMotor.rotateTo(30);
	}
}
