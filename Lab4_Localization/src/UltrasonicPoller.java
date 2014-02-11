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
	
	public UltrasonicPoller(UltrasonicSensor us, USLocalizer uSLocalizer) {
		this.us = us;
		this.uSLocalizer = uSLocalizer;
	}
	
	public void run() {
		uSLocalizer.doLocalization();
		Lab4.myMutex = 1; //after the ultra sonic localization is complete, myMutex is changed
						 //to 1 which is the condition for the LightPoller to begin
	}

}
