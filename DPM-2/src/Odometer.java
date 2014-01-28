/*
 * DPM Lab 2 - Odometer
 * 
 * Harris Miller 260499543
 * Joanna Halpern 260410826
 */
import lejos.nxt.Motor;

public class Odometer extends Thread {
	// robot position and angle
	private double x, y, theta;
	// distance each wheel travels and change in angle (theta) per ODOMETER_PERIOD (25ms)
	private double  deltaTheta, leftDistance, rightDistance;
	// calculated average of left and right distance traveled. The distance the center travels.
	private double centerDistance;
	// The distance traveled by left and right wheel over the previous cycle
	private double lastLeftDistance, lastRightDistance;
	private final double WHEEL_RADIUS = 2.075;
	private final double WIDTH = 14.9;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
		lastLeftDistance = 0;
		lastRightDistance = 0;
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here
			leftDistance = (WHEEL_RADIUS*Motor.A.getTachoCount())*(Math.PI/180) - lastLeftDistance;
			rightDistance = (WHEEL_RADIUS*Motor.B.getTachoCount())*(Math.PI/180) - lastRightDistance;
			
			centerDistance = (leftDistance+rightDistance)/2;
			deltaTheta = (leftDistance-rightDistance)/WIDTH;
			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				x += centerDistance*Math.sin(theta*(Math.PI/180) + deltaTheta/2);
				y += centerDistance*Math.cos(theta*(Math.PI/180) + deltaTheta/2);
				theta += (deltaTheta*(180/Math.PI)); //put theta in degrees
				theta = (360 + theta) % 360;
			}
			lastLeftDistance += leftDistance;
			lastRightDistance += rightDistance;
			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}
	
	public boolean isTurning(){
		double a = Math.abs(leftDistance-rightDistance);
		if (a > 1){
			return true;
		}
		return false;
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	
	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}

	public double getDis1() {
		return leftDistance;
	}

	public double getDis2() {
		return rightDistance;
	}
}