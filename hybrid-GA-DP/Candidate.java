package Hybrid_GA_DP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Candidate<T> implements Comparable<Candidate<T>>
{
	// tour for candidate
	private T tour;
	
	// total distance of tour
	private double totalDistance;
	
	// total number of vehicles used in tour
	private short numVehicles;
	
	// fitness
	private double fitness;
	
	/**
	 * Constructor initializes candidate variables
	 * @param tour
	 * @param totalDistance
	 * @param numVehicles
	 * @param overCapacity
	 * @param overTime
	 */
	public Candidate(T tour, double totalDistance, short numVehicles)
	{
		this.tour = tour;
		this.totalDistance = totalDistance;
		this.numVehicles = numVehicles;
		// this.overCapacity = overCapacity;
		// this.overTime = overTime;
	}
	
	/**
	 * Constructor 
	 * @param tour
	 * @param totalDistance
	 * @param numVehicles
	 * @param overCapacity
	 * @param overTime
	 */
	public Candidate(double fitness)
	{
		this.fitness = fitness;
	}
	
	public void evaluate(List<Candidate<T>> population, EvaluationComparator<T> evalComparator)
	{
		// sorted population
		List<Candidate<T>> sortedPopulation = new ArrayList<>(population);
		
		// sort population to get rank
		Collections.sort(sortedPopulation,evalComparator);
		
		// index (rank) of candidate
		int rank = sortedPopulation.indexOf(this);
		
		// max rank 
		int maxRank = sortedPopulation.size() - 1;
		
		// get fitness
		double evaluatedFitness = maxRank - rank + 1;	
		
		// update instance variable
		this.fitness = evaluatedFitness;
	}

	/**
	 * gets tour solution for the given candidate
	 * @return - returns tour
	 */
	public T getTour()
	{
		return (T) Collections.unmodifiableList((List<? extends T>) tour);
	}
	
	/**
	 * gets total distance of the tour for candidate
	 * @return - returns total distance
	 */
	public double getDist()
	{
		return totalDistance;
	}
	
	/**
	 * gets number of vehicles in solution of candidate
	 * @return - returns number of vehicles
	 */
	public short getNumVehicles()
	{
		return numVehicles;
	}
		
	/**
	 * gets over-capacity for candidate
	 * @return - returns over-capacity
	 *
	public double getOverCapacity()
	{
		return overCapacity;
	}
		
	/**
	 * gets over-time for candidate
	 * @return - returns over-time
	 *
	public double getOverTime()
	{
		return overTime;
	}*/
	
	/**
	 * get fitness of candidate
	 * @return - return fitness
	 */
	public double getFitness()
	{
		return fitness;
	}

	@Override
	public int compareTo(Candidate<T> o) 
	{
		return (int) Math.signum(this.getFitness() - o.getFitness());
	}
}
