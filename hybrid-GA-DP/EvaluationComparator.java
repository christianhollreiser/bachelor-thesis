package Hybrid_GA_DP;

import java.util.Comparator;

public class EvaluationComparator<T> implements Comparator<Candidate<T>>
{
	/**
	 * Constructor
	 */
	public EvaluationComparator()
	{
		
	}
			
	@Override
	public int compare(Candidate<T> o1, Candidate<T> o2) 
	{
		// primary objective: minimize # vehicles
		if(o1.getNumVehicles() == o2.getNumVehicles())
		{
			// secondary objective: minimize dist. traveled
			return (int) Math.signum(o1.getDist() - o2.getDist());
		}
		return o1.getNumVehicles() - o2.getNumVehicles();
	}
}
