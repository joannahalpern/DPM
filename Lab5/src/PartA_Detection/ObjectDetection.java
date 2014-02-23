package PartA_Detection;


//
//if block 
import Lab5.*;
import lejos.nxt.*;

public class ObjectDetection extends Thread {
	private static final double COLOUR_READING_THRESHOLD = 9;
	private static final double DETECTION_THRESHOLD = 30; //TODO set the detection range
	private static final double LOWER_FOAM_LIMIT = 380; //TODO set colour thresholds
	private static final double UPPER_FOAM_LIMIT = 540;
	private static final double LOWER_WOOD_LIMIT = 230;
	private static final double UPPER_WOOD_LIMIT = LOWER_FOAM_LIMIT;
	
	private UltrasonicPoller usPoller;
	private LightPoller lsPoller;
	private Navigation nav;
	private boolean objectDetected;
	private boolean block;
	private BlockType blockType;
	
	public ObjectDetection(UltrasonicPoller usPoller, LightPoller lsPoller, Navigation nav){
		this.usPoller = usPoller;
		this.lsPoller = lsPoller;
		this.nav = nav;
		this.objectDetected = false;
		this.block = false;
		this.blockType = BlockType.UNKNOWN;
	}
	
	public void run() {
		while(true){
			doBlockDetection();
			try { Thread.sleep(100); } catch(Exception e){}
			objectDetected = false;
		}
	}
	
	public boolean doBlockDetection(){
			if (isBlockinRange(usPoller, DETECTION_THRESHOLD)){
				objectDetected = true; //LCD will now display "Object Detected"

				//check to see block is close enough to read colour. If not, then move forward.
				while (!isBlockinRange(usPoller, COLOUR_READING_THRESHOLD)){
					nav.setSpeeds(50, 50); //TODO: changed nav so need to retest that this works
				}
				nav.setSpeeds(0, 0);//stop
				blockType = identifyBlock(lsPoller);
				switch (blockType){
					case STYROFOAM:
						block = true; //displays "Block" on LCD
						break;
					case WOOD:
						block = false; //displays "Not Block" on LCD
						break;
					case UNKNOWN:
						block = false; //displays "Not Block" on LCD
						break;
					default:
						block = false;
						break;
				}
			}
			return block;
	}

	public boolean isBlockinRange(UltrasonicPoller usPoller, double threshold){
		double distance = usPoller.getMeanDistance();
		if (distance< threshold){
			return true;
		}
		return false;
	}
	
	public BlockType identifyBlock(LightPoller lsPoller){
		try { Thread.sleep(lsPoller.POLLING_PERIOD*5); } catch(Exception e){}
		double colourVal = lsPoller.getColourVal();
		//TODO try lsPoller.getMedian() and lsPoller.getMean()
		BlockType blockType;
		
		if ((LOWER_FOAM_LIMIT < colourVal) && (colourVal < UPPER_FOAM_LIMIT)){
			blockType = BlockType.STYROFOAM;
		}
		else if ((LOWER_WOOD_LIMIT) < colourVal && colourVal < (UPPER_WOOD_LIMIT)){
			blockType = BlockType.WOOD;
		}
		else{
			blockType = BlockType.UNKNOWN;
		}
		
		return blockType;
	}

	public boolean isObjectDetected() {
		return objectDetected;
	}

	public boolean isBlock() {
		return block;
	}
	
	public BlockType getBlockType(){
		return blockType;
	}

}