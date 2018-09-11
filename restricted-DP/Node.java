package Restricted_DP;

/**
 * This class is for representing nodes and storing the relevant information
 * @author Christianhollreiser
 *
 */
public class Node implements Comparable<Node>
{
	// node number
	private String nodeID;
	
	// node number for exact dp problem
	private short nodeExactDP;
	
	// ready time
	private double windowBegin;
	
	// latest time
	private double windowEnd;
	
	// demand of city (node)
	private double demand;
	
	// service time
	private double serviceTime;
	
	public Node(String nodeID,short nodeExactDP, double x, double y, double wB, double wE, double demand, double serviceTime)
	{
		this.nodeID = nodeID;
		// this.xCoord = x;
		// this.yCoord = y;
		this.windowBegin = wB;
		this.windowEnd = wE;
		this.demand = demand;
		this.serviceTime = serviceTime;
		this.nodeExactDP = nodeExactDP;
	}
	
	public Node(String nodeID,short nodeExactDP, double wB, double wE, double demand, double serviceTime)
	{
		this.nodeID = nodeID;
		// this.xCoord = x;
		// this.yCoord = y;
		this.windowBegin = wB;
		this.windowEnd = wE;
		this.demand = demand;
		this.serviceTime = serviceTime;
		this.nodeExactDP = nodeExactDP;
	}
	
	public Node(Node other)
	{
		this.nodeID = other.getNode();
		// this.xCoord = other.getX();
		// this.yCoord = other.getY();
		this.windowBegin = other.getBeginTime();
		this.windowEnd = other.getEndTime();
		this.demand = other.getDemand();
		this.serviceTime = other.getDemand();
		this.nodeExactDP = other.getNodeExactDP();
	}
	
	public short getNodeNum()
	{
		return nodeExactDP;
	}
	
	public String getNode()
	{
		return nodeID;
	}
	
	public short getNodeExactDP()
	{
		return nodeExactDP;
	}
	
	public void setNodeExactDP(short node)
	{
		nodeExactDP = node;
	}
	
	public double getBeginTime()
	{
		return windowBegin;
	}
	
	public double getEndTime()
	{
		return windowEnd;
	}
	
	public double getDemand()
	{
		return demand;
	}
	
	public double getServiceTime()
	{
		return serviceTime;
	}


	@Override
	public int compareTo(Node o) 
	{
		if(this.nodeID.equals(o.nodeID))
		{
			return 0;
		}
		else if((int) this.nodeID.charAt(0) == (int) o.nodeID.charAt(0))
		{
			return Integer.parseInt(this.nodeID.substring(1)) - Integer.parseInt(o.nodeID.substring(1));
		}
		else
		{
			return (int) Math.signum((double) ((int) o.nodeID.charAt(0)) - ((int) this.nodeID.charAt(0)));
		}
	}

	@Override
	public String toString() {
		return nodeID;
	}
}

	