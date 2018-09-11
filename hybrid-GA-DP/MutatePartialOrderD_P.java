package Hybrid_GA_DP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * This class performs the mutation phase
 * @author Christianhollreiser
 *
 */
public class MutatePartialOrderD_P implements EvolutionaryOperator<ArrayList<Short>, Short> 
{
	// probability to mutate
	private double p_m;
	
	public MutatePartialOrderD_P(double mutationProbability)
	{
		this.p_m = mutationProbability;
	}
	
	@Override
	public Map<Short,Short> apply(Map<Short,Short> partialCommonOrderD, List<ArrayList<Short>> parents, Random rng)  //TODO: Maybe change this mutation to removing each mapping with some very small probability
	{	 
		// initialize mutated partial common order D
		Map<Short,Short> mutatedPartialCommonOrderD = new HashMap<>(partialCommonOrderD);
		
		for(Short pair:partialCommonOrderD.keySet())									
		{
			// probability to choose first form of mutation
			double p = rng.nextDouble();
		
			if(p < p_m) 
			{
				// mutate
				mutatedPartialCommonOrderD.remove(pair);
			}
		}
		
		return mutatedPartialCommonOrderD;
	}

	@Override
	public ArrayList<Short> applyDP(
			Map<Short, Short> partialCommonOrderD,
			List<ArrayList<Short>> parents)
			
	{
		// NOT NEEDED HERE
		return null;
	}

	@Override
	public Map<Short, Short> apply(List<ArrayList<Short>> parents, Random rng) {
		// NOT NEEDED HERE
		return null;
	}

}
