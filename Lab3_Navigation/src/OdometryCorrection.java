/*
 * DPM Lab 3 - OdometryCorrection
 *
 * Harris Miller 260499543
 * Joanna Halpern 260410826
 */
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10; // How frequently the
														// correction iterates
														// in ms
	private Odometer odometer;
	private static int lightVal = 0; // the value read by the light sensor
	private static final int LIGHT_THRESHOLD = 350; // if lightVal is lower than
													// this number, we know
													// there is a dark surface
	// these values determine the origin on the physical grid. This sets the
	// origin to the center
	// of the first square, but these variables change
	// in our more general code (commented out throughout this class), which
	// sets the origin to the starting point of the robot
	private double gridOffsetY = 15.24;
	private double gridOffsetX = 15.24;

	// private boolean firstLineX = false;
	// private boolean firstLineY = false;

	// The distance between the light sensor and the center of the robot
	private final double DISTANCE_BTWN_SENSOR_CENTER = 4.5;
	// the distance btw grid lines
	private final double DISTANCE_BTWN_LINES = 30.48;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		// we have a color sensor in port S1, which emits green light
		ColorSensor sensor = new ColorSensor(SensorPort.S1);
		sensor.setFloodlight(true);
		sensor.setFloodlight(ColorSensor.Color.GREEN);

		while (true) {
			correctionStart = System.currentTimeMillis();

			double x, y, theta;

			lightVal = sensor.getNormalizedLightValue();
			if (lightVal < LIGHT_THRESHOLD) { // if it sees a line (because then
												// the lightVal will decrease
												// below LIGHT_THRESHOLD)
				theta = odometer.getTheta();

				if (isYdirection(theta)) { // affects y

					// if (!firstLineY) { // if it has not seen a line while
					// going
					// // in the y direction yet
					// calculateGridOffset(odometer, theta);
					// firstLineY = true;
					// }

					y = odometer.getY();

					// inversePosition maps the grid with the origin at
					// gridOffset(x, y) to a grid with the origin
					// at the bottom left of the physical tile the robot was
					// placed in. This is done because the next method,
					// nearest30() requires
					// a grid with the origin at the bottom left corner
					y = inversePositionY(y, theta);

					// nearest30 calculates the nearest grid line it is at so
					// that it can set the coordinate to that value
					y = nearest30(y);

					// This maps the origin of the grid back to the center of
					// the starting tile
					y = centerPositionY(y, theta);

					odometer.setY(y);
				} else { // affects x

					// if (!firstLineX) { // if it has not seen a line while
					// going in the y direction yet
					// calculateGridOffset(odometer, theta); // TODO: place
					// // this properly
					// firstLineX = true;
					// }

					x = odometer.getX();

					// the same process occurs here as with y (above)
					x = inversePositionX(x, theta);
					x = nearest30(x);
					x = centerPositionX(x, theta);
					odometer.setX(x);
				}
			}

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}

	/**
	 * What nearest30() does: This takes a value (which is the coordinate that
	 * the odometer is reporting at that time) and returns the multiple of 30.48
	 * that that value is closest to. This is only called once the sensor sees a
	 * line
	 * 
	 * How nearest30 works: -first it calculate the remainder of the input when
	 * divided by DISTANCE_BTWN_LINES -It later (in the if statement) uses the
	 * remainder to determine if it should return the higher or lower line
	 * coordinate -It next calculates the dividend of the input/tile length,
	 * which is the number of lines it has passed
	 * 
	 * Example: -If the given value is 40, the remainder will be about 10 (40
	 * mod 30). Since 10< half of 30, it returns the lower number. -The dividend
	 * is 1, so the lower number it is returning is 1*30 + 0 = 30.
	 * 
	 * -If the given value is 50, the remainder will be about 20 (50 mod 30).
	 * Since 20> half of 30, it returns the higher number. -The dividend is
	 * still 1, so the lower number it is returning is 1*30 + 30 = 60.
	 */
	public double nearest30(double value) {

		if (value < DISTANCE_BTWN_LINES) { // if it is in the first tile, it
											// returns the coordinate of the
											// first line
			return DISTANCE_BTWN_LINES;
		}

		value = (int) (value * 100); // multiplied by 100 for specificity in int
		int q = (int) value;
		int remainder = q % 3048; // uses mod to get remainder
		// 3048 is DISTANCE_BTWN_LINES*100
		int dividend = q / 3048; // since dividend is int, it is floored (eg
									// 2.4134 become 2)
		int a = 0;
		if (remainder > 1524) { // if remainder is greater than half of
								// DISTANCE_BTWN_LINES, then it goes to the
								// higher value, else the lower one
			a = 3048;
		}
		return ((double) (dividend * 3048 + a)) / 100; // divided by 100 to put
														// back to double in cm
														// (since we multiplyed
														// by 10 before)
	}

	/**
	 * This method compensates for the fact that the center of the first tile is
	 * considered (0,0) by subtracting gridOffset(x, y) from the initial (x,y).
	 * Then, it compensates for the fact that the sensor is ahead of the center
	 * axle by 4.5cm by adding or subtracting that value to x or y, based on the
	 * rotation of the robot (theta)
	 */
	public double centerPositionX(double x, double theta) {
		x -= gridOffsetX;
		double centerCorrection = DISTANCE_BTWN_SENSOR_CENTER;
		if (isPositiveXDirection(theta)) { // x is changing positively
			x -= centerCorrection;
		} else { // x is changing negatively
			x += centerCorrection;
		}
		return x;
	}

	public double centerPositionY(double y, double theta) {
		y -= gridOffsetY;
		double centerCorrection = DISTANCE_BTWN_SENSOR_CENTER;
		if (isPositiveYDirection(theta)) { // y is changing positively
			y -= centerCorrection;
		} else { // y is changing negatively
			y += centerCorrection;
		}
		return y;
	}

	/**
	 * The inversePosition functions do the exact inverse of the centerPostition
	 * functions for x and y This is needed because the method nearest30() only
	 * works when the origin is the physical bottom left corner of the starting
	 * tile. (15.24,15.24) is the origin we are given
	 */
	public double inversePositionX(double x, double theta) {
		double centerCorrection = DISTANCE_BTWN_SENSOR_CENTER;
		if (isPositiveXDirection(theta)) { // x is changing positively
			x += centerCorrection;
		} else { // x is changing negatively
			x -= centerCorrection;
		}
		x += gridOffsetX;
		return x;
	}

	public double inversePositionY(double y, double theta) {
		double centerCorrection = DISTANCE_BTWN_SENSOR_CENTER;
		if (isPositiveYDirection(theta)) { // y is changing positively
			y += centerCorrection;
		} else { // y is changing negatively
			y -= centerCorrection;
		}
		y += gridOffsetY;
		return y;
	}

	public static int getLightVal() {
		return lightVal;
	}

	public void calculateGridOffset(Odometer odometer, double theta) {
		double deltaY = odometer.getY();
		double deltaX = odometer.getX();

		if (isPositiveYDirection(theta)) {
			gridOffsetY = DISTANCE_BTWN_LINES
					- (deltaY + DISTANCE_BTWN_SENSOR_CENTER);
		} else if (isNegativeYDirection(theta)) {
			gridOffsetY = deltaY + DISTANCE_BTWN_SENSOR_CENTER;
		} else if (isPositiveXDirection(theta)) {
			gridOffsetX = DISTANCE_BTWN_LINES
					- (deltaX + DISTANCE_BTWN_SENSOR_CENTER);
		} else {// is negative Y direction
			gridOffsetX = deltaX + DISTANCE_BTWN_SENSOR_CENTER;
		}
	}

	/**
	 * This returns true if the robot is pointing in the positive y direction
	 */
	private boolean isPositiveYDirection(double theta) {
		if (theta >= 315 || theta <= 45) {
			return true;
		}
		return false;
	}

	/**
	 * This returns true if the robot is pointing in the negative y direction
	 */
	private boolean isNegativeYDirection(double theta) {
		if (135 <= theta && theta <= 225) {
			return true;
		}
		return false;
	}

	/**
	 * This returns true if the robot is pointing in the y direction
	 */
	private boolean isYdirection(double theta) {
		if (isPositiveYDirection(theta) || isNegativeYDirection(theta)) {
			return true;
		}
		return false;
	}

	/**
	 * This returns true if the robot is pointing in the positive x direction
	 */
	private boolean isPositiveXDirection(double theta) {
		if (theta > 45 && theta < 135) {
			return true;
		}
		return false;
	}
}