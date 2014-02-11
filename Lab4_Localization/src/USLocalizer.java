/*
 * ECSE 211 Lab 4 - Group 53
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

import lejos.nxt.Motor;
import lejos.nxt.UltrasonicSensor;
//TODO: add some kind of error-catcher for US readings
public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE};

	private Navigation nav;
	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	private static double distance = 9999; //this is distance to the wall as seen by ultra sonic sensor
										   //this value will change as soon as the sensor starts polling
	private static double angleA, angleB;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType, Navigation nav) {
		this.nav = nav;
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		
		
		// switch off the ultrasonic sensor
//		us.off();
	}
	/**
	 * The Robot will determine it's current angle by detecting walls with the ultra sonic sensor
	 * and recording the angles that the odometer reads when it sees a wall. It then uses those
	 * recorded angles to calculate it's current angle.
	 */
	public void doLocalization() {
		
		double [] pos = new double [3];
		//Case: FALLING EDGE
		if (locType == LocalizationType.FALLING_EDGE) {
			
			//Robot turns clockwise until it see a wall
			//if it already doesn't see a wall, it skips this first while loop
			nav.setClockwise();
			while (isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
			//Now the robot is facing a wall and it continues rotating clockwise until
			//it no longer sees a wall
			//It then stops and latches it's current angle
			}
			while (!isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
			}
			Motor.A.stop(true);
			Motor.B.stop(false);
			
			odo.getPosition(pos);
			angleA = pos[2];
			
			//The robot then turns counter clockwise until is sees a wall
			nav.setCounterClockwise();
			while (isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
			}
			//Then the robot continues counter clockwise until it doesn't see a wall
			//It will then latch that angle
			while (!isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
			}
			Motor.A.stop(true);
			Motor.B.stop(false);
			
			odo.getPosition(pos);
			angleB = pos[2];
			
			//Using the 2 latched angles, it calculates what it's current angle must be and puts that between 0 and 360 degrees
			double currentAngle = fixDegAngle(-calculateChangeAngleToZero(angleA, angleB, locType));
			
			odo.setPosition(new double [] {0.0, 0.0, currentAngle}, new boolean [] {true, true, true});
		} 
		else{//Case: RISING EDGE
			nav.setClockwise();
			//robot passes first wall if it has started not facing the wall
			while (!isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
				if (isWallSeen()){ //if a wall is seen, it waits before checking again so that it doesn't skip the
								   //next part and latch an angle too soon
					try { Thread.sleep(500); } catch(Exception e){}
				}
			}
			//robot turns until it stops seeing the wall and it latches that angle
			while (isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
			}
			Motor.A.stop(true);
			Motor.B.stop(false);
			
			odo.getPosition(pos);
			angleA = pos[2];
			
			
			//Robot turns counterclockwise until it sees a wall again
			nav.setCounterClockwise();
			while (!isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
				if (isWallSeen()){ //if a wall is seen, it waits before checking again so that it doesn't skip the
								   //next part and latch an angle too soon
					try { Thread.sleep(500); } catch(Exception e){}
				}
			}
			//Robot turns counter clockwise until it doesn't see a wall again
			//It then latches that angle
			while (isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
			}
			odo.getPosition(pos);
			angleB = pos[2];

			Motor.A.stop(true);
			Motor.B.stop(true);
			
			//Using the 2 latched angles, it calculates what it's current angle must be and puts that between 0 and 360 degrees
			double currentAngle = fixDegAngle(-calculateChangeAngleToZero(angleA, angleB, locType));
			
			odo.setPosition(new double [] {0.0, 0.0, currentAngle}, new boolean [] {true, true, true});
			}
		return;
	}
	
	/**
	 * If a wall is within threshold of 32cm, it returns true. 
	 * Else returns false
	 */
	public boolean isWallSeen(){
		distance = getFilteredData();

		if (distance < 32){
			return true;
		}
		return false;
	}
	
	public static double getDistance() {
		return distance;
	}

	/**
	 * Returns distance seen by ultra sonic sensor.
	 * If the read distance is greater than 50, it will just return 50
	 */
	private int getFilteredData() {
		int distance = us.getDistance();

		if (distance > 50){
			distance = 50;
		}
		return distance;
	}

	/**
	 * Calculates robot's current angle based on the two latched angles
	 */
	private double calculateChangeAngleToZero(double angleA, double angleB, LocalizationType type) {
		double deltaTheta=999999; //this value will be changed in the cases
		angleA= backToRawAngles(angleA); //puts angle between -180 and 180 (to simplify calculations)
		angleB= backToRawAngles(angleB);
		
		double middleAngle = (angleA+angleB)/2;
		switch (type){
			case FALLING_EDGE:
				if(angleA<0){ //This is Falling Edge where the robot started facing away from wall
					deltaTheta = (135-(angleB-middleAngle)); //formula calculated using trigonometry
				}
				else{//Falling Edge where the robot started facing the wall
					deltaTheta = (-45-(angleB-middleAngle));
				}
				break;
			case RISING_EDGE:
				if(angleA>0){ //This is Rising Edge where the robot started facing away from wall
					deltaTheta = (135-(angleB-middleAngle));
				}
				else{//Rising edge where the robot started facing away from wall
					deltaTheta = (-45-(angleB-middleAngle));
				}
		}
		return deltaTheta;

	}
	
	public static double getAngleA1() {
		return angleA;
	}

	public static double getAngleA2() {
		return angleA;
	}

	public static double getAngleB1() {
		return angleB;
	}

	public static double getAngleB2() {
		return angleB;
	}
	/**
	 * Converts angle
	 * @param inAngle between 0 and 360
	 * @returns equivalent angle between -180 and 180
	 */
	private double backToRawAngles(double inAngle){
		if(inAngle>180){
			return inAngle-360;
		}
		return inAngle;
	}
	/**
	 * Converts angle
	 * @param any angle
	 * @returns equivalent angle between 0 and 360
	 */
	public static double fixDegAngle(double angle) {		
		while (angle < 0.0){
			angle += 360;
		}
		angle = angle % 360.0;
		return angle;
	}
}
