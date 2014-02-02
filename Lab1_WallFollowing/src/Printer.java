/*
 * ECSE 211 Lab 1 - Group 53
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.Button;
import lejos.nxt.LCD;

public class Printer extends Thread {
	
	private UltrasonicController cont;
	private final int option;
	
	public Printer(int option, UltrasonicController cont) {
		this.cont = cont;
		this.option = option;
	}
	
	public void run() {
		while (true) {
			LCD.clear();
			LCD.drawString("Controller Type is... ", 0, 0);
			if (this.option == Button.ID_LEFT){
				LCD.drawString("BangBang", 0, 1);
				LCD.drawString("counter = " + cont.getCounter(), 0, 4);//prints the counter value


			}
			else if (this.option == Button.ID_RIGHT)
				LCD.drawString("P type", 0, 1);
				LCD.drawString("US Distance: " + cont.readUSDistance(), 0, 2 );
				LCD.drawString("counter = " + cont.getCounter(), 0, 4);	//prints the counter value
				LCD.drawString("Left Speed: " + cont.readLeftSpeed(), 0, 5);//prints the left motor speed value
				LCD.drawString("Right Speed: " + cont.readRightSpeed(), 0, 6);//prints the right motor speed value
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	public static void printMainMenu() {
		LCD.clear();
		LCD.drawString("left = bangbang",  0, 0);
		LCD.drawString("right = p type", 0, 1);
	}
}
