/*
 * ECSE 211 Lab 1 - Group 53
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class PController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorStraight = 150; 
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private final int slope = 3; //used in calculating function for motor speed
	private int distance;
	private int counter; //used for gaps and left turns (see BangBang)
	private int leftSpeed;
	private int rightSpeed;

	private final int counterThreshold = 110; //(see BangBang)
	
	public PController(int bandCenter, int bandwith) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftSpeed = 100;
		rightSpeed = 100;
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
		leftMotor.forward();
		rightMotor.forward();
		counter = 0; //counter starts at 0
	}
	
	/*This uses functions to determine the motor speeds.
	 * For distances <= 30, it uses a quadratic function to make a smoother turn
	 * For distance > 30, it uses a linear function
	 */
	private void motorsGo(){
		if(distance<=30){
			leftSpeed = (int) (-0.167*distance*distance +300);
			rightSpeed = (int) (0.167*distance*distance);
		}
		else{
			leftSpeed = -1*slope*distance + 210; 
			rightSpeed = (int) (.7*slope*distance + 90);
			if(leftSpeed<0){
				leftSpeed = 0;
			}
		}
		leftMotor.forward(); //used in case of motor speeds to high it resets the motors
		rightMotor.forward();
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
	}
	
	//this method is called when there might be a gap and counter is increasing
	private void continueStraight() { 
		rightMotor.forward();
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
	}
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		
		//case: gap or left turn
		if (distance > 60){ //if the sensor is reading a high value, it assumes it's a gap or left turn
			//case: gap
			if (counter < counterThreshold){  
				continueStraight();
				counter++;
			}
			//case: left turn
			else{
				motorsGo();
			}
		}
		//default case (for left turns and non extreme circumstances)
		else{
			counter = 0; //counter resets when sensor reads a value<=60
			motorsGo();
		}
		
	}
	public int getCounter() { //used for printer
		return this.counter;
	}
	
	public int readLeftSpeed(){//""
		return leftSpeed;
	}
	public int readRightSpeed(){//""
		return rightSpeed;
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
