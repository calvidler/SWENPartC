package mycontroller;
import java.io.*;

import java.util.HashMap;

import tiles.HealthTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class SafeSearch implements Route {
	
	

	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
	
	
	private Coordinate start =null;
	private int counter = 0;
	private boolean offStart = false;
	private int count =0;
	private int countWait = 100; //generally not next to lava
	private boolean hasRounded = false;
	
	private Coordinate currentKeyCoordinate = null;
	private GetKey gk;
	private boolean foundKey = true;
	
	

	
	// Car Speed to move at
	private final float CAR_SPEED = 3;
	
	
	private SteeringWheel turn;
	private TileDetector checkTiles;
	private Car car;
	boolean reverse = false;
	
	public SafeSearch(SteeringWheel turn, Car car, TileDetector checkTiles){
		this.turn = turn;
		this.car = car;
		this.checkTiles = checkTiles;
		
	}
	
	public boolean run(float delta) {
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = car.getView();
		
		checkStateChange(car);

		// Once the car is already stuck to a wall, apply the following logic
		if(!foundKey && checkStraight()) {
			System.out.print("key . ");
			String[] splitCoordinate = currentKeyCoordinate.toString().split(",");
			int toX = Integer.parseInt(splitCoordinate[0]);
			int toY = Integer.parseInt(splitCoordinate[1]);
			String startCoordinate = car.getPosition(); 
			splitCoordinate = startCoordinate.split(",");
			int startX = Integer.parseInt(splitCoordinate[0]);
			int startY = Integer.parseInt(splitCoordinate[1]);
			if((car.getOrientation() == WorldSpatial.Direction.NORTH || car.getOrientation() == WorldSpatial.Direction.SOUTH) && startX != toX) return true;
			if((car.getOrientation() == WorldSpatial.Direction.EAST || car.getOrientation() == WorldSpatial.Direction.WEST) && startY != toY) return true;
			
			foundKey = gk.getKey(delta, currentView);
			if(foundKey) {
				count = 0;
				start = null;
				counter =0;
				offStart = false;
				
			}
		}
		else {
			System.out.print("1. ");
			if(checkRounded() || hasRounded) {
				hasRounded = true;
				count++;
				if(count > countWait  && checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL,false) && checkStraight() && !reverse) {
					hasRounded = false;
					count = 0;
					start = null;
					counter =0;
					offStart = false;
					return true;
				}
			}
			if(reverse && count > 3*countWait) { //dont get stuck in reverse
				reverse = false;
			}
			// Readjust the car if it is misaligned.
			turn.readjust(lastTurnDirection,delta, isTurningLeft,isTurningRight);
			
			if(isTurningRight){
				System.out.print("2. ");
				if(!checkTiles.checkSide(currentView, MapTile.Type.TRAP, car.getOrientation(), "right",false)){ //not turning into traps
					turn.applyRightTurn(delta);
				}
			}
			else if(isTurningLeft){
				System.out.print("3. ");
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
				System.out.print("4. ");
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
				System.out.print("5. ");
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
			if(reverse && !checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL,false) ) { // reversing if and no wall to left
				if(car.getSpeed() < CAR_SPEED){
					car.applyForwardAcceleration();
				}
				car.turnLeft(delta);
			}
			
			Coordinate key = checkTiles.checkDiagonal(currentView, car.getOrientation()); //checking to key in any diagonal
			if(key!=null) {
				currentKeyCoordinate= key;
				gk = new GetKey(turn, car, checkTiles, currentKeyCoordinate);
				foundKey = false;
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
	
	
	private boolean checkRounded() {
		counter++;
		Coordinate currentC = new Coordinate(car.getPosition());
		if(start == null) {
			start = currentC;
		}
		
		if(counter > 100) {
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
	
	private boolean checkStraight() {
		switch(Math.round(car.getAngle())) {
		case 0:
			return true;
		case 90:
			return true;
		case 180:
			return true;
		case 270:
			return true;
		default:
			return false;
		}
	}
	
	
	
	
	
}