package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class CheckTiles {
	public static final int ALL = 5;
	private Car car;
	// How many minimum units the wall is away from the player.
	private int wallSensitivity = 2;

	public CheckTiles(Car car) {
		this.car = car;

	}

	/**
	 * Check if you have a specified tile around you
	 * 
	 * @param orientation
	 *            the orientation we are in based on WorldSpatial
	 * @param currentView
	 *            what the car can currently see
	 * @return
	 */
	public Coordinate checkTileAround(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView,
			MapTile.Type tile) {
		switch (orientation) {
		case EAST:
			return checkEast(currentView, tile);
		case NORTH:
			return checkNorth(currentView, tile);
		case SOUTH:
			return checkSouth(currentView, tile);
		case WEST:
			return checkWest(currentView, tile);

		default:
			return checkAll(currentView, tile);

		}
	}

	// returns first occurrence of the matching tile type
	public Coordinate checkAll(HashMap<Coordinate, MapTile> currentView, MapTile.Type tile) {
		Coordinate temp = checkEast(currentView, tile);
		if (temp != null) {
			return temp;
		} else {
			temp = checkSouth(currentView, tile);
			if (temp != null) {
				return temp;
			} else {
				temp = checkSouth(currentView, tile);
				if (temp != null) {
					return temp;
				} else {
					temp = checkWest(currentView, tile);
					return temp;
				}
			}

		}
	}

	/**
	 * Method below just iterates through the list and check in the correct
	 * coordinates. i.e. Given your current position is 10,10 checkEast will check
	 * up to wallSensitivity amount of tiles to the right. checkWest will check up
	 * to wallSensitivity amount of tiles to the left. checkNorth will check up to
	 * wallSensitivity amount of tiles to the top. checkSouth will check up to
	 * wallSensitivity amount of tiles below.
	 */
	public Coordinate checkEast(HashMap<Coordinate, MapTile> currentView, MapTile.Type tileToCheck) {
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			Coordinate toCheck = new Coordinate(currentPosition.x + i, currentPosition.y);
			MapTile tile = currentView.get(toCheck);
			if (tile.isType(tileToCheck)) {
				return toCheck;
			}
		}
		return null;
	}

	public Coordinate checkWest(HashMap<Coordinate, MapTile> currentView, MapTile.Type tileToCheck) {
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			Coordinate toCheck = new Coordinate(currentPosition.x - i, currentPosition.y);
			MapTile tile = currentView.get(toCheck);
			if (tile.isType(tileToCheck)) {
				return toCheck;
			}
		}
		return null;
	}

	public Coordinate checkNorth(HashMap<Coordinate, MapTile> currentView, MapTile.Type tileToCheck) {
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			Coordinate toCheck = new Coordinate(currentPosition.x, currentPosition.y + i);
			MapTile tile = currentView.get(toCheck);
			if (tile.isType(tileToCheck)) {
				return toCheck;
			}
		}
		return null;
	}

	public Coordinate checkSouth(HashMap<Coordinate, MapTile> currentView, MapTile.Type tileToCheck) {
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(car.getPosition());
		for (int i = 0; i <= wallSensitivity; i++) {
			Coordinate toCheck = new Coordinate(currentPosition.x, currentPosition.y - i);
			MapTile tile = currentView.get(toCheck);
			if (tile.isType(tileToCheck)) {
				return toCheck;
			}
		}
		return null;
	}
}
