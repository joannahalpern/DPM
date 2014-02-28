package Lab5;


import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * Given a cartesian coordinate, this class controls the motors to drive to that
 * point.
 * 
 * @author Joanna and Harris
 * 
 */
public class NavigationOur{
	public static int fwdSpeed = 250;
	public static int rotationSpeed = 125;
	public static final double WIDTH = 15.3;
	public static final double WHEEL_RADIUS = 2.09;
	public static double dT = 0;
	private Odometer odometer;
	private static double xCurrent = 0;
	private static double yCurrent = 0;
	private static double thetaCurrent;
	private final static NXTRegulatedMotor leftMotor = Motor.A;
	private final static NXTRegulatedMotor rightMotor = Motor.B;

	public NavigationOur(Odometer odometer) {//initialize the navigator with stopped motors with low acceleration
		this.odometer = odometer;
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor,
				rightMotor }) {
			motor.stop();
			motor.setAcceleration(1000);
		}
	}

	/**
	 * In this constructor you additionally set the forward speed and rotation speed
	 */
	public NavigationOur(Odometer odometer, int fwdSpeed, int rotationSpeed) {//initialize the navigator with stopped motors with low acceleration
		this.odometer = odometer;
		this.fwdSpeed = fwdSpeed;
		this.rotationSpeed = rotationSpeed;
		for (NXTRegulatedMotor motor : new NXTRegulatedMotor[] { leftMotor,
				rightMotor }) {
			motor.stop();
			motor.setAcceleration(1000);
		}
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
		thetaCurrent = odometer.getAng();
		
		double goalTheta;
		double deltaX = xDestination - xCurrent;
		double deltaY = yDestination - yCurrent;
		
			// this is the change in angle needed to reach the destination
			goalTheta = Math.atan2(deltaX, deltaY)*180/Math.PI; 
			if (Math.abs(thetaCurrent-goalTheta) > 1) { // if theta has significant error
				turnTo(goalTheta);
			}
			leftMotor.forward();
			rightMotor.forward();
			leftMotor.setSpeed(fwdSpeed);
			rightMotor.setSpeed(fwdSpeed);

			// totalDistance uses pythagoras theorem to calculate the total
			// distance needed to travel
			double totalDistance = Math.sqrt((deltaX)*(deltaX) + (deltaY)*(deltaY));
			leftMotor.rotate(convertDistance(WHEEL_RADIUS, totalDistance), true);
			rightMotor.rotate(convertDistance(WHEEL_RADIUS, totalDistance), false);
			/*
			 * try { Thread.sleep(1000); } catch (InterruptedException e) { }
			 */
	}
	
	public void moveBy(double distance){//as opposed to moveTo. This method takes negatives
		leftMotor.rotate(convertDistance(WHEEL_RADIUS, distance), true);
		rightMotor.rotate(convertDistance(WHEEL_RADIUS, distance), false);
	}

	/**
	 * This method causes the robot to turn (on point) to the absolute heading
	 * theta. This method should turn a MINIMAL angle to it's target.
	 */
	public void turnTo(double theta) {
		double deltaTheta = theta - odometer.getAng(); 
		deltaTheta = smarterTurns(deltaTheta);
		int turningAngle = (int) (deltaTheta * WIDTH / 2 / WHEEL_RADIUS);
		
		leftMotor.setSpeed(rotationSpeed);
		rightMotor.setSpeed(rotationSpeed);
		leftMotor.rotate(turningAngle, true);
		rightMotor.rotate(-turningAngle, false); // turnTo minimal angle
	}
	
	public static void go(int speed) { //this is used to turn the motors on, indefinitely 
		Motor.A.setSpeed(speed);
		Motor.B.setSpeed(speed);
	}

	// this method converts an input between 0 and 360 to an output between -180 and 180
	public double smarterTurns(double dTheta) { 
		int theta = (int) (dTheta * 100); //mod only with ints so we multiply by 100 now and divide at the bottom so we dont lose sigfigs
		while (theta<0){
			theta += 36000; //put theta between 0 and 360
		}
		theta = theta%36000; 
		
		if (theta > 18000) { //puts theta between -180 and 180
			theta -= 36000;
		} 
		dTheta = (double) theta;
		dTheta /= 100;
		return dTheta;
	}

	public static boolean isNavigating() { //UNUSED
		return false;
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
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
	
	public void setBackward(){//set motors to run backward
		Motor.A.backward();
		Motor.B.backward();
	}
	
	public void setForward(){//set motors to run forward
		Motor.A.forward();
		Motor.B.forward();
	}
	
	public void setClockwise() {//set motors to run clockwise
		Motor.A.forward();
		Motor.B.backward();		
	}
	public void setCounterClockwise() {//set motors to run counterclockwise
		Motor.A.backward();		
		Motor.B.forward();
	}
	public void setForwardSpeed(int fwdSpeed){
		this.fwdSpeed = fwdSpeed;
	}
	public void setRotationSpeed(int rotationSpeed){
		this.rotationSpeed = rotationSpeed;
	}
}