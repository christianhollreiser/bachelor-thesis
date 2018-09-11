package Hybrid_GA_DP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class constructs the partial common order set of a given pair of parents
 * @author Christianhollreiser
 *
 */
public class Crossover implements EvolutionaryOperator<ArrayList<Short>,Short>
{	
	public Crossover()
	{
		
	}

	@Override
	public Map<Short, Short> apply(List<ArrayList<Short>> parents, Random rng) 
	{
		Map<Short,Short> partialCommonOrderD = new HashMap<>();
		
		ArrayList<Short> parent1 = new ArrayList<>(parents.get(0));
		ArrayList<Short> parent2 = new ArrayList<>(parents.get(1));
		
		for(int indexIp1 = 0; indexIp1 < parent1.size(); indexIp1++)
		{
			// index of next neighbouring node j in parent 1
			int indexJp1 = (indexIp1+1) % parent1.size();
			
			// index of node i in parent 2
			int indexIp2 = parent2.indexOf(parent1.get(indexIp1));
			// index of next neighbouring node j in parent 2
			int indexJp2 = (indexIp2+1) % parent2.size();
			
			// check if j succeeds i in parent2 as well
			if(parent2.get(indexJp2) == parent1.get(indexJp1))
			{
				partialCommonOrderD.put(parent1.get(indexIp1), parent2.get(indexJp2));
			}
		}
			
		return partialCommonOrderD;
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
	public Map<Short, Short> apply(Map<Short, Short> partialCommonOrderD,
			List<ArrayList<Short>> parents, Random rng) {
		// NOT NEEDED HERE
		return null;
	}
}
