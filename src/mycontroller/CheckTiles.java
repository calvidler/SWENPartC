package mycontroller;

import java.util.HashMap;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class CheckTiles {
	
	private Car car;
	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 2;
	private int lavaSensitivity;
	private int key =4;
	private float speed;
	
	public CheckTiles(Car car) {
		this.car = car;
		this.lavaSensitivity = car.VIEW_SQUARE;
		
	}

	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	public boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, MapTile.Type type , boolean enterLava){
		/*if(car.getSpeed() < 1) wallSensitivity = 1;
		else wallSensitivity = 2;*/
		switch(orientation){
		case EAST:
			return checkEast(currentView, type, enterLava);
		case NORTH:
			return checkNorth(currentView, type, enterLava);
		case SOUTH:
			return checkSouth(currentView, type, enterLava);
		case WEST:
			return checkWest(currentView, type, enterLava);
		default:
			return false;
		
		}
	}
	
	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	public boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, MapTile.Type type, boolean enterLava) {
		
		switch(orientation){
		case EAST:
			return checkNorth(currentView, type, enterLava);
		case NORTH:
			return checkWest(currentView, type, enterLava);
		case SOUTH:
			return checkEast(currentView, type, enterLava);
		case WEST:
			return checkSouth(currentView, type, enterLava);
		default:
			return false;
		}
		
	}
	public String checkDiagonal(HashMap<Coordinate, MapTile> currentView,WorldSpatial.Direction orientation) {
		switch(orientation){
		case NORTH:
			if(checkNorthEast(currentView)) return "right";
			else if(checkNorthWest(currentView)) return "left";
		case EAST:
			if(checkNorthEast(currentView)) return "left";
			else if(checkSouthEast(currentView)) return "right";
		case SOUTH:
			if(checkSouthEast(currentView)) return "left";
			else if(checkSouthWest(currentView)) return "right";
		case WEST:
			if(checkNorthWest(currentView)) return "left";
			else if(checkSouthWest(currentView)) return "right";
		default:
			return null;
		}
	}
	
	public boolean checkSide(HashMap<Coordinate, MapTile> currentView, MapTile.Type type,WorldSpatial.Direction orientation, String turning , boolean enterLava) {
		switch(orientation){
		case NORTH:
			if(turning.equals("left")) return checkWest(currentView, type, enterLava);
			else return checkEast(currentView, type, enterLava);
		case EAST:
			if(turning.equals("left")) return checkNorth(currentView, type, enterLava);
			else return checkSouth(currentView, type, enterLava);
		case SOUTH:
			if(turning.equals("left")) return checkEast(currentView, type, enterLava);
			else return checkWest(currentView, type, enterLava);
		case WEST:
			if(turning.equals("left")) return checkSouth(currentView, type, enterLava);
			else return checkNorth(currentView, type, enterLava);
		default:
			return false;
		}
	}


	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView, MapTile.Type type, boolean enterLava){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if((tile.isType(type) || tile instanceof LavaTrap && enterLava == false) && !(tile instanceof HealthTrap)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView, MapTile.Type type, boolean enterLava){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if((tile.isType(type) || tile instanceof LavaTrap && enterLava == false) && !(tile instanceof HealthTrap)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView, MapTile.Type type, boolean enterLava){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if((tile.isType(type) || tile instanceof LavaTrap && enterLava == false) && !(tile instanceof HealthTrap)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView, MapTile.Type type, boolean enterLava){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if((tile.isType(type) || tile instanceof LavaTrap && enterLava == false) && !(tile instanceof HealthTrap)){
				return true;
			}
		}
		return false;
	}
	
	
	public boolean checkNorthEast(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
				Coordinate currentPosition = new Coordinate(car.getPosition());
				for(int i = 0; i <= lavaSensitivity; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y+i));
					if(tile instanceof LavaTrap){
						if(((LavaTrap) tile).getKey() == key) {
							key--;
							return true;
						}
					}
				}
				return false;
		
	}
	public boolean checkNorthWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
				Coordinate currentPosition = new Coordinate(car.getPosition());
				for(int i = 0; i <= lavaSensitivity; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y+i));
					if(tile instanceof LavaTrap){
						if(((LavaTrap) tile).getKey() == key) {
							key--;
							return true;
						}
					}
				}
				return false;
		
	}
	
	public boolean checkSouthEast(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
				Coordinate currentPosition = new Coordinate(car.getPosition());
				for(int i = 0; i <= lavaSensitivity; i++){
					MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y-i));
					if(tile instanceof LavaTrap){
						if(((LavaTrap) tile).getKey() == key) {
							key--;
							return true;
						}
					}
				}
				return false;
		
	}
	
	public boolean checkSouthWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= lavaSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y-i));
			if(tile instanceof LavaTrap){
				if(((LavaTrap) tile).getKey() == key) {
					key--;
					return true;
				}
			}
		}
		return false;
		
	}
}
