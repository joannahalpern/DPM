package Lab5;

import lejos.geom.Point;

/**
 * Coordinate in both Cartesian and polar
 * @author Joanna
 *
 */
public class Coordinate{
	private double x;
	private double y;
	private double radius;
	private double angle;
	
	/**
	 * Create a Cartesian Coordinate
	 * @param x
	 * @param y
	 */
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
		this.radius = calculateRadius();
		this.angle = calculateAngle();
	}
	
	/**
	 * Create a polar coordinate if polar is set to true or Cartesian if polar is set to false
	 * @param x
	 * @param y
	 * @param polar
	 */
	public Coordinate(double radiusOrX, double angleOrY, boolean polar) {
		if (polar){
			this.x = calculateX();
			this.y = calculateY();
			this.radius = radiusOrX;
			this.angle = angleOrY;
		}
		else { //cartesian
			this.x = radiusOrX;
			this.y = angleOrY;
			this.radius = calculateRadius();
			this.angle = calculateAngle();
		}
	}

	private double calculateRadius() {
		return 0;
	}

	private double calculateAngle() {
		return 0;
	}

	private double calculateX() {
		return 0;
	}

	private double calculateY() {
		return 0;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getRadius() {
		return radius;
	}

	public double getAngle() {
		return angle;
	}

	public void setX(double x) {
		this.x = x;
		this.angle = calculateAngle();
		this.radius = calculateRadius();
	}

	public void setY(double y) {
		this.y = y;
		this.angle = calculateAngle();
		this.radius = calculateRadius();
	}
	public void setRadius(double radius) {
		this.radius = radius;
		this.x = calculateX();
		this.y = calculateY();
	}

	public void setAngle(double angle) {
		this.angle = angle;
		this.x = calculateX();
		this.y = calculateY();
	}
	public double angleTo(Coordinate c){
		return 0;
	}
}
