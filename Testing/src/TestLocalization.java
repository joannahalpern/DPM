
public class TestLocalization {

	public static void main(String[] args) {

//		double lol = calculateChangeAngleToZeroFE(160.3, 355);
		double lol = calculateChangeAngleToZeroRE(305, 135);
		System.out.println(lol);
	}
	private static double calculateChangeAngleToZeroFE(double angleA, double angleB, boolean risingEdge) {
		angleA= backToRawAngles(angleA);
		angleB= backToRawAngles(angleB);
		
		double centerAngle = (angleA+angleB)/2;
		if(angleA<0){
			return (135-(angleB-centerAngle));
		}
		else{
			return (-45-(angleB-centerAngle));
		}

	}
	private static double calculateChangeAngleToZeroRE(double angleA, double angleB) {
		angleA= backToRawAngles(angleA);
		angleB= backToRawAngles(angleB);
		
		double centerAngle = (angleA+angleB)/2;
		if(angleA>0){
			return (135-(angleB-centerAngle));
		}
		else{
			return (-45-(angleB-centerAngle));
		}

	}
	private static double backToRawAngles(double inAngle){
		if(inAngle>180){
			return inAngle-360;
		}
		return inAngle;
	}
}
