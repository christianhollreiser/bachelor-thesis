package Restricted_DP;

import java.util.ArrayList;


public class State implements Comparable<State>
{
	// subset S of state
	private ArrayListSet subSet;
	
	// number of vehicles used
	private short numVehiclesUsed;
	
	// destination node j of state 
	private short j;
	
	// minimum cost of state C(S,j)
	private double minCost;
	
	// current capacity 
	private double currentCapacity;
	
	// current time
	private double currentTime;
	
	// final number vehicles used
	private short finalNumberVehiclesUsed;
	
	private ArrayList<Short> ListS; 
	
	
	
	/**
	 * Constructor
	 * @param S
	 * @param j
	 * @param minCost
	 * @param m
	 */
	public State(ArrayListSet S, short j, short i, double minCost)
	{
		this.subSet = S;
		this.j = j;
		this.minCost = minCost;
	}
	
	public State(ArrayListSet S, ArrayList<Short> ListS, short j, Node nodeI, Node nodeJ, double minCost,double initialCapacity, double initialTime,short prevNumVehiclesUsed, short finalNumberVehiclesUsed) 
	{
		this.subSet = S;
		this.minCost = minCost;
		this.j = j; 
		this.ListS = ListS;
		
		if((nodeI instanceof DepotBeginNode)&& !(nodeJ instanceof DepotEndNode))
		{
				this.finalNumberVehiclesUsed = (short) (finalNumberVehiclesUsed + 1);
		}
		else
		{
			this.finalNumberVehiclesUsed = finalNumberVehiclesUsed;
		}
		
		if((nodeJ instanceof DepotEndNode))
		{
			this.numVehiclesUsed = (short) (prevNumVehiclesUsed + 1);
		}
		else
		{
			this.numVehiclesUsed = prevNumVehiclesUsed;
		}
		
		this.currentCapacity=initialCapacity;
		this.currentTime = initialTime;
	}

	public State(State other) 
	{
		this.subSet = other.getS();
		this.minCost = other.getDistanceTravelled();
		this.currentCapacity= other.getCapacity();
		this.currentTime = other.getTime();
		this.j = other.getJ();
		this.numVehiclesUsed = other.getNumVehiclesUsed();
		this.finalNumberVehiclesUsed = other.getFinalNumVehiclesUsed();
		this.ListS = other.getListS();
		
	}
	
	public ArrayList<Short> getListS()   
	{
		return ListS;
	}
	
	public double getDistanceTravelled()
	{
		return minCost;
	}
	
	public short getJ()
	{
		return j;
	}
	
	public short getNumVehiclesUsed()
	{
		return numVehiclesUsed;
	}
	
	public short getFinalNumVehiclesUsed()
	{
		return finalNumberVehiclesUsed;
	}
	
	public ArrayListSet getS()
	{	
		// return subset
		return subSet;
	}
	
	public double getCapacity()
	{
		return currentCapacity;
	}
	
	public double getTime()
	{
		return currentTime;
	}
	
	public void updateCapacity(double newCapacity)
	{
		currentCapacity = newCapacity;
	}
	
	public void updateTime(double newTime)
	{
		currentTime = newTime;
	}
	

	@Override
	public int compareTo(State o) 
	{
		
		if(this.getNumVehiclesUsed() == o.getNumVehiclesUsed())
		{
			if(this.minCost == o.minCost)
			{
				if(this.getTime() == o.getTime())
				{
					return (int) Math.signum(o.getCapacity() - this.getCapacity());
				}
				return (int) Math.signum(this.getTime() - o.getTime());
			}
			return (int) Math.signum(this.minCost - o.minCost);
		}
		return this.getNumVehiclesUsed() - o.getNumVehiclesUsed();
	}

	@Override
	public String toString() {
		return "(" + ListS.toString() + "," + j +")";
	}
	
	
}
