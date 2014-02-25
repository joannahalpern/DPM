package Lab5;

public class PolarCoordinate {
	private double radius;
	private double angle;
	
	public PolarCoordinate(double radius, double angle) {
		this.radius = radius;
		this.angle = angle;
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
