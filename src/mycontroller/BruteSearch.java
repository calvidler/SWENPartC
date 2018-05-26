package mycontroller;

import java.util.HashMap;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

/**
 * The Class BruteSearch.
 */
public class BruteSearch implements Route {
	
	/** The is following wall. */
	private boolean isFollowingWall = true; // This is initialized when the car sticks to a wall.
	
	/** The last turn direction. */
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	
	/** The is turning left. */
	private boolean isTurningLeft = false;
	
	/** The is turning right. */
	private boolean isTurningRight = false; 
	
	/** The previous state. */
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
	
	/** The above health. */
	private float aboveHealth;
	
	/** The lava crossed. */
	private boolean lavaCrossed = false;
	
	/** The start. */
	private Coordinate start =null;
	
	/** The counter. */
	private int counter = 0;
	
	/** The off start. */
	private boolean offStart = false;
	
	/** The count. */
	private int count =0;
	
	/** The count wait. */
	private int countWait = 100; //generally not next to lava
	
	/** The has rounded. */
	private boolean hasRounded = false;
	
	

	
	/** The car speed. */
	// Car Speed to move at
	private final float CAR_SPEED = 3;
	
	
	/** The turn. */
	private SteeringWheel turn;
	
	/** The check tiles. */
	private TileDetector checkTiles;
	
	/** The car. */
	private Car car;
	
	/** The reverse. */
	boolean reverse = false;
	
	
	
	/**
	 * Instantiates a new brute search.
	 *
	 * @param turn the turn
	 * @param car the car
	 * @param checkWall the check wall
	 */
	public BruteSearch(SteeringWheel turn, Car car, TileDetector checkWall) {
		this.turn = turn;
		this.car = car;
		this.checkTiles = checkWall;
		this.aboveHealth = car.getHealth();
	}
	
	/* (non-Javadoc)
	 * @see mycontroller.Route#run(float)
	 */
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
				if(car.getHealth() < aboveHealth && checkTiles.checkCurrentTileHealth(currentView)) {
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
	
	
	/**
	 * Check current tile lava.
	 *
	 * @param currentView the current view
	 * @return true, if successful
	 */
	private boolean checkCurrentTileLava(HashMap<Coordinate,MapTile> currentView) {
		Coordinate currentPosition = new Coordinate(car.getPosition());
		MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y));
		if(tile instanceof LavaTrap) {
			lavaCrossed = true;
			return true;
		}
		return false;
			
	}
	
	/**
	 * Check current tile finish.
	 *
	 * @param currentView the current view
	 * @return true, if successful
	 */
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
	 *
	 * @param car the car
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
	
	
	/**
	 * Not following wall.
	 *
	 * @param currentView the current view
	 * @param delta the delta
	 */
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
	
	/**
	 * Check rounded.
	 *
	 * @return true, if successful
	 */
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
	
	
	/**
	 * Check straight.
	 *
	 * @return true, if successful
	 */
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
	
	/**
	 * Check round coordinate.
	 *
	 * @param c1 the c 1
	 * @param c2 the c 2
	 * @return true, if successful
	 */
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
