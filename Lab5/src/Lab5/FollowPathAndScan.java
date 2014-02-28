package Lab5;

import java.util.Queue;
import java.util.Stack;

import lejos.geom.Point;

/**
 * This class has the robot navigate a specific path (to specific points). Every few seconds, the robot should
 * do a 180 scan to detect blocks. 
 * At each
 * 
 * If there is a block, it puts coordinates (of type Point) of that block into the stack.
 * It puts all the points into the stack at the same time (with synchronized updateLab5Stack())
 * 
 * See calculateBlockCoords() method to see how the block coordinates are determined from the scan
 * 
 * It starts obstacle avoidance only when moving to point and not while scanning
 * 
 * Path first takes robot to second half of board (60.96, 182.88)
 *
 *This should only execute while Lab5.navigate == true. Else, sleep.
 */
public class FollowPathAndScan extends Thread{
	private static final int DERIVATIVE_THRESHOLD = 9;
	Navigation nav;
	Odometer odo;
	UltrasonicPoller usPoller;
	LightPoller lsPoller;
	ObstacleAvoidance avoider;
	Queue<Coordinate> navQueue;
	Coordinate nextPoint = new Coordinate(0, 0);
	public static boolean usScanning = false;


	public FollowPathAndScan(Navigation nav, Odometer odo,
			UltrasonicPoller usPoller, LightPoller lsPoller, ObstacleAvoidance avoider) {
		this.nav = nav;
		this.odo = odo;
		this.usPoller = usPoller;
		this.lsPoller = lsPoller;
		this.avoider = avoider;
		this.navQueue = new Queue<Coordinate>();
		navQueue.addElement(new Coordinate(30.48, 152.4));
		navQueue.addElement(new Coordinate(30.48, 213.36));
		navQueue.addElement(new Coordinate(91.44, 213.36));
		navQueue.addElement(new Coordinate(91.44, 152.4));
	}
	
	public void run(){
		while (Lab5.navigate == true && (!navQueue.empty())){
			
			//travel to first point
			Coordinate nextPoint = (Coordinate) navQueue.pop();
			Lab5.obstacleAvoidance = true;
			nav.travelTo(nextPoint.getX(), nextPoint.getY());
			Lab5.obstacleAvoidance = false;
			
			//scan 180 and put detected blocks in queue
			nav.turnTo(-90, true);
			usScanning = true;
			Queue<PolarCoordinate> distanceQueue = collectDistances();
			nav.turnTo(90, true);
			usScanning = false;
			
			Stack<Point> blockStack = calculateBlockCoords(distanceQueue);
			updateLab5Stack(blockStack);

			avoider.setBlockThreshold(30);
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}
	
	/**
	 * Every 100 ms, a the median distance and angle of the robot is put into the distanceQueue of PolarCoordinates
	 * (see LightPoller for how median distance is calculated)
	 * @return
	 */
	private Queue<PolarCoordinate> collectDistances(){
		Queue<PolarCoordinate> distanceQueue = new Queue<PolarCoordinate>();
		while (FollowPathAndScan.usScanning == true){
			distanceQueue.push(new PolarCoordinate(usPoller.getMedianDistance(), odo.getAng()));
			try { Thread.sleep(100); } catch(Exception e){}
		}
		return distanceQueue;
	}
	
	
	/**
	 * Analyzes data and determines coordinates of blocks. 
	 * Looks at changes in distances to see when block starts and ends
	 * The derivative spike (as shown in class notes)
	 * 
	 * Puts distance differences into derivativeQueue. It then finds the next big negative derivative followed
	 * by a positive derivative and determines the midpoint between the negative and positive spikes. That 
	 * midpoint is  converted into it's cartesian coordinate and is considered the coordinate object. It is put
	 * in the blockStack.
	 * 
	 * Threshold will be 37 (diagonal of grid square - half of cube width)
	 * 
	 * if facing wall, ignore, unless distance is less than wall distance
	 * 
	 * @param distanceQueue
	 */
	private Stack<Point> calculateBlockCoords(Queue<PolarCoordinate> distanceQueue) {
		Stack<Point> blockStack = new Stack<Point>();
		Queue<PolarCoordinate> derivativeQueue = new Queue<PolarCoordinate>();
		int queueSize = distanceQueue.size()-1;
		
		for(int i = 0; i<queueSize; i++){ 
			PolarCoordinate firstPC = (PolarCoordinate) distanceQueue.pop();
			PolarCoordinate secondPC = (PolarCoordinate) distanceQueue.peek();
			
			derivativeQueue.push(firstPC.subtractRadius(secondPC));
		}
		
		PolarCoordinate negative;
		PolarCoordinate positive;
		PolarCoordinate difference;
		int negativePos;
		int positivePos;
		int middlePos;
		Queue<PolarCoordinate> tempQ = new Queue<PolarCoordinate>();
		Queue<PolarCoordinate> blockQ = new Queue<PolarCoordinate>();
		for(int j=0; j<queueSize; j++){
			difference = (PolarCoordinate) derivativeQueue.pop();
			
			if ( difference.getRadius() < -DERIVATIVE_THRESHOLD){//negative threshold
				negative = difference;
				negativePos = j;
				
				while (j<queueSize && !(difference.getRadius() > DERIVATIVE_THRESHOLD)){
					difference = (PolarCoordinate) derivativeQueue.pop();
					tempQ.push(difference);
					j++;
					if (difference.getRadius() > DERIVATIVE_THRESHOLD){
						positive = difference;
						positivePos = j;
						
						middlePos = positivePos-negativePos;
						for (int k = 0; k<(middlePos/2); k++){
							tempQ.pop();
						}
						blockQ.push((PolarCoordinate) tempQ.pop());
					}
				}
			}
		}
		return blockStack;
	}

	/**
	 * Put all Coordinates in stack in Lab5
	 */
	private synchronized void updateLab5Stack(Stack<Point> blockStack){
		while (!blockStack.empty()){
			Lab5.blockStack.push( blockStack.pop());
		}
	}
	
}
