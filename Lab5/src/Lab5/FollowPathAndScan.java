package Lab5;

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
 *TODO: Fill in FollowPathAndScan
 */
public class FollowPathAndScan extends Thread{
	Navigation nav;
	Odometer odo;
	UltrasonicPoller usPoller;
	LightPoller lsPoller;
	ObstacleAvoidance avoider;


	public FollowPathAndScan(Navigation nav, Odometer odo,
			UltrasonicPoller usPoller, LightPoller lsPoller, ObstacleAvoidance avoider) {
		this.nav = nav;
		this.odo = odo;
		this.usPoller = usPoller;
		this.lsPoller = lsPoller;
		this.avoider = avoider;
	}
	
	public void run(){
		
	}
	
	private synchronized void updateStack(){
		
	}
	
}
