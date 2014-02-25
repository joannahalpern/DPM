package Lab5;
/**
 * While Lab5.obstacleAvoidance == true:
 * 
 * if us sensor sees something within threshold, it stops navigation (navigation = false), and turns right.
 * It then checks if something's in front, if it is it turn right again. If not, moves forward an amount d (d should be bigger than a block),
 * then it sets navigation to true again. (some of this should be in a synchronized lock)
 */
public class ObstacleAvoidance extends Thread {
	Navigation nav;
	UltrasonicPoller usPoller;
	
	public ObstacleAvoidance(Navigation nav, UltrasonicPoller usPoller) {
		this.nav = nav;
		this.usPoller = usPoller;
	}
	
	public void run(){
		avoidObstacles();
	}
	public void avoidObstacles(){
		while (Lab5.obstacleAvoidance == true){
			
		}
	}
}
