package Lab5;

import java.util.Queue;
import java.util.Stack;

import lejos.geom.Point;

/**
 * This class has the robot navigate a specific path (to specific points). Every few seconds, the robot should
 * do a 180 scan to detect blocks. If there is a block, it puts coordinates (of type Point) of that block into the stack.
 * It should put all the points into the stack at the same time (synchronized)
 * 
 * It should also start obstacle avoidance only when moving to point and not while scanning
 * 
 * Path should first take robot to second half of board (60.96, 182.88)
 *
 *This should only execute while Lab5.navigate == true. Else, sleep.
 *
 *TODO: Test FollowPathAndScan
 *TODO: case where block is on point
 */
public class FollowPathAndScan extends Thread{
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

			try { Thread.sleep(10); } catch(Exception e){} //TODO: test without this sleep
		}
	}
	
	//TODO: move this to uspoller
	private Queue<PolarCoordinate> collectDistances(){
		Queue<PolarCoordinate> distanceQueue = new Queue<PolarCoordinate>();
		while (FollowPathAndScan.usScanning == true){
			distanceQueue.push(new PolarCoordinate(usPoller.getMedianDistance(), odo.getAng()));
			try { Thread.sleep(100); } catch(Exception e){}
			//TODO: timer
		}
		return distanceQueue;
	}
	
	
	/**
	 * Analyzes data and determines coordinates of blocks. 
	 * Look at changes in distances to see when block starts and ends
	 * The derivative spike (see class notes)
	 * 
	 * TODO: Hard code walls in
	 * Threshold will be 37 (diagonal of grid square - half of cube width)
	 * 
	 * if facing wall, ignore, unless distance is less than wall distance
	 * 
	 * TODO: Fill this in
	 * @param distanceQueue
	 */
	private Stack<Point> calculateBlockCoords(Queue<PolarCoordinate> distanceQueue) {
		Stack<Point> blockStack = new Stack<Point>();
		
//		Discrete Derivative - from notes
//		d1 = 0
//		for i = 2 to N
//		    di = xi – xi+1

		
		return blockStack;
	}

	/**
	 * Put all Coordinates in stack in Lab5. Need to convert to Points (or change Lab5 to coordinate)
	 * @param blockQueue
	 */
	private synchronized void updateLab5Stack(Stack<Point> blockStack){
		while (!blockStack.empty()){
			Lab5.blockStack.push( blockStack.pop());
		}
	}
	
}
