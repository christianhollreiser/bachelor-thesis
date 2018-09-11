package Restricted_DP;

public class DepotBeginNode extends Node 
{	
	// predecessor
	DepotEndNode predecessorDepot;
		
	// successor
	DepotEndNode successorDepot;
	
	public DepotBeginNode(String nodeID, short nodeIndex, double x, double y, double wB, double wE,double demand, double serviceTime) 
	{
		super(nodeID, nodeIndex , x, y, wB, wE, demand, serviceTime);
	}
	
	public DepotBeginNode(String nodeID, short nodeIndex, double wB, double wE,double demand, double serviceTime) 
	{
		super(nodeID, nodeIndex , wB, wE, demand, serviceTime);
	}
	
	public DepotBeginNode(DepotBeginNode other) 
	{
		super(other.getNode(), other.getNodeNum(),other.getBeginTime(),other.getEndTime(), other.getDemand(), other.getServiceTime());
		this.predecessorDepot = other.getPreDepot();
		this.successorDepot = other.getSucDepot();
	}
	
	public void setPreDepot(DepotEndNode pred)
	{
		predecessorDepot = pred;
	}
	
	public void setSucDepot(DepotEndNode suc)
	{
		successorDepot = suc;
	}
	
	public DepotEndNode getPreDepot()
	{
		return predecessorDepot;
	}
	
	public DepotEndNode getSucDepot()
	{
		return successorDepot;
	}
}
