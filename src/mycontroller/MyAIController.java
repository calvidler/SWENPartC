package mycontroller;


import controller.CarController;
import world.Car;


public class MyAIController extends CarController{
		

	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	private Turn turn;
	private CheckTiles checkTile;
	private SafeSearch safeSearch;
	private BruteSearch bruteSearch;
	
	
	private int check = 1;
	
	private Car car;
	
	
		
	public MyAIController(Car car) {
		super(car);
		this.turn = new Turn(isTurningLeft, isTurningRight, car);
		this.checkTile = new CheckTiles(car);
		this.car = car;
		
		this.safeSearch = new SafeSearch(turn, car, checkTile); // does not adapt well if used with others 
		this.bruteSearch = new BruteSearch(turn, car, checkTile);
		
		
		
		
	}

	@Override
	public void update(float delta) {
		CompositeRoutes compositeRoutes = new CompositeRoutes();
		if(check%2 != 0) { 
			System.out.print("2. ");
			compositeRoutes.remove(safeSearch);
			compositeRoutes.add(bruteSearch);
		}
		else {
			System.out.print("1. ");
			compositeRoutes.remove(bruteSearch);
			compositeRoutes.add(safeSearch);
		}
		if(compositeRoutes.run(delta)) {
			check++;
		}
		
		
		
		
		
		
		
	}
	
	
}
