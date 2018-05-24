package mycontroller;

import java.util.HashMap;

import tiles.HealthTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class GetKey {
	
	private Turn turn;
	private CheckTiles checkTiles;
	private Car car;
	private int toX;
	private int toY;
	private int startX;
	private int startY;
	
	private boolean found = false;
	
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	
	
	
	boolean start;
	
	public GetKey(Turn turn, Car car, CheckTiles checkWall, Coordinate key){
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
	
	public boolean getKey(float delta, HashMap<Coordinate,MapTile> currentView) {
		String[] splitCoordinate = car.getPosition().split(",");
		int currentX = Integer.parseInt(splitCoordinate[0]);
		int currentY = Integer.parseInt(splitCoordinate[1]);
		WorldSpatial.Direction orientation=  car.getOrientation();		
		// Readjust the car if it is misaligned.
		turn.readjust(lastTurnDirection,delta, isTurningLeft,isTurningRight);
		
		if(checkCurrentTileHealth(currentView) && car.getHealth() < 100 && car.getSpeed() < 0.5) {
			car.brake();
		}
		if(isTurningLeft && !checkStraight()) {
			car.turnLeft(delta);
		}
		else if(isTurningLeft && checkStraight()) isTurningLeft =false;
		else if(isTurningRight && !checkStraight()) {
			car.turnRight(delta);
		}
		else if(isTurningRight && checkStraight()) isTurningRight =false;
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
	
	
	private boolean checkCurrentTileHealth(HashMap<Coordinate,MapTile> currentView) {
		Coordinate currentPosition = new Coordinate(car.getPosition());
		MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y));
		if(tile instanceof HealthTrap) {
			return true;
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
