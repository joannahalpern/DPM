/*

 * Lab4- Group 53 - UltrasonicPoller
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.UltrasonicSensor;

//This code is what was given in lab 1 except that we added myMutex
public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private USLocalizer uSLocalizer;
	private double distance = 99999;
	
	public UltrasonicPoller(UltrasonicSensor us, USLocalizer uSLocalizer) {
		this.us = us;
		this.uSLocalizer = uSLocalizer;
	}
	
	public void run() {
		while(true){
		distance = us.getDistance();
		try { Thread.sleep(50); } catch(Exception e){}
		}
	}
	
	public double getDistance(){
		return distance;
	}

}
