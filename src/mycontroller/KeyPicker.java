package mycontroller;

import java.util.HashMap;

import tiles.HealthTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

// 
/**
 * The Class GetKey supports the functionality of BruteSearch.
 */
public class KeyPicker {
	
	/** The turn. */
	private SteeringWheel turn;
	
	/** The check tiles. */
	private TileDetector checkTiles;
	
	/** The car. */
	private Car car;
	
	/** The to X. */
	private int toX;
	
	/** The to Y. */
	private int toY;
	
	/** The start X. */
	private int startX;
	
	/** The start Y. */
	private int startY;
	
	/** The found. */
	private boolean found = false;
	
	/** The last turn direction. */
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	
	/** The is turning left. */
	private boolean isTurningLeft = false;
	
	/** The is turning right. */
	private boolean isTurningRight = false; 
	
	
	
	/** The start. */
	boolean start;
	
	/**
	 * Instantiates new key picker.
	 *
	 * @param turn the turn
	 * @param car the car
	 * @param checkWall the check wall
	 * @param key the key
	 */
	public KeyPicker(SteeringWheel turn, Car car, TileDetector checkWall, Coordinate key){
		this.turn = turn;
		this.car = car;
		this.checkTiles = checkWall;
		this.start = true;
		String[] splitCoordinate = key.toString().split(",");
		this.toX = Integer.parseInt(splitCoordinate[0]);
		this.toY = Integer.parseInt(splitCoordinate[1]);
		String startCoordinate = car.getPosition(); 
		splitCoordinate = startCoordinate.split(",");
		startX = Integer.parseInt(splitCoordinate[0]);
		startY = Integer.parseInt(splitCoordinate[1]);
		
		
	}
	
	/**
	 * Gets the key.
	 *
	 * @param delta the delta
	 * @param currentView the current view
	 * @return the key
	 */
	public boolean getKey(float delta, HashMap<Coordinate,MapTile> currentView) {
		String[] splitCoordinate = car.getPosition().split(",");
		int currentX = Integer.parseInt(splitCoordinate[0]);
		int currentY = Integer.parseInt(splitCoordinate[1]);
		WorldSpatial.Direction orientation=  car.getOrientation();		
		// Readjust the car if it is misaligned.
		turn.readjust(lastTurnDirection,delta, isTurningLeft,isTurningRight);
		
		if(checkTiles.checkCurrentTileHealth(currentView) && car.getHealth() < 100 && car.getSpeed() < 0.5) {
			car.brake();
		}
		if(isTurningLeft && !turn.checkStraight()) {
			car.turnLeft(delta);
		}
		else if(isTurningLeft && turn.checkStraight()) isTurningLeft =false;
		else if(isTurningRight && !turn.checkStraight()) {
			car.turnRight(delta);
		}
		else if(isTurningRight && turn.checkStraight()) isTurningRight =false;
		else if(toX < currentX) {
			System.out.print("3. ");
			switch(orientation) {
			case SOUTH:
				car.turnLeft(delta);
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			case NORTH:
				car.turnRight(delta);
				lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
				isTurningRight = true;
			case EAST:
				car.applyReverseAcceleration();
				break;
			case WEST:
				car.applyForwardAcceleration();
				break;
			default:
				break;
				
			}
			
		}
		else if(toX > currentX) {
			System.out.print("4. ");
			switch(orientation) {
			case SOUTH:
				car.turnRight(delta);
				lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
				isTurningRight = true;
			case NORTH:
				car.turnLeft(delta);
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
				
			case WEST:
				car.applyReverseAcceleration();
				break;
			case EAST:
				car.applyForwardAcceleration();
				break;
			default:
				break;
				
			}
		}
		else if(toY < currentY) {
			System.out.print("1. ");
			switch(orientation) {
			case WEST:
				car.turnLeft(delta);
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			case EAST:
				car.turnRight(delta);
				lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
				isTurningRight = true;
			case NORTH:
				car.applyReverseAcceleration();
				break;
			case SOUTH:
				car.applyForwardAcceleration();
				break;
			default:
				break;
				
			}
		}
		else if(toY > currentY) {
			System.out.print("2. ");
			switch(orientation) {
			case WEST:
				car.turnRight(delta);
				lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
				isTurningRight = true;
			case EAST:
				car.turnLeft(delta);
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			case SOUTH:
				car.applyReverseAcceleration();
				break;
			case NORTH:
				car.applyForwardAcceleration();
				break;
			default:
				break;
				
			}
		
		}
		if(toX == currentX && toY == currentY && !found) {
			found = true;
			toX = startX;
			toY = startY;
			
		}
		else if(toX == currentX && toY == currentY && found) {
			return true;
		}
		
		
		return false;
	}

}
