package ebola;
import java.util.LinkedList;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkFactoryFinder;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.WrapAroundBorders;

public class ebolaModel implements ContextBuilder<Object>{

		public Context<Object> build(Context<Object> context) {	
		
			// Here assign the parameter values
			Parameters p = RunEnvironment.getInstance().getParameters();
			int width = (Integer)p.getValue("width");
			int height = (Integer)p.getValue("height");
			int numOfHospitals = (Integer)p.getValue("numOfHospitals");
				
			// Create a new 2D grid on which the agents will move.Multi-occupancy 
			Grid<Object> grid = GridFactoryFinder.createGridFactory(null).createGrid("grid", context, 	
					new GridBuilderParameters<Object>(new WrapAroundBorders(), 
						new RandomGridAdder<Object>(), true, width, height));
			
			// Create the social Network = Un-directed
			Network<Object> hNetwork = NetworkFactoryFinder.createNetworkFactory(null).createNetwork(
					"hospitalNetwork", context, false);
				
			// Create the initial hospitals and add to the context. 
		
			Population pop = new Population();
			context.add(pop);
			grid.moveTo(pop,0,0);

			// Create the initial hospitals and add to the context. 
			for(int i = 0; i < numOfHospitals; i++){
				
				Hospital h = new Hospital(i);
				context.add(h);
				width = RandomHelper.nextIntFromTo(0, width-1);
				height = RandomHelper.nextIntFromTo(0, height-1);
				grid.moveTo(h,width,height);
				pop.hList.add(h);
			}
			randomStaticHospitalNetwork(grid, hNetwork);
			return context;	
		}
		// Random Graph among hospitals
		public void randomStaticHospitalNetwork(Grid grid,Network<Object> hNetwork){
			
			for (Object o: grid.getObjects()){
				
				Hospital source = new Hospital(0);
				Hospital target = new Hospital(0);
				if (o instanceof Hospital){
					
					source = (Hospital)o;
				}	
				for (Object ob: grid.getObjects()){
					
					if (ob instanceof Hospital){
						
						target = (Hospital)ob;
					}
					if (source.id != target.id){
						
						//int x = Math.abs((int)grid.getLocation(source).getX()-(int)grid.getLocation(target).getX());
						//int y = Math.abs(grid.getLocation(source).getY()-grid.getLocation(target).getY());
						//double distance = Math.sqrt((Math.pow(x, 2)+Math.pow(y, 2)))/(grid.getDimensions().getHeight()*grid.getDimensions().getWidth());
						//hNetwork.addEdge(source,target,distance);
						hNetwork.addEdge(source,target);
						System.out.println(hNetwork.getDegree(source)+ "FF");
					}
				}
			}
		}
}

