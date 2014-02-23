package Lab5;

import java.util.Stack;

/**
 * This class has the robot navigate a specific path (to specific points). Every few seconds, the robot should
 * do a 180 scan to detect blocks. If there is a block, it puts coordinates of that block into the stack.
 *
 */
public class FollowPathAndScan extends Thread{
	Navigation nav;
	Odometer odo;
	UltrasonicPoller usPoller;
	LightPoller lsPoller;

	Stack stack;

	public FollowPathAndScan(Navigation nav, Odometer odo,
			UltrasonicPoller usPoller, LightPoller lsPoller) {
		this.nav = nav;
		this.odo = odo;
		this.usPoller = usPoller;
		this.lsPoller = lsPoller;
		
		this.stack = new Stack();
	}
	
	public void run(){
		
	}
	
}
