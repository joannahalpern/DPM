import lejos.nxt.*;

public class Lab4 {

	public static void main(String[] args) {
		/*
		 * Wait for startup button press
		 * Button.ID_LEFT = BangBang Type
		 * Button.ID_RIGHT = P Type
		 */
		
		int option = 0;
		while (option == 0)
			option = Button.waitForAnyPress();
		
		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		LCDInfo lcd = new LCDInfo(odo);
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
//		LightSensor ls = new LightSensor(SensorPort.S1);
		// perform the ultrasonic localization
		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
		UltrasonicPoller usPoller = new UltrasonicPoller(us, usl);
		
		
		switch(option) {
		case Button.ID_LEFT:
			usPoller.start();
			break;
		case Button.ID_RIGHT:
			usPoller.start();
			break;
		default:
			System.out.println("Error - invalid button");
			System.exit(-1);
			break;
		}
		
		Button.waitForAnyPress();
		System.exit(0);
	}
}
		/**
		 * our stuff
		 */
		// setup the odometer, display, and ultrasonic and light sensors
//		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
//		Odometer odo = new Odometer(patBot, true);
//		LCDInfo lcd = new LCDInfo(odo);
//		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
////		LightSensor ls = new LightSensor(SensorPort.S1);
//		// perform the ultrasonic localization
//		USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
//		UltrasonicPoller usPoller = new UltrasonicPoller(us, usl);
//		usPoller.start();
		
//		usl.doLocalization(); //this will set the odometer to the correct theta
		
		// perform the light sensor localization
//		LightLocalizer lsl = new LightLocalizer(odo, ls);
//		lsl.doLocalization();			
//		
//		Button.waitForAnyPress();
//		System.exit(0);
//		
//	}
//
//}
