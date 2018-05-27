package mycontroller;


import controller.CarController;
import world.Car;


public class MyAIController extends CarController{
		

	
	private CompositeRoute compositeRoutes;

	
	private int check = 1;
	
	private Car car;
	
	
		
	public MyAIController(Car car) {
		super(car);
		
		this.car = car;
		this.compositeRoutes = CompositeRoute.getInstance( car);
		
	}

	@Override
	public void update(float delta) {

		compositeRoutes.updateRoute(check);
		if(compositeRoutes.run(delta)) {
			check++;
		}
	}
	
	
}
