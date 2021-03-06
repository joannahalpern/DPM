

	
public class TestCalculatePosition {
	private static double[] angles = {0, 88.3, 138.4, 95.3};
	public static final double DIST_BTW_LS_AXLE = 12.5; //cm

	public static void main(String[] args){
		System.out.println("x = " + calculateCurrentPositionX());
		System.out.println("y = " + calculateCurrentPositionYOLLLLO());
	}
	
	
	
	private static double calculateCurrentPositionX(){
		double thetaX = (360- (angles[1]+angles[2]) )/2*Math.PI/180;
		double thetaY = (360- (angles[2]+angles[3]) )/2*Math.PI/180;
		double x = -1*DIST_BTW_LS_AXLE* Math.cos(thetaX);
		double y = -1*DIST_BTW_LS_AXLE* Math.cos(thetaY);
		return x;
	}
	private static double calculateCurrentPositionYOLLLLO(){
		double thetaX = (360- (angles[1]+angles[2]) )*Math.PI/180;
		double thetaY = (360- (angles[2]+angles[3]) )*Math.PI/180;
		double x = -1*DIST_BTW_LS_AXLE* Math.cos(thetaX/2);
		double y = -1*DIST_BTW_LS_AXLE* Math.cos(thetaY/2);
		return y;
	}
	
}
