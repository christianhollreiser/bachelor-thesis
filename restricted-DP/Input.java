package Restricted_DP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Constructs all the input for restricted DP
 * @author Christianhollreiser
 *
 */
public class Input
{
	private File inputData;
	
	private int numVehicles;
	
	private Map<Short, Node> nodeMap;
	
	private short numNodes;
	
	private double[] x;
	
	private double[] y;
	
	private Node[] nodes;
	
	private ArrayList<Short> customerNodes;
	
	public Input(File F, short numberOfVehicles, short numNodes)
	{
		this.inputData = F;
		this.numVehicles = numberOfVehicles;
		this.numNodes = numNodes;
		this.nodes = new Node[2*numberOfVehicles + numNodes -1];
		this.x = new double[2*numberOfVehicles + numNodes -1];
		this.y = new double[2*numberOfVehicles + numNodes -1];
		this.nodeMap = new HashMap<>();
		this.customerNodes = new ArrayList<>(numNodes-1);
	}
	
	public Double getVehicleCapacity()
	{
		// initialize capacity to 0 
		double capacity = 0;
		
		try (Scanner reader = new Scanner(inputData))
		{
			for(int i = 0; i<4;i++)
			{
				reader.nextLine();	
			}
			reader.nextDouble();
			capacity = reader.nextDouble(); //CHANGEBACK
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return capacity;

	}
	
	public ArrayListSet getCityNodes()
	{
		ArrayListSet nodes = new ArrayListSet();
		try (Scanner reader = new Scanner(inputData))
		{
			// remove first lines
			for(int i = 0; i<10;i++)
			{
				reader.nextLine();
			}
			
			for(short i = 0; i<numNodes-1; i++) // numNodes - 1 is the number of customers
			{
				String nodeNum = "r" + reader.next();
				short nodeIndex = i;
				
				double x = reader.nextDouble();
				double y = reader.nextDouble();
				double demand = reader.nextDouble();   
				double beginWindow = reader.nextDouble();
				double endWindow = reader.nextDouble();
				double serviceTime = reader.nextDouble();
				
				Node newNode = new Node(nodeNum,nodeIndex, x, y, beginWindow, endWindow, demand, serviceTime);
				
				// for distance matrix 
				this.nodes[i] = newNode;
				this.x[i] = x;
				this.y[i] = y;
				
				this.nodeMap.put(nodeIndex, newNode);
				
				customerNodes.add(nodeIndex);
				
				nodes.set(nodeIndex);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		return nodes;
	}
	
	public ArrayList<Short> getCustomers()
	{
		return customerNodes;
	}
	
	public ArrayListSet getDepotBeginNodes()
	{
		ArrayListSet nodes = new ArrayListSet();
		try (Scanner reader = new Scanner(inputData))
		{
			// remove first lines
			for(int i = 0; i<9;i++)
			{
				reader.nextLine();
			}
			
			// read data for depot node
			String nodeNum = "o" + reader.next();
			short nodeIndex = (short) (numNodes - 1);
			double x = reader.nextDouble();
			double y = reader.nextDouble();
			double demand = reader.nextDouble();
			double beginWindow = reader.nextDouble();
			double endWindow = reader.nextDouble();
			double serviceTime = reader.nextDouble();
			
			// first begin depot node
			DepotBeginNode firstBegin = new DepotBeginNode(nodeNum, nodeIndex, x, y, beginWindow, endWindow, demand, serviceTime);
			nodes.set(nodeIndex);

			this.nodeMap.put(nodeIndex, firstBegin);
			
			// for distance matrix
			this.nodes[numNodes-1] = firstBegin;
			this.x[numNodes-1] = x;
			this.y[numNodes-1] = y;
			
			for(short i = 1; i<numVehicles; i++)
			{
				// create next begin depot node
				nodeNum = "o" + i;
				nodeIndex = (short) (i + numNodes - 1);
				DepotBeginNode newNode = new DepotBeginNode(nodeNum, nodeIndex, x, y, beginWindow, endWindow, demand, serviceTime);
				nodes.set(nodeIndex);
				
				this.nodeMap.put(nodeIndex, newNode);
				
				// for distance matrix
				this.nodes[i+numNodes-1] = newNode;
				this.x[i+numNodes-1] = x;
				this.y[i+numNodes-1] = y;
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return nodes;
	}
	
	public ArrayListSet getDepotEndNodes()
	{
		ArrayListSet nodes = new ArrayListSet();
		try (Scanner reader = new Scanner(inputData))
		{
			// remove first lines
			for(int i = 0; i<9;i++)
			{
				reader.nextLine();
			}
			
			// read data for depot node
			String nodeNum = "d" + reader.next();
			short nodeIndex = (short) (numNodes + numVehicles - 1);
			double x = reader.nextDouble();
			double y = reader.nextDouble();
			double demand = reader.nextDouble();
			double beginWindow = reader.nextDouble();
			double endWindow = reader.nextDouble();
			double serviceTime = reader.nextDouble();
			
			// first begin depot node
			DepotEndNode firstEnd = new DepotEndNode(nodeNum, nodeIndex, x, y, beginWindow, endWindow, demand, serviceTime);
			nodes.set(nodeIndex);
			
			this.nodeMap.put(nodeIndex, firstEnd);

			
			// for distance matrix
			this.nodes[numNodes + numVehicles -1] = firstEnd;
			this.x[numNodes + numVehicles - 1] = x;
			this.y[numNodes + numVehicles -1] = y;
			
			for(int i = 1; i<numVehicles; i++)
			{
				// create next begin depot node
				nodeNum = "d" + i;
				nodeIndex = (short) (i + numNodes + numVehicles - 1);
				DepotEndNode newNode = new DepotEndNode(nodeNum, nodeIndex, x, y, beginWindow, endWindow, demand, serviceTime);
				nodes.set(nodeIndex);
				
				this.nodeMap.put(nodeIndex, newNode);
				
				// for distance matrix
				this.nodes[ i + numNodes + numVehicles -1] = newNode;
				this.x[i + numNodes + numVehicles -1] = x;
				this.y[i + numNodes + numVehicles -1] = y;
				
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return nodes;
	}
	
	public Map<Short, Node> getNodeMapping()
	{
		return nodeMap;
	}
	
	
	public double[][] getDistanceMatrix()
	{
		int numTotalNodes = this.nodes.length;
		double[][] distanceMatrix = new double[numTotalNodes][numTotalNodes];
		
		for(int i = 0; i<numTotalNodes; i++)
		{
			for(int j = 0; j<numTotalNodes; j++)
			{
				distanceMatrix[this.nodes[i].getNodeNum()][this.nodes[j].getNodeNum()] = Math.sqrt(Math.pow((x[i]-x[j]),2) + Math.pow((y[i] - y[j]),2));
			}
		}
				
		return distanceMatrix;
	}
	
}
