package ebola;


import java.util.Iterator;
import java.util.LinkedList;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;

public class Population {
	
	public LinkedList<Hospital> hList;
	
	public Population(){
		
		this.hList = new LinkedList<Hospital>();
	}
	
	//Step function - at every time tick agents run it after they are shuffled - You can add priority to the agent types.
		@ScheduledMethod(start = 0, interval = 1, shuffle = false, priority = 0)
		public void step(){
			
			// Get Context 
			Context context = ContextUtils.getContext(this);
			Grid grid = (Grid) context.getProjection("grid");
		    Network<Hospital> hNetwork = (Network<Hospital>) context.getProjection("hospitalNetwork");
		    // Get parameters
			Parameters p = RunEnvironment.getInstance().getParameters();	
		    Hospital hosp = new Hospital (0);
		    // For all hospitals decide on the demand stream
		    int index = 0;
		    for (Iterator<Hospital> iterator = this.hList.iterator(); iterator.hasNext();) {
				
		    	index = index +1;
				hosp = (Hospital)iterator.next();
				// Here Allocate unmet demands
				int t = getTime();
				// Stigmatization mechanism
				if (hosp.id == 0){
					if (t < p.getInteger("ebolaEnd") && t > p.getInteger("ebolaOnset")) {
						
						hosp.numOfEbola = p.getInteger("ebolaDemand");
					}
					else{
						
						hosp.numOfEbola = 0;
					}
					if (hosp.numOfEbola > 0){
					
						hosp.stigma = hosp.stigma - (hosp.stigma-0.5)*0.1;
						hosp.ebolaOverhead = (Double)p.getDouble("ebolaOverhead");
					}
					else if (hosp.numOfEbola == 0 && t>p.getInteger("ebolaOnset")){
					
						hosp.stigma = hosp.stigma + (0.9-hosp.stigma)*0.05;
						hosp.ebolaOverhead = 0;
					}
					else{
					
						hosp.stigma = 1;
						hosp.ebolaOverhead = 0;
					}
				}
				else {
					
					hosp.stigma = 1;
					hosp.ebolaOverhead = 0;
				}
				double d = 0;
				if (p.getDouble("demandDist") == 0){
					
					d = (Integer)p.getInteger("demand");
				}
				else {
					
					d = RandomHelper.createPoisson((Integer)p.getInteger("demand")).nextInt();
				}
		   		hosp.demand = (int)d;
				hosp.demand = (int) Math.ceil(hosp.stigma*hosp.demand)+hosp.transfer;
				hosp.demand = Math.min(hosp.demand,hosp.maxBedCapacity);
				if (d-hosp.demand > 0){
					
					hosp.unmetDemand = (int)(d-hosp.demand);
				}
				
				if (hosp.unmetDemand > 0&&index!=this.hList.size()){
					
					this.hList.get(index).transfer = hosp.unmetDemand;
				}
				/*// Total Weight - For Roulette Wheel purposes
				double totalWeight;
				if (hosp.unmetDemand > 0){
					
					for(Iterator<RepastEdge<Hospital>> iterator =  hNetwork.getEdges(hosp).iterator(); iterator.hasNext();) {
						
						// Accumulate social influence of each beneficiary whom the agent is connected to.
						RepastEdge<Hospital> h = iterator.next();
						totalWeight = totalWeight + h.getWeight();
					}	
					// Cumulative weight
					double temp = 0;
					double r = RandomHelper.nextDoubleFromTo(0, 1);
					for(Iterator<RepastEdge<Hospital>> iterator =  hNetwork.getEdges(hosp).iterator(); iterator.hasNext();) {
						
						// Accumulate social influence of each beneficiary whom the agent is connected to.
						RepastEdge<Hospital> h = iterator.next();
						temp = temp + h.getWeight();
						if (r<temp/totalWeight){
							
							h.getTarget().transfer = hosp.unmetDemand;
						}		
					}			
				}*/
			}
		}
		public int getTime(){
			
			Schedule schedule= (Schedule) RunEnvironment.getInstance().getCurrentSchedule();
			int t = (int) schedule.getTickCount();
			return t;
		}
}
