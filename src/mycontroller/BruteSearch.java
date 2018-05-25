package mycontroller;

import java.util.HashMap;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;





public class BruteSearch implements Route {
	private boolean isFollowingWall = true; // This is initialized when the car sticks to a wall.
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
	
	private float aboveHealth;
	
	private boolean lavaCrossed = false;
	
	private Coordinate start =null;
	private int counter = 0;
	private boolean offStart = false;
	private int count =0;
	private int countWait = 100; //generally not next to lava
	private boolean hasRounded = false;
	
	

	
	// Car Speed to move at
	private final float CAR_SPEED = 3;
	
	
	private SteeringWheel turn;
	private TileDetector checkTiles;
	private Car car;
	boolean reverse = false;
	
	
	
	public BruteSearch(SteeringWheel turn, Car car, TileDetector checkWall) {
		this.turn = turn;
		this.car = car;
		this.checkTiles = checkWall;
		this.aboveHealth = car.getHealth();
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
			if(checkRounded() && !hasRounded) {
				hasRounded = true;
				count++;
				if(count > countWait  && checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL,false) && checkStraight() && !reverse) {
					count = 0;
					start = null;
					counter =0;
					offStart = false;
					return true;
				}
			}
			if(!checkCurrentTileLava(currentView) && lavaCrossed == true && !checkCurrentTileFinish(currentView) && hasRounded) {
				count++;
				if(count > 12) {
					lastTurnDirection = null;
					lavaCrossed = false;
					System.out.println("left " + lastTurnDirection + " ");
					count =0;
					return true;
				}
			}
			
			// Readjust the car if it is misaligned.
			turn.readjust(lastTurnDirection,delta, isTurningLeft,isTurningRight);
			
			if(isTurningRight){
				System.out.print("1. ");
				turn.applyRightTurn(delta);
			}
			else if(isTurningLeft){ 
				System.out.print("2. ");
				// Apply the left turn if you are not currently near a wall.
				if(!checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL ,true)){
					System.out.print("2.1 ");
					turn.applyLeftTurn(delta);
				}
				else{
					System.out.print("2.2 ");
					isTurningLeft = false;
				}
			}
			// Try to determine whether or not the car is next to a wall.
			else if(checkTiles.checkFollowingWall(car.getOrientation(),currentView,MapTile.Type.WALL,true)){
				System.out.print("3. ");
				reverse = false;
				// Maintain some velocity
				if(car.getHealth() < aboveHealth && checkCurrentTileHealth(currentView)) {
					System.out.print("3.1 ");
					car.brake();
				}
				else if(car.getSpeed() < CAR_SPEED){
					System.out.print("3.2 ");
					car.applyForwardAcceleration();
				}
				// If there is wall ahead, turn right!
				if(checkTiles.checkWallAhead(car.getOrientation(),currentView,MapTile.Type.WALL,true) ){
					System.out.print("3.4 ");
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					isTurningRight = true;				
					
				}

			}
			// This indicates that I can do a left turn if I am not turning right
			else{
				System.out.print("4. ");
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			}
			
		}
		return false;
	}
	
	private boolean checkCurrentTileHealth(HashMap<Coordinate,MapTile> currentView) {
		Coordinate currentPosition = new Coordinate(car.getPosition());
		MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y));
		if(tile instanceof HealthTrap) {
			return true;
		}
		return false;
			
	}
	
	private boolean checkCurrentTileLava(HashMap<Coordinate,MapTile> currentView) {
		Coordinate currentPosition = new Coordinate(car.getPosition());
		MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y));
		if(tile instanceof LavaTrap) {
			lavaCrossed = true;
			return true;
		}
		return false;
			
	}
	private boolean checkCurrentTileFinish(HashMap<Coordinate,MapTile> currentView) { //for easy map
		Coordinate currentPosition = new Coordinate(car.getPosition());
		MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y));
		if(tile.getType() == MapTile.Type.FINISH) {
			return true;
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
		
		if(checkTiles.checkNorth(currentView,MapTile.Type.WALL, true)){  //only checks north wall
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
		
		if(counter > 100) {
			return checkRoundCoordinate(currentC, start) ;
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
	
	private boolean checkRoundCoordinate(Coordinate c1, Coordinate c2) {
		String[] splitCoordinate = c1.toString().split(",");
		int x = Integer.parseInt(splitCoordinate[0]);
		int y = Integer.parseInt(splitCoordinate[1]);
		
		for(int i =-1 ; i<= 1; i++) {
			Coordinate c= new Coordinate(x+i,y);
			Coordinate d = new Coordinate(x,y+i);
			if(c2.equals(c) || c2.equals(d)) {
				System.out.println("damn!");
				return true;
			}
		}
		
		return false;
	}
	
}
