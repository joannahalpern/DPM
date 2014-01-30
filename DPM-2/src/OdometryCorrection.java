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
	private static double gridOffsetY = 15.24;
	private static double gridOffsetX = 15.24;
	private static boolean firstLineX = false;
	private static boolean firstLineY = false;

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
			if (lightVal < LIGHT_THRESHOLD) { //TODO: is this saying "if it does not see a line"?
				calculateGridOffset(odometer);	//TODO: place this properly
				
				theta = odometer.getTheta();

				if (theta > 315 || theta < 45 || (135 < theta && theta < 225)) { // affects y
					y = odometer.getY();

					y = inversePositionY(y, theta);
					y = nearest30(y);
					y = centerPositionY(y, theta);
					odometer.setY(y);
				} 
				else { // affects x

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

	public static double nearest30(double value) {

		if (value < 30.48) { // if
			return 30.48;
		}

		value = (int) (value * 100);
		int q = (int) value;
		int remainder = q % 3048;
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
		double centerCorrection = 4.5;
		if (theta > 45 && theta < 135) { // x is changing positively
			x -= centerCorrection;
		} else { // x is changing negatively
			x += centerCorrection;
		}
		return x;
	}

	public double inversePositionX(double x, double theta) {
		double centerCorrection = 4.5;
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
		double centerCorrection = 4.5;
		if (theta > 315 || theta < 45) { // x is changing positively
			y -= centerCorrection;
		} else { // x is changing negatively
			y += centerCorrection;
		}
		return y;
	}

	public double inversePositionY(double y, double theta) {
		double centerCorrection = 4.5;
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

	public static double calculateGridOffset(Odometer odometer) {
		double theta = odometer.getTheta();
				
		if (!firstLineY) { // if firstLineY has not been read
			firstLineY = true;
			double deltaY = odometer.getY();
			if (theta > 315 || theta < 45) {
				gridOffsetY = 30.48 - (deltaY + 4.5);
			} else if (135 < theta && theta < 225) {
				gridOffsetY = deltaY + 4.5;
			}
		} 
		else if (!firstLineX) { // if firstLineX has not been read
			firstLineX = true;
			double deltaX = odometer.getX();
			if (theta > 45 && theta < 135) {
				gridOffsetX = 30.48 - (deltaX + 4.5);
			} else {
				gridOffsetX = deltaX + 4.5;
			}
		}
		return 0;
	}
}