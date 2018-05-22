package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class FollowWall implements Route {
	
	
	
	private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
	

	
	// Car Speed to move at
	private final float CAR_SPEED = 3;
	
	
	private Turn turn;
	private CheckTiles checkWall;
	private Car car;
	
	public FollowWall(Turn turn, Car car, CheckTiles checkWall){
		this.turn = turn;
		this.car = car;
		this.checkWall = checkWall;
		
	}
	
	public void run(float delta) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = car.getView();
		
		checkStateChange(car);

		// If you are not following a wall initially, find a wall to stick to!
		if(!isFollowingWall){ //keeps going north till it hits a wall then turns right
			if(car.getSpeed() < CAR_SPEED){
				car.applyForwardAcceleration();
			}
			
			if(checkWall.checkNorth(currentView, MapTile.Type.WALL) != null){  //only checks north wall
				// Turn right until we go back to east!
				if(!car.getOrientation().equals(WorldSpatial.Direction.EAST)){ // turn right when wall north
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					turn.applyRightTurn(car.getOrientation(),delta);
				}
				else{ //fully turned right follow wall
					isFollowingWall = true;
				}
			}
			else if(!car.getOrientation().equals(WorldSpatial.Direction.NORTH)){ // Turn towards the north if no wall north
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				turn.applyLeftTurn(car.getOrientation(),delta);
			}
		}
		// Once the car is already stuck to a wall, apply the following logic
		else{
			// Readjust the car if it is misaligned.
			turn.readjust(lastTurnDirection,delta, isTurningLeft,isTurningRight);
			
			if(isTurningRight){
				turn.applyRightTurn(car.getOrientation(),delta);
			}
			else if(isTurningLeft){ 
				// Apply the left turn if you are not currently near a wall.
				if(checkWall.checkTileAround(car.getOrientation(),currentView, MapTile.Type.WALL)== null){
					turn.applyLeftTurn(car.getOrientation(),delta);
				}
				else{
					isTurningLeft = false;
				}
			}
			// Try to determine whether or not the car is next to a wall.
			if(checkWall.checkTileAround(car.getOrientation(),currentView, MapTile.Type.WALL)!= null){
				// Maintain some velocity
				if(car.getSpeed() < CAR_SPEED){
					car.applyForwardAcceleration();
				}
				// If there is wall ahead, turn right!
				if(checkWall.checkTileAround(car.getOrientation(),currentView, MapTile.Type.WALL)!= null){
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					isTurningRight = true;				
					
				}

			}
			// This indicates that I can do a left turn if I am not turning right
			else{
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			}
		}
	}
	
	/**
	 * Checks whether the car's state has changed or not, stops turning if it
	 *  already has.
	 */
	public void checkStateChange(Car car) { //keep in controller
		if(previousState == null){
			previousState = car.getOrientation();
		}
		else{
			if(previousState != car.getOrientation()){
				if(isTurningLeft){
					isTurningLeft = false;
				}
				if(isTurningRight){
					isTurningRight = false;
				}
				previousState = car.getOrientation();
			}
		}
	}
}
