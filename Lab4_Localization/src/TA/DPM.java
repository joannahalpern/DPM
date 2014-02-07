package TA;
import lejos.nxt.Motor;

// Main Class
public class DPM {
	public static void main(String [] args) {
		OdometerTA odo = new OdometerTA(Motor.A, Motor.B, 30, true);
		NavigationTA nav = new NavigationTA(odo);
		
		// move in a square
		nav.travelTo(0, 30);
		nav.travelTo(30, 30);
		nav.travelTo(30, 0);
		nav.travelTo(0, 0);
	}
}
