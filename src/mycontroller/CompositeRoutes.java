package mycontroller;

import java.util.ArrayList;
import java.util.List;


public class CompositeRoutes {
	//Collection of routes
    private List<Route> routeTypes = new ArrayList<Route>();

    //Runs algorithms
    public void run(float delta) {
		for (Route route : routeTypes) {
            route.run(delta);
        }
    }

    //Adds routes to list.
    public void add(Route route) {
        routeTypes.add(route);
    }

    //Removes routes from list
    public void remove(Route route) {
        routeTypes.remove(route);
    }
}
