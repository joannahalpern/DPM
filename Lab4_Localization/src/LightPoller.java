/*
 * ECSE 211 Lab 1 - Group 53
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
		lsLocalizer.doLocalization();
	}

}
