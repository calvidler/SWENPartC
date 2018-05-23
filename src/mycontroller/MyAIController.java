package mycontroller;


import controller.CarController;
import world.Car;
import controller.AIController;


public class MyAIController extends CarController{
		

	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	private Car car;
	private Turn turn;
	private CheckTiles checkTile;
	private FollowWall followWall;
	private HealthStrategy healthStrategy;
	
	
	private int check = 0;
	
	
		
	public MyAIController(Car car) {
		super(car);
		this.turn = new Turn(isTurningLeft, isTurningRight, car);
		this.checkTile = new CheckTiles(car);
		this.car = car;
		
		followWall = new FollowWall(turn, car, checkTile); // does not adapt well if used with others 
		this.healthStrategy = new HealthStrategy(turn, car, checkTile);
		
		
		
	}

	@Override
	public void update(float delta) {
		
		CompositeRoutes compositeRoutes = new CompositeRoutes();
		if(check%2 != 0) { 
			System.out.print("2. ");
			compositeRoutes.remove(followWall);
			compositeRoutes.add(healthStrategy);
		}
		else {
			System.out.print("1. ");
			compositeRoutes.remove(healthStrategy);
			compositeRoutes.add(followWall);
		}
		if(compositeRoutes.run(delta)) {
			System.out.println("Yay");
			check++;
		}
		
		
		
		
		
		
		
	}
	
	
}
