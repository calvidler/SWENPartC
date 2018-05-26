package mycontroller;

import java.util.ArrayList;
import java.util.List;

import world.Car;


public class CompositeRoute {
	//Collection of routes
    public Route currentRoute ;

	private SafeSearch safeSearch;
	private BruteSearch bruteSearch;
	 private static CompositeRoute instance; //only instance of this class

	 //private constructor
	private CompositeRoute(Car car, TileDetector checkTile, SteeringWheel turn) {
		this.safeSearch = new SafeSearch(turn, car, checkTile); // does not adapt well if used with others
		this.bruteSearch = new BruteSearch(turn, car, checkTile);

	}
	//implements singleton
	public static CompositeRoute getInstance(Car car, TileDetector checkTile, SteeringWheel turn) {
		if ( instance == null ) {
			instance = new CompositeRoute(car, checkTile, turn);

		}
        return instance;
    }
	
    //determines route to use
    public void updateRoute(int check) {

    	if(check%2 != 0) { 
			System.out.print("2. ");
			currentRoute = (bruteSearch);
		}
		else {
			System.out.print("1. ");
			currentRoute =safeSearch;
		}
	}
    //Runs algorithms
    public boolean run(float delta) {
		
            return currentRoute.run(delta);
        
    }
}
