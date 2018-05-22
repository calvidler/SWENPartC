package mycontroller;


import controller.CarController;
import world.Car;


public class MyAIController extends CarController{
		

	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	private Car car;
	private Turn turn;
	private CheckTiles checkTile;
	private FollowWall followWall;
	
	
		
	public MyAIController(Car car) {
		super(car);
		this.turn = new Turn(isTurningLeft, isTurningRight, car);
		this.checkTile = new CheckTiles(car);
		this.car = car;
		
		followWall = new FollowWall(turn, car, checkTile); // does not adapt well if used with others 
		
		
	}

	@Override
	public void update(float delta) {
		
		CompositeRoutes compositeRoutes = new CompositeRoutes();
		compositeRoutes.add(followWall);
		
		
		compositeRoutes.run(delta);
		
		
		
	}
	
	
}
