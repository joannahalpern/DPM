/*
 * Lab5- Group 53 - ObjectDetection
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.*;

public class ObjectDetection extends Thread {
	private static final double COLOUR_READING_THRESHOLD = 9;
	private static final double DETECTION_THRESHOLD = 15; //TODO set the detection range
	private static final double LOWER_FOAM_LIMIT = 380; //TODO set colour thresholds
	private static final double UPPER_FOAM_LIMIT = 540;
	private static final double LOWER_WOOD_LIMIT = 230;
	private static final double UPPER_WOOD_LIMIT = LOWER_FOAM_LIMIT;
	
	private UltrasonicPoller usPoller;
	private LightPoller lsPoller;
	private Navigation nav;
	private boolean objectDetected;
	private boolean block;
	
	public ObjectDetection(UltrasonicPoller usPoller, LightPoller lsPoller, Navigation nav){
		this.usPoller = usPoller;
		this.lsPoller = lsPoller;
		this.nav = nav;
		this.objectDetected = false;
		this.block = false;
	}
	
	public void run() {
		while(true){
			if (isBlockinRange(usPoller, DETECTION_THRESHOLD)){
				objectDetected = true; //LCD will now display "Object Detected"

				//check to see block is close enough to read colour. If not, then move forward.
				while (!isBlockinRange(usPoller, COLOUR_READING_THRESHOLD)){
					nav.setForward();
					nav.go(50);
				}
				nav.go(0);//stop
				BlockType blockType = identifyBlock(lsPoller);
				switch (blockType){
					case STYROFOAM:
						block = true; //display "Block" on LCD
						break;
					case WOOD:
						block = false; //display "Not Block" on LCD
						break;
					case UNKNOWN:
						block = false; //display "Not Block" on LCD
						break;
				}
				switch (blockType){
				case WOOD:
					
				}
			try { Thread.sleep(50); } catch(Exception e){}
		}
			objectDetected = false;
	}
		
	}

	public boolean isBlockinRange(UltrasonicPoller usPoller, double threshold){
		double distance = usPoller.getDistance();
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
//			block = true;
		}
		else if ((LOWER_WOOD_LIMIT) < colourVal && colourVal < (UPPER_WOOD_LIMIT)){
			blockType = BlockType.WOOD;
//			block = true;
		}
		else{
			blockType = BlockType.STYROFOAM;
//			block = false;
		}
		
		return blockType;
	}

	public boolean isObjectDetected() {
		return objectDetected;
	}

	public boolean isBlock() {
		return block;
	}

}