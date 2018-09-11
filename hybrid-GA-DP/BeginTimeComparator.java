package Hybrid_GA_DP;

import java.util.Comparator;
import java.util.Map;

import Restricted_DP.Node;

public class BeginTimeComparator implements Comparator<Short>
{
	private Map<Short, Node> nodeMap;
	
	public BeginTimeComparator(Map<Short, Node> nodeMap)
	{
		this.nodeMap = nodeMap;
	}
	
	@Override
	public int compare(Short o1, Short o2) 
	{
		return (int) Math.signum(nodeMap.get(o1).getBeginTime() - nodeMap.get(o2).getBeginTime());
	}

}
