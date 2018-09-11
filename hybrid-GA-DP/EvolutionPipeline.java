package Hybrid_GA_DP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
/**
 * Class performs the operators such as crossover and mutation on a given population
 * @author Christianhollreiser
 *
 * @param <T> 
 * @param <E> 
 */

public class EvolutionPipeline<T,E> extends FitnessProportionateSelection<T> implements EvolutionaryOperator<T,E> 
{
	// list of evolutionary operators to apply on current generation to obtain next generation 
	private final List<EvolutionaryOperator<T,E>> pipeline;
	
	// list of solutions/performance measures for an evolved population
	private Map<T,Double[]> performanceMeasures;
	
	// number of crossovers
	private final int numberOfMatings;

	/**
	 * constructor accepts a list of evolutionary operators and the number of crossovers to do with a given population
	 * @param pipeline - list of evolutionary operators
	 * @param numCrossovers - total number of crossovers to make
	 */
	public EvolutionPipeline(List<EvolutionaryOperator<T,E>> pipeline, int numMatings) 
	{
        this.pipeline = new ArrayList<EvolutionaryOperator<T,E>>(pipeline);
        
        //get number crossovers to do
        this.numberOfMatings = numMatings;
        
        // intialize performance measures
        performanceMeasures = new HashMap<>();
	}
	

	/**
	 * This method applies the evolutionary operators to the current generation
	 * @param selectedCandidates
	 * @param rng
	 * @return
	 */
	public List<T> applyOperators(List<Candidate<T>> selectedCandidates, Random rng) 
	{
		// initialize current population
		List<Candidate<T>> population = selectedCandidates;
		
		// intialize new generation
		List<T> newGeneration = new ArrayList<>();
		
		// intialize number of new candidates created
		int n = 0;
		
		// possible first parents
		List<Candidate<T>> possibleFirstParents = new ArrayList<>(population);
		
		while(n < numberOfMatings)
		{
			// initialize selected parents list for each crossover
			List<T> parents = new ArrayList<>(2);
			
			// select parent using fitness proportionate selection
			Candidate<T> firstParent = selectIndividual(possibleFirstParents,rng);
	
			List<Candidate<T>> possibleSecondParents = new ArrayList<>(population);
			possibleSecondParents.remove(firstParent);
			
			// select second parent using fitness proportionate selection
			Candidate<T> secondParent = selectIndividual(possibleSecondParents,rng);
			
			// add parents
			parents.add(firstParent.getTour());
			parents.add(secondParent.getTour());

			// crossover to get partial common order D
			Map<E,E> partialCommonOrderD = pipeline.get(0).apply(parents, rng);
			
			// mutate partial common order D
			Map<E,E> mutatedD = pipeline.get(1).apply(partialCommonOrderD,parents,rng);
			 
			// apply DP using partial common order map D from crossover
			CrossoverDP<T,E> crossoverDPOperator = (CrossoverDP<T,E>) pipeline.get(2);
			
			T newIndividual = crossoverDPOperator.applyDP(mutatedD, parents);
			
			// add solution and performance measure mapping
			performanceMeasures.put(newIndividual, crossoverDPOperator.getPerformanceMeasuresDP());

			// add new candidate
			newGeneration.add(newIndividual);
			// update number of new candidates created
			n++;

		}	
		return newGeneration;
	}
	
	/**
	 * gets the performance measures for the evolved population
	 * @return
	 */
	public  Map<T,Double[]> getEvolvedPopPM()
	{
		return performanceMeasures;
	}


	@Override
	public T applyDP(Map<E, E> partialCommonOrderD, List<T> parents) 
	{
		// NOT NEEDED HERE
		return null;
	}


	@Override
	public Map<E, E> apply(Map<E, E> partialCommonOrderD, List<T> parents,
			Random rng) 
	{
		// NOT NEEDED HERE
		return null;
	}


	@Override
	public Map<E, E> apply(List<T> parents, Random rng) 
	{
		// NOT NEEDED HERE
		return null;
	}

}
