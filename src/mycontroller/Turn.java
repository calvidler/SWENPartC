package mycontroller;
import world.Car;

import world.WorldSpatial;

public class Turn {
	private boolean isTurningLeft;
	private boolean isTurningRight; 
	private Car car;
	// Offset used to differentiate between 0 and 360 degrees
	private int EAST_THRESHOLD = 3;
	
	
	public Turn(boolean isTurningLeft, boolean isTurningRight, Car car) {
		this.isTurningRight = isTurningRight;
		this.isTurningLeft = isTurningLeft;
		this.car = car;
	}
	
	
	
	/**
	 * Readjust the car to the orientation we are in.
	 * @param lastTurnDirection
	 * @param delta
	 */
	public void readjust(WorldSpatial.RelativeDirection lastTurnDirection, float delta, boolean isTurningLeft, boolean isTurningRight) {
		this.isTurningLeft = isTurningLeft;
		this.isTurningRight = isTurningRight;
		if(lastTurnDirection != null){
			if(!isTurningRight && lastTurnDirection.equals(WorldSpatial.RelativeDirection.RIGHT)){
				adjustRight(car.getOrientation(),delta);
			}
			else if(!isTurningLeft && lastTurnDirection.equals(WorldSpatial.RelativeDirection.LEFT)){
				adjustLeft(car.getOrientation(),delta);
			}
		}
		
	}
	
	/**
	 * Try to orient myself to a degree that I was supposed to be at if I am
	 * misaligned.
	 */
	public void adjustLeft(WorldSpatial.Direction orientation, float delta) {
		
		switch(orientation){
		case EAST:
			if(car.getAngle() > WorldSpatial.EAST_DEGREE_MIN+EAST_THRESHOLD){
				car.turnRight(delta);
			}
			break;
		case NORTH:
			if(car.getAngle() > WorldSpatial.NORTH_DEGREE){
				car.turnRight(delta);
			}
			break;
		case SOUTH:
			if(car.getAngle() > WorldSpatial.SOUTH_DEGREE){
				car.turnRight(delta);
			}
			break;
		case WEST:
			if(car.getAngle() > WorldSpatial.WEST_DEGREE){
				car.turnRight(delta);
			}
			break;
			
		default:
			break;
		}
		
	}

	private void adjustRight(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(car.getAngle() > WorldSpatial.SOUTH_DEGREE && car.getAngle() < WorldSpatial.EAST_DEGREE_MAX){
				car.turnLeft(delta);
			}
			break;
		case NORTH:
			if(car.getAngle() < WorldSpatial.NORTH_DEGREE){
				car.turnLeft(delta);
			}
			break;
		case SOUTH:
			if(car.getAngle() < WorldSpatial.SOUTH_DEGREE){
				car.turnLeft(delta);
			}
			break;
		case WEST:
			if(car.getAngle() < WorldSpatial.WEST_DEGREE){
				car.turnLeft(delta);
			}
			break;
			
		default:
			break;
		}
		
	}
	
	/**
	 * Turn the car counter clock wise (think of a compass going counter clock-wise)
	 */
	public void applyLeftTurn(float delta) {
		if(car.getSpeed() < 1) {
			car.applyForwardAcceleration();
			
		}
		car.turnLeft(delta);
		
	}
	
	/**
	 * Turn the car clock wise (think of a compass going clock-wise)
	 */
	public void applyRightTurn(float delta) {
		if(car.getSpeed() < 1) {
			car.applyForwardAcceleration();
			
		}
		car.turnRight(delta);
		
	}

}
