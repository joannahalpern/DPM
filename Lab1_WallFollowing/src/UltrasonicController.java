/*
 * ECSE 211 Lab 1 - Group 53
 * Harris Miller - 260499543
 * Joanna Halpern - 260410826
 */

public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
	
	public int getCounter();
	
	public int readLeftSpeed();
	
	public int readRightSpeed();
}
