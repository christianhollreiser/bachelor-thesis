package Hybrid_GA_DP;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FitnessProportionateSelection<T>
{
	public Candidate<T> selectIndividual(List<Candidate<T>> potentialCandidates, Random rng)
	{
		// sum fitness
		double totalFitness = 0;
		for(int h = 0; h<potentialCandidates.size(); h++)
		{
			// get candidate
			Candidate<T> currentCandidate = potentialCandidates.get(h);
			// add fitness of candidate
			totalFitness += currentCandidate.getFitness();
		}
		
		// generate random number between 0 and totalFitness
		double rand = rng.nextDouble()*totalFitness;
		
		// sort population on increasing fitness
		Collections.sort(potentialCandidates);
		
		// initialize selected individual
		Candidate<T> selectedCandidate = null;
		
		// locate candidate to select
		for(int j = 0; j<potentialCandidates.size(); j++)
		{
			Candidate<T> nextCandidate = potentialCandidates.get(j);
			rand -= nextCandidate.getFitness();
			
			if(rand<0)
			{
				// select current candidate
				selectedCandidate = nextCandidate;
				
				// stop loop as individual to select found
				break;
			}
		}
		
		return selectedCandidate;
	}
}
