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
	private static double distance = 9999;
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
			
			
			//
			nav.setCounterClockwise();
			while (!isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
				if (isWallSeen()){ //if a wall is seen, it waits before checking again so that it doesn't skip the
								   //next part and latch an angle too soon
					try { Thread.sleep(250); } catch(Exception e){}
				}
			}
			while (isWallSeen()){
				Navigation.go(Navigation.ROTATION_SPEED);
			}
			odo.getPosition(pos);
			angleB = pos[2];

			Motor.A.stop(true);
			
			double currentAngle = fixDegAngle(-calculateChangeAngleToZero(angleA, angleB, locType));
			
			odo.setPosition(new double [] {0.0, 0.0, currentAngle}, new boolean [] {true, true, true});
			}
		return; //TODO: check return
	}
	
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

	private int getFilteredData() {
		int distance = us.getDistance();

		if (distance > 50){
			distance = 50;
		}
		return distance;
	}

	private double calculateChangeAngleToZero(double angleA, double angleB, LocalizationType type) {//falling edge
		double deltaTheta=999999;
		angleA= backToRawAngles(angleA);
		angleB= backToRawAngles(angleB);
		
		double centerAngle = (angleA+angleB)/2;
		switch (type){
			case FALLING_EDGE:
				if(angleA<0){
					deltaTheta = (135-(angleB-centerAngle));
				}
				else{
					deltaTheta = (-45-(angleB-centerAngle));
				}
				break;
			case RISING_EDGE:
				if(angleA>0){
					deltaTheta = (135-(angleB-centerAngle));
				}
				else{
					deltaTheta = (-45-(angleB-centerAngle));
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

	private double backToRawAngles(double inAngle){
		if(inAngle>180){
			return inAngle-360;
		}
		return inAngle;
	}
	
	public static double fixDegAngle(double angle) {		
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);
		
		return angle % 360.0;
	}
}
