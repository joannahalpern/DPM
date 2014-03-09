package Lab5;

import lejos.nxt.UltrasonicSensor;
import Lab5.USLocalizer.LocalizationType;

/**
 * Assuming we are starting at the origin, the robot will rotate counterclockwise until the ultrasonic sensor no longer sees a wall 
 * (meaning it's facing about 135 degrees). It will then rotate clockwise and will record what angle it's at when
 * the colour sensor detects a grid line
 */
public class LocalizationAtOrigin {
	private Navigation nav;
	private Odometer odo;
	private UltrasonicPoller usPoller;
	private LightPoller lsPoller;
										   //this value will change as soon as the sensor starts polling
	private static double distance = 9999; //this is distance to the wall as seen by ultra sonic sensor
	private static double angleA, angleB;

	public LocalizationAtOrigin(Odometer odo, Navigation nav, UltrasonicPoller usPoller, LightPoller lsPoller) {
		this.nav = nav;
		this.odo = odo;
		this.usPoller = usPoller;
		this.lsPoller = lsPoller;
	}

	public static void localizeFromOrigin(){
	}
}
