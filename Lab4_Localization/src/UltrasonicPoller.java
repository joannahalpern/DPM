/*
 * Lab4- Group 53 - UltrasonicPoller
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.UltrasonicSensor;


public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private USLocalizer uSLocalizer;
	
	public UltrasonicPoller(UltrasonicSensor us, USLocalizer uSLocalizer) {
		this.us = us;
		this.uSLocalizer = uSLocalizer;
	}
	
	public void run() {
		uSLocalizer.doLocalization();
		Lab4.myMutex = 1;
	}

}
