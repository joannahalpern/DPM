package Lab5;
/**
 * While Lab5.obstacleAvoidance == true:
 * 
 * if us sensor sees something within threshold, it stops navigation (navigation = false), and turns right.
 * It then checks if something's in front, if it is it turns right again. If not, moves forward an amount d (d should be bigger than a block),
 * then it sets navigation to true again. (some of this should be in a synchronized lock)
 * 
 * if block is detected near coordinate, obstacle avoidance should stop (Lab5.obstacleAvoidance = false)
 * 
 * TODO test ObstacleAvoidance
 */
public class ObstacleAvoidance extends Thread {
	private static final double BLOCK_THRESHOLD = 30;
	Navigation nav;
	UltrasonicPoller usPoller;
	Odometer odo;
	
	
	public ObstacleAvoidance(Navigation nav, UltrasonicPoller usPoller, Odometer odo) {
		this.nav = nav;
		this.usPoller = usPoller;
		this.odo = odo;
	}
	
	public void run(){
		avoidObstacles();
	}
	public void avoidObstacles(){
		while (Lab5.obstacleAvoidance == true){
			if (isBlockinRange(usPoller, BLOCK_THRESHOLD)){
				
				synchronized (Lab5.lock) {
					Lab5.navigate = false;
					do{
						nav.turnTo( (90+odo.getAng()), true);
					} while (isBlockinRange(usPoller, BLOCK_THRESHOLD));
					nav.goForward(BLOCK_THRESHOLD-2); //TODO adjust distance
					Lab5.navigate = true;
				}
			}
			try { Thread.sleep(500); } catch(Exception e){} //TODO adjust sleep time
			
		}
	}


	public boolean isBlockinRange(UltrasonicPoller usPoller, double threshold){
		double distance = usPoller.getMeanDistance();
		if (distance< threshold){
			return true;
		}
		return false;
	}
}