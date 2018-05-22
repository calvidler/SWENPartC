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
	private CheckTiles checkTiles;
	private Car car;
	boolean reverse = false;
	
	public FollowWall(Turn turn, Car car, CheckTiles checkWall){
		this.turn = turn;
		this.car = car;
		this.checkTiles = checkWall;
		
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
			
			if(checkTiles.checkNorth(currentView,MapTile.Type.WALL)){  //only checks north wall
				// Turn right until we go back to east!
				if(!car.getOrientation().equals(WorldSpatial.Direction.EAST)){ // turn right when wall north
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					turn.applyRightTurn(delta);
				}
				else{ //fully turned right follow wall
					isFollowingWall = true;
				}
			}
			else if(!car.getOrientation().equals(WorldSpatial.Direction.NORTH)){ // Turn towards the north if no wall north
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				turn.applyLeftTurn(delta);
			}
		}
		// Once the car is already stuck to a wall, apply the following logic
		else{
			// Readjust the car if it is misaligned.
			turn.readjust(lastTurnDirection,delta, isTurningLeft,isTurningRight);
			
			if(isTurningRight){
				if(!checkTiles.checkSide(currentView, MapTile.Type.TRAP, car.getOrientation(), "right")){ //not turning into traps
					turn.applyRightTurn(delta);
				}
			}
			else if(isTurningLeft){ 
				// Apply the left turn if you are not currently near a wall.
				if(!checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL)){
					if(!checkTiles.checkSide(currentView, MapTile.Type.TRAP, car.getOrientation(), "left")){ //not turning into traps
						turn.applyLeftTurn(delta);
					}
				}
				else{
					isTurningLeft = false;
				}
			}
			// Try to determine whether or not the car is next to a wall.
			else if(checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL)){
				reverse = false;
				// Maintain some velocity
				if(car.getSpeed() < CAR_SPEED){
					car.applyForwardAcceleration();
				}
				// If there is wall ahead, turn right!
				if(checkTiles.checkWallAhead(car.getOrientation(),currentView,MapTile.Type.WALL) ){
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					isTurningRight = true;				
					
				}

			}
			// This indicates that I can do a left turn if I am not turning right
			else{
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			}
			if(checkTiles.checkSide(currentView, MapTile.Type.TRAP, car.getOrientation(), "right") || checkTiles.checkSide(currentView, MapTile.Type.WALL, car.getOrientation(), "right")){ // come to deadend
				if(checkTiles.checkSide(currentView, MapTile.Type.TRAP, car.getOrientation(), "left") || checkTiles.checkSide(currentView, MapTile.Type.WALL, car.getOrientation(), "left")) {
					if(checkTiles.checkWallAhead(car.getOrientation(),currentView,MapTile.Type.WALL) || checkTiles.checkWallAhead(car.getOrientation(),currentView,MapTile.Type.TRAP) ) {
						car.applyReverseAcceleration();
						reverse = true;
					}
				}
				
			}
			if(reverse && !checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL) ) { // reversing if come to a deadend
				if(car.getSpeed() < CAR_SPEED){
					car.applyForwardAcceleration();
				}
				car.turnLeft(delta);
			}
			String key = checkTiles.checkDiagonal(currentView, car.getOrientation()); //checking to key in any diagonal
			if(key != null) {
				System.out.println("found! ");
				switch(key) {
					case "left":
						car.turnLeft(delta);
					case "right":
						car.turnRight(delta);
				}
			}
		}
	}
	
	/**
	 * Checks whether the car's state has changed or not, stops turning if it
	 *  already has.
	 */
	private void checkStateChange(Car car) { //keep in controller
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
