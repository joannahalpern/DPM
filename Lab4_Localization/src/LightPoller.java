/*
 * Lab4- Group 53 - LightPoller
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.ColorSensor;


public class LightPoller extends Thread{
	private ColorSensor ls;
	private LightLocalizer lsLocalizer;
	private Navigation nav;
	
	public LightPoller(ColorSensor ls, LightLocalizer lsLocalizer, Navigation nav) {
		this.ls = ls;
		this.lsLocalizer = lsLocalizer;
		this.nav = nav;
	}
	
	public void run() {
		while(true){//sleep every half second until myMutex (permission variable) goes to 1
			if (Lab4.myMutex == 1){
				lsLocalizer.doLocalization();
				Lab4.myMutex=0; //this way it only runs once
			}
			else{
				try { Thread.sleep(500); } catch(Exception e){}
			}
		}
	}

}
