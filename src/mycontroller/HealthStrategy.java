package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

public class HealthStrategy {
	private Car car;
	private float health;
	public HealthStrategy(Car car) {
		this.car = car;
	}
	public boolean heal(HashMap<Coordinate, MapTile> currentView ) {
		
		
		return false;
		
	}
}
