import lejos.nxt.ColorSensor;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor ls;
	private Navigation nav;
	
	public LightLocalizer(Odometer odo, ColorSensor ls, Navigation nav) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		this.nav = nav;
		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		nav.travelTo(5,5);
		nav.travelTo(0,5);
		nav.travelTo(0,0);

		
	}

}
