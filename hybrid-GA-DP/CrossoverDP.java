package Hybrid_GA_DP;

import java.util.List;
import java.util.Map;
import java.util.Random;

import Restricted_DP.RestrictedDPAlgorithmGA;
/**
 * This class performs the crossover by DP phase
 * @author Christianhollreiser
 *
 * @param <T>
 * @param <E>
 */
public class CrossoverDP<T,E> implements EvolutionaryOperator<T, E>
{
	// restricted DP object
	private final RestrictedDPAlgorithmGA restrictedDP;
	
	// solutionDP
	Double[] performanceMeasures;

	/**
	 * Constructor accepts a restricted DP (for GA) object to 
	 * initialize the instance variable
	 * @param restrictedDP
	 */
	public CrossoverDP(RestrictedDPAlgorithmGA restrictedDP)
	{
		this.restrictedDP = restrictedDP;
		this.performanceMeasures = new Double[2];
	}
	
	@Override
	public T applyDP(Map<E, E> partialCommonOrderD, List<T> parents) 
			
	{
		
		// apply DP subject to partial order D common to parents
		
		// set partial order D for restricted DP 
		Map<Short, Short> partialOrderD = (Map<Short, Short>) partialCommonOrderD; 
		restrictedDP.setPartialOrderD(partialOrderD);
		
		// get solution
		Map<T, Double[]>solutionDP = (Map<T, Double[]>) restrictedDP.runAlgorithm();
		
		// get new individual (tour)
		T newIndividual = solutionDP.keySet().iterator().next();
		
		// get performance measures
		Double[] pm = solutionDP.values().iterator().next();
		performanceMeasures[0] = pm[0];
		performanceMeasures[1] = pm[1];
		
		return newIndividual;
	}
	
	/**
	 * gets the solution of the most recent DP algorithm run including tour and performance measures
	 * 
	 * @return - returns a map containing: solution tour (ArrayList<Short>) and performance measures (Double[])
	 */
	public Double[] getPerformanceMeasuresDP()
	{
		return performanceMeasures;
	}

	@Override
	public Map<E, E> apply(List<T> parents, Random rng) 
	{
		// NOT NEEDED HERE
		return null;
	}

	@Override
	public Map<E, E> apply(Map<E, E> partialCommonOrderD, List<T> parents, Random rng) 
			{
		// NOT NEEDED HERE
		return null;
	}

}
