package Hybrid_GA_DP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SelectionStrategy<T> extends FitnessProportionateSelection<T>
{

	public SelectionStrategy()
	{
		
	}
	
	
	/**
	 * selection procedure in which a subset of individuals are selected through 
	 * fitness proportionate selection (also known as roulette wheel selection)
	 * @param population
	 * @param selectionSize
	 * @param rng
	 * @return
	 */
	public List<T> select(List<Candidate<T>> evaluatedPopulation, int selectionSize, Random rng) 
	{
		// initialize list of selected individuals
		List<T> selectedIndividuals = new ArrayList<>(selectionSize);
		
		// population of candidates to select from
		List<Candidate<T>> potentialCandidates = new ArrayList<>(evaluatedPopulation);
		
		// select candidates 
		for(int i = 0; i<selectionSize; i++)
		{
			Candidate<T> selectedCandidate = selectIndividual(potentialCandidates, rng);
			
			// add selected individual to list
			selectedIndividuals.add(selectedCandidate.getTour());
			
			// remove last selected individual from potential candidates
			potentialCandidates.remove(selectedCandidate);
		}
		
		return (List<T>) selectedIndividuals;				//TODO: decide whether to make type throughout allll classes or to just change all to ArrayList<Short>
	}
}
