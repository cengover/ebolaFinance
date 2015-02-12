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

public class Hospital {

	public int id;
	public int maxBedCapacity;
	public int demand;
	public int numOfEbola;
	public double stigma;
	public double bedCost;
	public double idleBedCost;
	public double ebolaCost;
	public double profitPerBed;
	public double profitPerEbola;
	public double overheadCost;
	public double ebolaOverhead;
	public double totalCost;
	public double totalProfit;
	public int unmetDemand;
	public int stigmatized;
	public int transfer;
	
	
	public Hospital(int id){
		
		Parameters p = RunEnvironment.getInstance().getParameters();
		this.id = id;
		this.maxBedCapacity=RandomHelper.getUniform().nextIntFromTo(p.getInteger("minCapacity"), p.getInteger("maxCapacity"));	
		this.bedCost = RandomHelper.getUniform().nextDoubleFromTo(p.getDouble("minBedCost"), p.getDouble("maxBedCost"));
		this.idleBedCost = RandomHelper.getUniform().nextDoubleFromTo(p.getDouble("minIdleBedCost"), p.getDouble("maxIdleBedCost"));
		this.ebolaCost = (Double)p.getDouble("ebolaCost");
		this.profitPerBed = RandomHelper.getUniform().nextDoubleFromTo(p.getDouble("minBedProfit"), p.getDouble("maxBedProfit"));
		this.profitPerEbola = (Double)p.getDouble("ebolaProfit");
		this.overheadCost = (Double)p.getDouble("overhead");
		this.ebolaOverhead = 0;
		this.stigma = 1.00;
		this.unmetDemand = 0;
		this.transfer = 0;
	}
	
	//Step function - at every time tick agents run it after they are shuffled - You can add priority to the agent types.
	@ScheduledMethod(start = 0, interval = 1, shuffle = false, priority = 1)
	public void step(){
		// Get Context 
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid) context.getProjection("grid");
	    Network<Hospital> hNetwork = (Network<Hospital>) context.getProjection("hospitalNetwork");
	    // Get parameters
		Parameters p = RunEnvironment.getInstance().getParameters();	
		
		// Cost and Profit functions
		totalCost = bedCost*demand+idleBedCost*(Math.max(maxBedCapacity-demand,0)-numOfEbola*(Integer)p.getInteger("numOfBedForEbola"))+numOfEbola*ebolaCost+ebolaOverhead+overheadCost;
		totalProfit = profitPerBed*demand+profitPerEbola*numOfEbola;
		transfer = 0;
		RunEnvironment.getInstance().endAt(p.getInteger("endOfSim"));	
	}
	public double gettotalProfit(){
		
		return this.totalProfit;
	}
	public double gettotalCost(){
		
		return this.totalCost;
	}
	public double getunmetDemand(){
		
		return this.unmetDemand;
	}
	public int getTime(){
		
		Schedule schedule= (Schedule) RunEnvironment.getInstance().getCurrentSchedule();
		int t = (int) schedule.getTickCount();
		return t;
	}
	public int getId(){

		return this.id;
	}
}

