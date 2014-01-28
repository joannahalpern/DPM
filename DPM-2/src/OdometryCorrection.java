/*
 * DPM Lab 2 - OdometryCorrection
 * 
 * Harris Miller 260499543
 * Joanna Halpern 260410826
 */
import lejos.nxt.ColorSensor;
import lejos.nxt.SensorPort;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private static int lightVal = 0;
	private static final int LIGHT_THRESHOLD = 350;
	private double gridOffsetY = 15.24; //should be 0 if we want to use
	private double gridOffsetX = 15.24;
	private boolean firstLineX = false;
	private boolean firstLineY = false;
	private final double DISTANCE_BTWN_SENSOR_CENTER = 4.5;
	private final double DISTANCE_BTWN_LINES = 30.48;
	
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		ColorSensor sensor = new ColorSensor(SensorPort.S1);
		sensor.setFloodlight(true);
		sensor.setFloodlight(ColorSensor.Color.GREEN);

		while (true) {
			correctionStart = System.currentTimeMillis();

			double x, y, theta;

			lightVal = sensor.getNormalizedLightValue();
			if (lightVal < LIGHT_THRESHOLD) { // if it sees a line (because then the lightVal will decrease)
				theta = odometer.getTheta();

				if (isYdirection(theta)) { // affects y
					if (!firstLineY) { // if it has not seen a line while going
										// in the y direction yet
						calculateGridOffset(odometer, theta); 
						firstLineY = true;
					}

					y = odometer.getY();
					y = inversePositionY(y, theta);
					y = nearest30(y);
					y = centerPositionY(y, theta);
					odometer.setY(y);
				} else { // affects x
					if (!firstLineX) { // if it has not seen a line while going in the y direction yet
						calculateGridOffset(odometer, theta); // TODO: place
																// this properly
						firstLineX = true;
					}

					x = odometer.getX();
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

	public double nearest30(double value) {

		if (value < DISTANCE_BTWN_LINES) { 
			return DISTANCE_BTWN_LINES;
		}

		value = (int) (value * 100);
		int q = (int) value;
		int remainder = q % 3048; //TODO: change
		int dividend = q / 3048;
		int a = 0;
		if (remainder > 1524) {
			a = 3048;
		}
		return ((double) (dividend * 3048 + a)) / 100;
	}

	/*
	 * This method compensates for the fact that the robot's starting position
	 * is considered (0,0) by subtracting GRID_OFFSET from the initial (x,y).
	 * Then, it compensates for the fact that the sensor is ahead of the center
	 * axle by 4.5cm by adding or subtracting that value to x or y, based on the
	 * rotation of the robot (theta)
	 */
	public double centerPositionX(double x, double theta) {
		x -= gridOffsetX;
		double centerCorrection = DISTANCE_BTWN_SENSOR_CENTER;
		if (theta > 45 && theta < 135) { // x is changing positively
			x -= centerCorrection;
		} else { // x is changing negatively
			x += centerCorrection;
		}
		return x;
	}

	public double inversePositionX(double x, double theta) {
		double centerCorrection = DISTANCE_BTWN_SENSOR_CENTER;
		if (theta > 45 && theta < 135) { // x is changing positively
			x += centerCorrection;
		} else { // x is changing negatively
			x -= centerCorrection;
		}
		x += gridOffsetX;
		return x;
	}

	public double centerPositionY(double y, double theta) {
		y -= gridOffsetY;
		double centerCorrection = DISTANCE_BTWN_SENSOR_CENTER;
		if (theta > 315 || theta < 45) { // x is changing positively
			y -= centerCorrection;
		} else { // x is changing negatively
			y += centerCorrection;
		}
		return y;
	}

	public double inversePositionY(double y, double theta) {
		double centerCorrection = DISTANCE_BTWN_SENSOR_CENTER;
		if (theta > 315 || theta < 45) { // x is changing positively
			y += centerCorrection;
		} else { // x is changing negatively
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
			gridOffsetY = DISTANCE_BTWN_LINES - (deltaY + DISTANCE_BTWN_SENSOR_CENTER);
		} 
		else if (isNegativeYDirection(theta)) {
			gridOffsetY = deltaY + DISTANCE_BTWN_SENSOR_CENTER;
		} 
		else if (isPositiveXDirection(theta)) {
			gridOffsetX = DISTANCE_BTWN_LINES - (deltaX + DISTANCE_BTWN_SENSOR_CENTER);
		} 
		else {// is negative Y direction
			gridOffsetX = deltaX + DISTANCE_BTWN_SENSOR_CENTER;
		}
	}

	private boolean isPositiveYDirection(double theta) {
		if (theta >= 315 || theta <= 45) {
			return true;
		}
		return false;
	}

	private boolean isNegativeYDirection(double theta) {
		if (135 <= theta && theta <= 225) {
			return true;
		}
		return false;
	}

	private boolean isYdirection(double theta) {
		if (isPositiveYDirection(theta) || isNegativeYDirection(theta)) {
			return true;
		}
		return false;
	}

	private boolean isPositiveXDirection(double theta) {
		if (theta > 45 && theta < 135) {
			return true;
		}
		return false;
	}
}