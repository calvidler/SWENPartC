package mycontroller;


import controller.CarController;
import world.Car;


public class MyAIController extends CarController{
		

	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	private SteeringWheel turn;
	private TileDetector checkTile;
	private CompositeRoutes compositeRoutes;

	
	private int check = 1;
	
	private Car car;
	
	
		
	public MyAIController(Car car) {
		super(car);
		this.turn = new SteeringWheel(isTurningLeft, isTurningRight, car);
		this.checkTile = new TileDetector(car);
		this.car = car;
		this.compositeRoutes = CompositeRoutes.getInstance( car,  checkTile,  turn);
		
	}

	@Override
	public void update(float delta) {

		compositeRoutes.updateRoute(check);
		if(compositeRoutes.run(delta)) {
			check++;
		}
	}
	
	
}
