package Restricted_DP;
import java.util.Comparator;
import java.util.Map;

/**
 * Comparator for sorting nodes for nearest neighbours - for parameter E
 * @author Christianhollreiser
 *
 */
public class NearestNeighbourComparator implements Comparator<Short>
{
	private double[] distances;
	
	public NearestNeighbourComparator(Map<Short,Node> nodes, double[] distances)
	{
		this.distances = distances;
	}
	
	
	@Override
	public int compare(Short o1, Short o2) 
	{
		// sort in order of closest neighbours
		return (int) Math.signum(distances[o1]- distances[o2]);
	}

}
