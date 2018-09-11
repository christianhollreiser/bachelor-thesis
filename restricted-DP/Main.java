package Restricted_DP;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
/**
 * This class prepares all the necessary input and elements for the restricted DP algorithm
 * Also runs the algorithm
 * @author Christianhollreiser
 *
 */
 class Main 
{
	private static Map<ArrayList<Short>, Double[]> solutionRestricted = null;

	private short numV;
	
	private File f;
	
	private int E;
	
	private int H;
	
	public Main(short numV, File f, int H, int E)
	{
		this.numV = numV;
		this.f = f;
		this.H = H;
		this.E = E;
	}
	
	public void runAlgorithms()
	{
		// File f = new File("C101.txt");
		Input input = new Input(f,numV, (short) 101);  
		
		ArrayListSet cities = input.getCityNodes();
		ArrayList<Short> customerNodes = input.getCustomers();
		ArrayListSet depotBeginNodes = input.getDepotBeginNodes();
		ArrayListSet depotEndNodes = input.getDepotEndNodes();
		Map<Short,Node> nodeMapping = input.getNodeMapping();
		double[][] distances = input.getDistanceMatrix();
		
		cities.or(depotBeginNodes);
		cities.or(depotEndNodes);
	
		Double vehicleCapacity = input.getVehicleCapacity();
		

		// set pre and successor for end depot nodes
		for(short i = (short) depotEndNodes.nextSetBit( 0 ); i >= 0; i = (short) depotEndNodes.nextSetBit( i + 1 )) 
		{
			System.out.print(nodeMapping.values());
			DepotBeginNode pred = (DepotBeginNode) nodeMapping.get((short) (i-numV));
			
			// successor
			DepotBeginNode suc;
			if(i==depotEndNodes.previousSetBit(depotEndNodes.length()))
			{
				suc = (DepotBeginNode) nodeMapping.get((short) depotBeginNodes.nextSetBit(0)); // first begin depot
			}
			else
			{
				suc = (DepotBeginNode) nodeMapping.get((short) (i+1-numV));
			}
			
			// set pre and successor node
			((DepotEndNode) nodeMapping.get((short) i)).setPreDepot(pred);
			((DepotEndNode) nodeMapping.get((short) i)).setSucDepot(suc);
		}
		
		// set pre and successor for begin depot nodes
		for(short i = (short) depotBeginNodes.nextSetBit( 0 ); i >= 0; i = (short) depotBeginNodes.nextSetBit( i + 1 )) 
		{
			// predecessor 
			DepotEndNode pred;
			if(i== depotBeginNodes.nextSetBit(0))
			{
				int index = depotEndNodes.previousSetBit(depotEndNodes.length());
				pred = (DepotEndNode) nodeMapping.get((short) index);
			}
			else
			{
				int index = i-1;
				pred = (DepotEndNode) nodeMapping.get((short) (index + numV));
			}
			
			// successor
			DepotEndNode suc = (DepotEndNode) nodeMapping.get((short) (i + numV));
			
			// set pre and successor node
			((DepotBeginNode) nodeMapping.get((short) i)).setPreDepot(pred);
			((DepotBeginNode) nodeMapping.get((short) i)).setSucDepot(suc);
		}
		
		//------------------ RESTRICTED STATE SPACE DP ALGORITHM ----------------

		/////////////////////////////////////////
		
		// begin cpu time
		long starttime = System.currentTimeMillis();
		
		//RestrictedSpaceDPAlgorithm testRestricted = new RestrictedSpaceDPAlgorithm (cities,depotBeginNodes,depotEndNodes, H , E, vehicleCapacity);
		RestrictedDPAlgorithm testRestricted = new RestrictedDPAlgorithm(nodeMapping, cities,depotBeginNodes,depotEndNodes, H , E, vehicleCapacity, distances, customerNodes);

		Map<ArrayList<Short>, Double[]> solutionRestrictedDP = testRestricted.runAlgorithm();
		
		solutionRestricted = solutionRestrictedDP;
		
		// end cpu time
		long endtime = System.currentTimeMillis();
		
		// print cpu time
		System.out.println("CPU time in seconds: " + (endtime - starttime)/1000);


	}
	
	public Map<ArrayList<Short>, Double[]> getSolution()
	{
		return solutionRestricted;
	}
}
