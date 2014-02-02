/*
 * ECSE 211 Lab 1 - Group 53
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth; // essentially ideal distance and error
	private final int motorLow, motorHigh; //Used "correction" variables instead
	private final int motorStraight = 100;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private final int correction = 50;// added/subtracted to motorStraight based on distance
	private final int sharpCorrection = 75;// added/subtracted to motorStraight based on extreme distances
	private int distance;
	public int counter;// used for gaps/sharp-left turns as explained below

//thresholds 
	private final int counterThreshold = 150;
	private final int closeThreshold = 9;
	
	
	public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		counter = 0;//counter starts at 0
	}
	private void continueStraight() { //motorSpeed default: move straight
		rightMotor.forward();
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
	}
	private void moveFurther() { //motorSpeed correction to turn right
		rightMotor.forward();
		leftMotor.setSpeed(motorStraight + correction);
		rightMotor.setSpeed(motorStraight - correction);
	}
	private void moveCloser() { //motorSpeed correction to turn left
		rightMotor.forward();
		leftMotor.setSpeed(motorStraight - correction);
		rightMotor.setSpeed(motorStraight + correction);
	}
	
	private void sharpLeft(){ //for extreme distance correction to the left
		rightMotor.forward();
		leftMotor.setSpeed(motorStraight - correction);
		rightMotor.setSpeed(motorStraight + sharpCorrection - 10);//we experimented with these values until they worked
	}
	private void sharpRight(){ //for extreme distance correction to the right		
		leftMotor.setSpeed(motorStraight + sharpCorrection);
		rightMotor.setSpeed(0);

	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;//measured distance
		int tooClose = bandCenter - bandwidth;
		int tooFar = bandCenter + bandwidth;
		
		
		//case gap -- once distance passes 60cm, 
		//robot keeps moving forward until the counter reaches counterThreshold (gaps, turns etc..)
		if (distance > 60){
			if (counter < counterThreshold){// increments every sensor ping (10 milliseconds) without seeing a wall
				continueStraight();
				counter++;
			}
			else{
				sharpLeft(); //once counter passes counterThreshold, make a sharpLeft
			}
		}
		
		//case concave corner
		else {
			counter = 0;//reset counter when sensor detects a close wall again
			if (distance< closeThreshold){ //
				sharpRight();
			}
			else{
				if(distance<tooClose){//the backbone of the function
					moveFurther();
				}
				else if(distance>tooFar){// ""
					moveCloser();
				}
				else{
					continueStraight();// ""
				}
			}	
		}		
	}
	
	
	public int getCounter() {
		return this.counter; //used so we can print counter value
	}
	
	public int readLeftSpeed(){//used in P controller-not here-, mandated by interface
		return 0;
	}
	public int readRightSpeed(){//used in P controller-not here-, mandated by interface
		return 0;
	}

	@Override
	public int readUSDistance() {//used so we can print distance value
		return this.distance;
	}
}
