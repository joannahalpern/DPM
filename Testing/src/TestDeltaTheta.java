

public class TestDeltaTheta {
	private static final int FWD_SPEED = 150;
	private static final double WIDTH = 14.9;
	private static final double WHEEL_RADIUS = 2.075;
	private static double xCurrent = 0;
	private static double yCurrent = 0;
	private static double thetaCurrent = 0;
	public static int dT = 0;
	
	public static void main(String[] args) {
		double deltaTheta = calculateDeltaTheta(-2, -2);
		System.out.println("deltaTheta = " + deltaTheta);
		System.out.println("Math.atan2(-2, -2)*180/Math.PI = " + Math.atan2(-2, -2)*180/Math.PI);
	}
	private static double calculateDeltaTheta(double xDes, double yDes) {//TODO: if problems: test angle function plz
		double thetaDestination = Math.atan2(yDes-yCurrent, xDes-xCurrent)*180/Math.PI - 90;// -90 b/c atan2 returns an angle relative to pos-x axis
		double deltaTheta = thetaDestination - thetaCurrent;
		dT = (int) deltaTheta;
		return deltaTheta;
	}
}
