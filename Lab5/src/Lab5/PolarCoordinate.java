package Lab5;

public class PolarCoordinate {
	private double radius;
	private double angle;
	
	public PolarCoordinate(double radius, double angle) {
		this.radius = radius;
		this.angle = angle;
	}

	/**
	 * subtracts radius1 - radius2. Keeps the angle of the second PolarCoordinate
	 * @param secondPC
	 * @return
	 */
	public PolarCoordinate subtractRadius(PolarCoordinate secondPC){
		PolarCoordinate newPC = new PolarCoordinate(radius - secondPC.radius, secondPC.angle);
		return newPC;
	}
	
	public double getRadius() {
		return radius;
	}

	public double getAngle() {
		return angle;
	}

	public void setDistance(double radius) {
		this.radius = radius;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}
	
}
