package mycontroller;
import java.io.*;

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
	
	
	private Coordinate start =null;
	private int counter = 0;
	private boolean offStart = false;
	private int count =0;
	private boolean hasRounded = false;
	
	

	
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
	
	public boolean run(float delta) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = car.getView();
		
		checkStateChange(car);

		// If you are not following a wall initially, find a wall to stick to!
		if(!isFollowingWall){ //keeps going north till it hits a wall then turns right
			notFollowingWall(currentView,delta);
		}
		// Once the car is already stuck to a wall, apply the following logic
		else{
			if(checkRounded() || hasRounded) {
				hasRounded = true;
				count++;
				System.out.println(count);
				if(count > 45  && checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL,false) && !isTurningRight ) {
					System.out.println("count = " + count + " ");
					hasRounded = false;
					count = 0;
					start = null;
					counter =0;
					offStart = false;
					return true;
				}
			}
			// Readjust the car if it is misaligned.
			turn.readjust(lastTurnDirection,delta, isTurningLeft,isTurningRight);
			
			if(isTurningRight){
				if(!checkTiles.checkSide(currentView, MapTile.Type.TRAP, car.getOrientation(), "right",false)){ //not turning into traps
					turn.applyRightTurn(delta);
				}
			}
			else if(isTurningLeft){ 
				// Apply the left turn if you are not currently near a wall.
				if(!checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL,false)){
					if(!checkTiles.checkSide(currentView, MapTile.Type.TRAP, car.getOrientation(), "left",false)){ //not turning into traps
						turn.applyLeftTurn(delta);
					}
				}
				else{
					isTurningLeft = false;
				}
			}
			// Try to determine whether or not the car is next to a wall.
			else if(checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL,false)){
				reverse = false;
				// Maintain some velocity
				if(car.getSpeed() < CAR_SPEED){
					car.applyForwardAcceleration();
				}
				// If there is wall ahead, turn right!
				if(checkTiles.checkWallAhead(car.getOrientation(),currentView,MapTile.Type.WALL,false) ){
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					isTurningRight = true;				
					
				}

			}
			// This indicates that I can do a left turn if I am not turning right
			else{
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			}
			if(checkTiles.checkSide(currentView, MapTile.Type.TRAP, car.getOrientation(), "right", false) || checkTiles.checkSide(currentView, MapTile.Type.WALL, car.getOrientation(), "right", false)){ // come to deadend
				if(checkTiles.checkSide(currentView, MapTile.Type.TRAP, car.getOrientation(), "left", false) || checkTiles.checkSide(currentView, MapTile.Type.WALL, car.getOrientation(), "left",false)) {
					if(checkTiles.checkWallAhead(car.getOrientation(),currentView,MapTile.Type.WALL,false) || checkTiles.checkWallAhead(car.getOrientation(),currentView,MapTile.Type.TRAP,false) ) {
						car.applyReverseAcceleration();
						reverse = true;
					}
				}
				
			}
			if(reverse && !checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL,false) ) { // reversing if come to a deadend
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
						System.out.println("key");
						car.brake();
						car.turnLeft(delta);
					case "right":
						car.brake();
						car.turnRight(delta);
				}
			}
		}
		return false;
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
	
	private void notFollowingWall(HashMap<Coordinate,MapTile> currentView, float delta) {
		if(car.getSpeed() < CAR_SPEED){
			car.applyForwardAcceleration();
		}
		
		if(checkTiles.checkNorth(currentView,MapTile.Type.WALL,false)){  //only checks north wall
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
	
	private boolean checkRounded() {
		counter++;
		Coordinate currentC = new Coordinate(car.getPosition());
		if(start == null) {
			start = currentC;
		}
		
		if(counter > 40) {
			return checkRoundCoordinate(currentC, start) ;
		}
		return false;
	}
	
	private boolean checkRoundCoordinate(Coordinate c1, Coordinate c2) {
		String[] splitCoordinate = c1.toString().split(",");
		int x = Integer.parseInt(splitCoordinate[0]);
		int y = Integer.parseInt(splitCoordinate[1]);
		
		for(int i =-1 ; i<= 1; i++) {
			Coordinate c= new Coordinate(x+i,y);
			Coordinate d = new Coordinate(x,y+i);
			if(c2.equals(c) || c2.equals(d)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	
}
