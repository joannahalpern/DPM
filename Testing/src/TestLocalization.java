
public class TestLocalization {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		double lol = calculateCurrentAngle(315, 135);
		System.out.println(lol);
	}
	private static double calculateCurrentAngle (double angleA, double angleB) {
		double deltaTheta;
		if (angleA > angleB){
			deltaTheta = 45 - (angleA + angleB)/2;
		}
		else{
			deltaTheta = 225 - (angleA + angleB)/2;
		}
//		deltaTheta = 180 - (angleA + angleB)/2;
		return deltaTheta;
	}
}
