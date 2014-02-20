package Lab5;

import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.RConsole;
import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class RConsoleDisplay implements TimerListener{
	public static final int LCD_REFRESH = 143;
	private Odometer odo;
	private Timer lcdTimer;
	private LightPoller lightPoller;
	private UltrasonicPoller usPoller;
	private USLocalizer usl;

	
	// arrays for displaying data
	private double [] pos;
	
	public RConsoleDisplay(Odometer odo, LightPoller lightPoller, UltrasonicPoller usPoller, USLocalizer usl) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.lightPoller = lightPoller;
		this.usPoller = usPoller;
		this.usl = usl;

		// start the timer
		lcdTimer.start();
		RConsole.println("Y Position  |  Median Distance |  Mean Distance |  Distance");
	}
	
	public void timedOut() { 
		RConsole.println(odo.getY() + ", " + usPoller.getMedianDistance() + ", " + usPoller.getMeanDistance() + ", " + usPoller.getDistance());
	}
}
