package mycontroller;

import world.Car;


public class CompositeRoute implements Route{
	 private boolean isTurningLeft = false;
		private boolean isTurningRight = false; 
		private SteeringWheel turn;
		private TileDetector checkTile;
	//Collection of routes
    public Route currentRoute ;
   
	private SafeSearch safeSearch;
	private BruteSearch bruteSearch;
	 private static CompositeRoute instance; //only instance of this class

	 //private constructor
	private CompositeRoute(Car car) {
		this.turn = new SteeringWheel(isTurningLeft, isTurningRight, car);
		this.checkTile = new TileDetector(car);
		
		this.safeSearch = new SafeSearch(turn, car, checkTile); // does not adapt well if used with others
		this.bruteSearch = new BruteSearch(turn, car, checkTile);


	}
	//implements singleton
	public static CompositeRoute getInstance(Car car) {
		if ( instance == null ) {
			instance = new CompositeRoute(car);

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
