
import lejos.nxt.LCD;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDObjectDetection implements TimerListener{
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	private LightPoller lightPoller;
	private UltrasonicPoller usPoller;
	private ObjectDetection objectDetection;

	public LCDObjectDetection(Odometer odo, LightPoller lightPoller, UltrasonicPoller usPoller, ObjectDetection objectDetection) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this.lightPoller = lightPoller;
		this.usPoller = usPoller;
		this.objectDetection = objectDetection;
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		LCD.clear();
		
		BlockType blockType = objectDetection.getBlockType();
		switch (blockType){
		case STYROFOAM:
			LCD.drawString("STYROFOAM", 0, 5);
			break;
		case WOOD:
			LCD.drawString("WOOD", 0, 5);
			break;
		case UNKNOWN:
			LCD.drawString("UNKNOWN", 0, 5);
			break;
		}
		
		
//		LCD.drawString("Mean Colour= " + lightPoller.getMean(), 0, 1);
//		LCD.drawString("Mean Dis = " + usPoller.getMean(), 0, 1);
//		LCD.drawString("Median C = " + lightPoller.getMedian(), 0, 2);
		LCD.drawString("Median D = " + usPoller.getMedian(), 0, 2);

		
		LCD.drawString("Colour = " + lightPoller.getColourVal(), 0, 3);
		LCD.drawString("Distance = " + usPoller.getDistance(), 0, 4);
		
		if (objectDetection.isObjectDetected()){
			LCD.drawString("Object Detected", 0, 6);
			if (objectDetection.isBlock()){
				LCD.drawString("Block", 0, 7);
			}
			else{
				LCD.drawString("Not Block", 0, 7);
			}
		}
		else{
			LCD.drawString("Nothing Detected", 0, 6);
		}
		
	}
}
