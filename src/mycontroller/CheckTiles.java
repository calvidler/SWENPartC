package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class CheckTiles {
	
	private Car car;
	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 2;
	private int LavaSensitivity = 1;
	
	public CheckTiles(Car car) {
		this.car = car;
		
	}

	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	public boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, MapTile.Type type ){
		switch(orientation){
		case EAST:
			return checkEast(currentView, type);
		case NORTH:
			return checkNorth(currentView, type);
		case SOUTH:
			return checkSouth(currentView, type);
		case WEST:
			return checkWest(currentView, type);
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
	public boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView, MapTile.Type type) {
		
		switch(orientation){
		case EAST:
			return checkNorth(currentView, type);
		case NORTH:
			return checkWest(currentView, type);
		case SOUTH:
			return checkEast(currentView, type);
		case WEST:
			return checkSouth(currentView, type);
		default:
			return false;
		}
		
	}
	
	public boolean checkSide(HashMap<Coordinate, MapTile> currentView, MapTile.Type type,WorldSpatial.Direction orientation, String turning) {
		switch(orientation){
		case NORTH:
			if(turning.equals("left")) return checkWest(currentView, type);
			else return checkEast(currentView, type);
		case EAST:
			if(turning.equals("left")) return checkNorth(currentView, type);
			else return checkSouth(currentView, type);
		case SOUTH:
			if(turning.equals("left")) return checkEast(currentView, type);
			else return checkWest(currentView, type);
		case WEST:
			if(turning.equals("left")) return checkSouth(currentView, type);
			else return checkNorth(currentView, type);
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
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView, MapTile.Type type){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(type) || tile.isType(MapTile.Type.TRAP)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView, MapTile.Type type){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(type) || tile.isType(MapTile.Type.TRAP)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView, MapTile.Type type){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(type) || tile.isType(MapTile.Type.TRAP)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView, MapTile.Type type){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(type) || tile.isType(MapTile.Type.TRAP)){
				return true;
			}
		}
		return false;
	}
}
