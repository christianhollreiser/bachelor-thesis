package Restricted_DP;

public class DepotEndNode extends Node 
{
	
	// predecessor
	DepotBeginNode predecessorDepot;
		
	// successor
	DepotBeginNode successorDepot;
	
	public DepotEndNode(String nodeID, short nodeIndex, double x, double y, double wB, double wE,double demand, double serviceTime) 
	{
		super(nodeID,nodeIndex, x, y, wB, wE, demand, serviceTime);
	}
	
	public DepotEndNode(String nodeID, short nodeIndex, double wB, double wE,double demand, double serviceTime) 
	{
		super(nodeID,nodeIndex,wB, wE, demand, serviceTime);
	}
	
	public DepotEndNode(DepotEndNode other) 
	{
		super(other.getNode(), other.getNodeNum(), other.getBeginTime(),other.getEndTime(), other.getDemand(), other.getServiceTime());
		this.predecessorDepot = other.getPreDepot();
		this.successorDepot = other.getSucDepot();
	}
	
	public void setPreDepot(DepotBeginNode pred)
	{
		predecessorDepot = pred;
	}
	
	public void setSucDepot(DepotBeginNode suc)
	{
		successorDepot = suc;
	}
	
	
	public DepotBeginNode getPreDepot()
	{
		return predecessorDepot;
	}
	
	public DepotBeginNode getSucDepot()
	{
		return successorDepot;
	}
}
