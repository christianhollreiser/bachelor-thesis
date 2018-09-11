package Restricted_DP;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is a similar but adapted version of the restricted DP algorithm class. 
 * It is adapted for GA by including the checks for the partial order D common to two parents
 * @author Christianhollreiser
 *
 */
public class RestrictedDPAlgorithmGA
{
	// partial order D common to the two parents from GA crossover
	private Map<Short, Short> partialCommonOrderD;
	
	// list of states
	private ArrayList<State> states;
	
	// set of expanded states
	private Set<State> statesSet;
	
	// all the nodes
	private ArrayListSet cities;
	
	// customer nodes
	private ArrayListSet customerCities;
	
	// depot end nodes
	private ArrayListSet depotEndNodes;
	
	// depot begin nodes
	private ArrayListSet depotBeginNodes;
	
	// Mapping of nodes to their id value
	private Map<Short, Node> nodes;
	
	// max number expansions
	private int E;
	
	// max number subsets to expand to next stage
	private int H;
	
	// vehicle capacity
	private double vehicleCapacity;
	
	// distance matrix
	private double[][] distMatrix;
	
	// number of times infeasible partial order
	private int numInfeasPartialOrder;
	
	// number of times feasible partial order
	private int numFeasPartialOrder;
	
	// initial C(S,j)
	private Map<ArrayListSet,Map<Short,ArrayList<State>>> initialC_S_j = new HashMap<>();	
	
	public RestrictedDPAlgorithmGA(Map<Short,Node> nodeMapping, ArrayListSet cities,ArrayListSet depotBeginNodes,ArrayListSet depotEndNodes, int H, int E, double vehicleCapacity, double[][] distances)
	{
		// initialize node input
		this.cities = cities;
		this.depotBeginNodes = depotBeginNodes;
		this.depotEndNodes = depotEndNodes;
		this.customerCities = new ArrayListSet();
		this.customerCities.or(cities);
		this.customerCities.andNot(depotEndNodes);
		this.customerCities.andNot(depotBeginNodes);
		this.distMatrix = distances;
		
		this.nodes = nodeMapping;

		// initialize DP restriction parameters
		this.H = H;
		this.E = E;
		
		// initialize extension parameters
		this.vehicleCapacity = vehicleCapacity;
		
		// initialize list of states
		this.states = new ArrayList<State>();
		this.statesSet = new HashSet<>();
		
		numInfeasPartialOrder = 0;
	}
	
	/**
	 * This method sets the new partial order D common to 
	 * both parents candidates for crossover. This is to be checked 
	 * when making the new individual using DP.
	 * @param partialCommonOrderD - new partial order D common to both parents
	 */
	public void setPartialOrderD(Map<Short, Short> partialCommonOrderD)
	{
		this.partialCommonOrderD = partialCommonOrderD;
	}
	
	/**
	 * Initialization: C({o0,j},j) = c0j 
	 * for each initial state ({o0,j},j) forall j
	 */
	public void initialize()
	{	
		// prev dest node
		short i = (short) depotBeginNodes.nextSetBit(0);
		
		// current subset to expand
		ArrayListSet currentSubSetToExpand = new ArrayListSet();
		currentSubSetToExpand.set(i);
		
		ArrayList<Short> ListSToExpand = new ArrayList<>();
		ListSToExpand.add(i);
		
		// current state release time
		double currentStateReleaseTime = 0;
		
		// get all possible j for initial stage
		ArrayListSet feasibleJ = getFeasibleJExpansions(i,currentSubSetToExpand,vehicleCapacity,currentStateReleaseTime);
		
		// get remaining customers without previous sub set S AND j 
		ArrayListSet remainingCustomers = new ArrayListSet();
		remainingCustomers.or(customerCities);
		
		// initialize num vehicles used so far to 0
		short numVehiclesUsed = 0;
		short finalNumberVehiclesUsed = 0;
		
		// construct initial C(S,j)
		for(short j = (short) feasibleJ.nextSetBit( 0 ); j >= 0; j = (short) feasibleJ.nextSetBit( j + 1 ) ) 
		{	
			// min cost C(S,j) for current (S,j)
			double minDistance = distMatrix[i][j];

			// subset of size 2 containing destination j
			ArrayListSet setJ = new ArrayListSet();
			// add depot
			setJ.set(i);
			// add jth city 
			setJ.set(j);	
			
			ArrayList<Short> ListS = new ArrayList<>();
			ListS.add(i);
			ListS.add(j);
			
			// remove j from remaining customers
			remainingCustomers.clear(j);
			
			// check if demand of initial customer not above max vehicle capacity
			if(enoughCapacity(remainingCustomers, setJ,vehicleCapacity,j) && timeFeasible(remainingCustomers, setJ,currentStateReleaseTime, minDistance, j) && partialOrderCompatible(i,j))
			{
				// update new capacity
				double newCapacity = getCapacityNewState(vehicleCapacity,nodes.get(j));
				
				// new time
				double newStateReleaseTime = getTimeNewState(currentStateReleaseTime,minDistance, nodes.get(j));  
				
				State newState = new State(setJ,ListS, j, nodes.get(i), nodes.get(j), minDistance,newCapacity, newStateReleaseTime, numVehiclesUsed, finalNumberVehiclesUsed);

				// add state to total list of states
				states.add(newState);
			}
			
			// add j back to remaining customers for next j
			remainingCustomers.set(j);
		}
		// sort list of states for size m based on cost 
		Collections.sort(states);
		
		// get min H and states to take forward
		ArrayList<State> statesToGoForward = new ArrayList<>(states.subList(0, Math.min(H, states.size()))); 
		
		for(State nextState:statesToGoForward)
		{		
			Map<Short,ArrayList<State>> jState = new HashMap<>();
			
			// array containing best state for j for [mincost, maxcapacity, mintime]
			ArrayList<State> nextStates = new ArrayList<State>();
			State newState = new State(nextState);
			nextStates.add(newState);
			nextStates.add(newState);
			nextStates.add(newState);

			
			jState.put(nextState.getJ(), nextStates);
			
			
			// map intial C_S_j
			initialC_S_j.put(nextState.getS(), new HashMap<>(jState));
		}

		// remove all states of current stage in order to use for next stage
		states.clear();

	}
	
	public Map<ArrayList<Short>, Double[]> runAlgorithm()
	{
		// initialize the initial states
		initialize();
		
		// total number of cities (nodes)
		int numCities = cities.length();
		
		// initial C(S,j)
		Map<ArrayListSet,Map<Short,ArrayList<State>>> C_S_j_prev = initialC_S_j;
		
		for(int m = 3; m<=numCities; m++)
		{	
			// initialize mapping to hold new set of H states for this stage
			Map<ArrayListSet,Map<Short,ArrayList<State>>> C_S_j_new = new HashMap<>();
			
			// iterate through each S for current stage
			for(ArrayListSet previousSubSet:C_S_j_prev.keySet())
			{
				// iterate through state with set S
				for(ArrayList<State> nextPreviousStates:C_S_j_prev.get(previousSubSet).values())
				{	
					// initialize current state
					State currentState = null;
					
					// iterate through each state with same S and j
					for(State nextPreviousState:nextPreviousStates)
					{
						if(nextPreviousState != currentState)
						{
							// update current state
							currentState = nextPreviousState;
		
							// get node i (j for previous state)
							short i = nextPreviousState.getJ(); 
							
							// current time and capacity 
							double currentCapacityVehicle = nextPreviousState.getCapacity();
							double currentStateReleaseTime = nextPreviousState.getTime();
							
							// get set of possible j nodes
							ArrayListSet setPossibleJ = getFeasibleJExpansions(i,nextPreviousState.getS(), currentCapacityVehicle, currentStateReleaseTime);
							
							// create comparator for E restriction
							NearestNeighbourComparator nearestNeighbourSorter = new NearestNeighbourComparator(nodes,distMatrix[i]);
							
							// convert to list to be able to sort
							ArrayList<Short> feasibleJ = new ArrayList<Short>();
							for( short j = (short) setPossibleJ.nextSetBit( 0 ); j >= 0; j = (short) setPossibleJ.nextSetBit( j + 1 ) )
							{
								feasibleJ.add(j);
							}
							
							// sort in order of nearest neighbour
							Collections.sort(feasibleJ, nearestNeighbourSorter);
			
							// initialize number of expansions for current prev state
							int numberOfExpansions = 0;
							
							// get unvisited customers by current prev state
							ArrayListSet remainingCustomers = new ArrayListSet();
							remainingCustomers.or(customerCities);
							remainingCustomers.andNot(nextPreviousState.getS());
							
							for(Short j: feasibleJ) 
							{	
								if(numberOfExpansions == E)
								{
									break; // stop loop after E feasible nearest neighbour expansions
								}
								
								ArrayListSet expandedSet = new ArrayListSet(nextPreviousState.getS());
								expandedSet.set(j);
								
								ArrayList<Short> expandedList = new ArrayList<>(nextPreviousState.getListS());
								expandedList.add(j);
								
								// remove j from unvisited customers
								remainingCustomers.clear(j);
								
								// check time window and capacity constraints
								if(enoughCapacity(remainingCustomers, expandedSet,currentCapacityVehicle,j) && timeFeasible(remainingCustomers, expandedSet,currentStateReleaseTime, distMatrix[i][j], j))
								{
									// compute C(S\j,i) + Cij -> cost of expanded state
									double distance = nextPreviousState.getDistanceTravelled() + distMatrix[i][j]; 
		
									// compute new state capacity
									double newCapacity = getCapacityNewState(currentCapacityVehicle,nodes.get(j));
									
									// compute new state release time
									double newStateReleaseTime = getTimeNewState(currentStateReleaseTime, distMatrix[i][j], nodes.get(j)); 
									
									// get new state 
									State potentialNewState = new State(expandedSet, expandedList, j, nodes.get(i), nodes.get(j), distance, newCapacity,newStateReleaseTime, nextPreviousState.getNumVehiclesUsed(), nextPreviousState.getFinalNumVehiclesUsed()); 
									
									// create new states list for dominance criteria in case of new mapping
									ArrayList<State> newStatesSet = new ArrayList<>(3); // [minCostStateCopy, maxCapacityStateCopy, minReleaseTimeStateCopy]
									newStatesSet.add(potentialNewState);
									newStatesSet.add(potentialNewState);
									newStatesSet.add(potentialNewState);
									
									
									if(C_S_j_new.containsKey(expandedSet))
									{
										if(C_S_j_new.get(expandedSet).containsKey(j))
										{
											// get current state for (S,j)
											ArrayList<State> currentStates = C_S_j_new.get(expandedSet).get(j);
											
											// new states to be possibly updated
											ArrayList<State> newStates = C_S_j_new.get(expandedSet).get(j);		
											
											// boolean if potential new state used
											boolean newStateUsed = false;
											
											// check dominance
											if(potentialNewState.getDistanceTravelled()<=currentStates.get(0).getDistanceTravelled())
											{
												// replace current min distance state with new
												newStates.remove(0);
												newStates.add(0, potentialNewState);
												newStateUsed = true;
											}
											if(potentialNewState.getCapacity()>=currentStates.get(1).getCapacity())
											{
												// replace current max remaining capacity state with new
												newStates.remove(1);
												newStates.add(1, potentialNewState);
												newStateUsed = true;

											}
											if(potentialNewState.getTime() <= currentStates.get(2).getTime())
											{	
												// replace current min release time state with new
												newStates.remove(2);
												newStates.add(2,potentialNewState);
												newStateUsed = true;
											}	
											
											// if state used increment # expansions
											if(newStateUsed)
											{
												numberOfExpansions++;
											}
										}
										else
										{	
											// add new mapping for end node j
											C_S_j_new.get(expandedSet).put(j,newStatesSet);
											numberOfExpansions++;
										}
									}
									else
									{	
										// add new mapping for end node j
										Map<Short,ArrayList<State>> nodeStateMap = new HashMap<>();
										nodeStateMap.put(j, newStatesSet);
										
										// add new mapping for current expanded set (S+j)
										C_S_j_new.put(expandedSet, nodeStateMap);
										numberOfExpansions++;
									}
								}
								
								// add j back to remaining customers for next j
								remainingCustomers.set(j);
							}
						}
					}
				}
			}							 						
			
			// add all expanded states to set of new states
			for(Map<Short,ArrayList<State>> nextMap:C_S_j_new.values())
			{
				for(ArrayList<State> nextStateGroup:nextMap.values())
				statesSet.addAll(nextStateGroup);
			}
		
			// add all expanded states to list of states for sorting
			states.addAll(statesSet);
			
			// sort list of states based on hierarchy 
			Collections.sort(states);								 						
			
			// get min H and states to take forward
			List<State> statesToRemove = states.subList(Math.min(H, states.size()), states.size()); 
			
			// update mapping to have only the H states
			for(State nextState:statesToRemove)
			{
				C_S_j_new.get(nextState.getS()).get(nextState.getJ()).remove(nextState);
				C_S_j_new.get(nextState.getS()).get(nextState.getJ()).remove(nextState);
				C_S_j_new.get(nextState.getS()).get(nextState.getJ()).remove(nextState);
				if(C_S_j_new.get(nextState.getS()).get(nextState.getJ()).size() == 0)
				{
					C_S_j_new.get(nextState.getS()).remove(nextState.getJ());
				}
			}
			
			// remove all states of current stage in order to use for next stage
			states.clear();
			statesSet.clear();

			// clear previous mapping
			C_S_j_prev.clear();
			initialC_S_j.clear();
			
			// set previous mapping to new mapping for next stage
			C_S_j_prev = C_S_j_new;
	
		}
		
		// get length of optimal tour
		
		// final stage states - (V\0,j) for all j
		Map<ArrayListSet,Map<Short,ArrayList<State>>> finalStageSets = C_S_j_prev;
		// final state costs - C(V\0,j) for all j
		
		Map<Short,ArrayList<State>> finalStatesJ = finalStageSets.get(finalStageSets.keySet().iterator().next()); 
		
		
		Short lastEndNode = (short) depotEndNodes.previousSetBit(depotEndNodes.length());
		State finalOptimalState = finalStatesJ.get(lastEndNode).get(0);
		
		// initialize length optimal tour
		Map<ArrayList<Short>, Double[]> optimalTourMap = new HashMap<>();
		
		// optimal tour length
		double optimalTourLength = finalOptimalState.getDistanceTravelled() + distMatrix[lastEndNode][depotBeginNodes.nextSetBit(0)];
		double numberVehiclesUsed = finalOptimalState.getFinalNumVehiclesUsed();
		
		Double[] solution = new Double[2];
		solution[0] = numberVehiclesUsed;
		solution[1] = optimalTourLength;
		
		// initialize optimal tour
		ArrayList<Short> optimalTour = new ArrayList<>();
		optimalTour.addAll(finalOptimalState.getListS());		
		
		// finally add the start location as depot
		optimalTour.add((short) depotBeginNodes.nextSetBit(0)); 
		
		// map optimal tour to its length
		optimalTourMap.put(optimalTour, solution);
		
		C_S_j_prev.clear();
		finalStatesJ.clear();
		// return map of optimal tour and corresponding length
		return optimalTourMap;
	}	
	
	
	
	public ArrayListSet getFeasibleJExpansions(Short i, ArrayListSet currentSubSetToExpand, double currentCapacityVehicle, double currentStateReleaseTime)
	{	
		// get left over customer nodes
		ArrayListSet leftOverCustomers = new ArrayListSet();
		leftOverCustomers.or(customerCities);
		leftOverCustomers.andNot(currentSubSetToExpand);
		
		// get left over origin nodes
		ArrayListSet leftOverBeginNodes = new ArrayListSet();
		leftOverBeginNodes.or(depotBeginNodes);
		leftOverBeginNodes.andNot(currentSubSetToExpand);
		
		// get left over destination nodes
		ArrayListSet leftOverEndNodes = new ArrayListSet();
		leftOverEndNodes.or(depotEndNodes);
		leftOverEndNodes.andNot(currentSubSetToExpand);
		
		ArrayListSet potentialFeasibleJExpansions = new ArrayListSet();
		
		// check first if i is in partial order D and enforce partial order j if so
		boolean partialOrderExists = false;
		boolean partialOrderFeasible = false;
		if(partialCommonOrderD.containsKey(i))
		{
			partialOrderExists = true;
			
			// check if j is a depotEndNode then just use next depot end node
			Short j;
			if(depotEndNodes.get(partialCommonOrderD.get(i)) == true)
			{
				// j is just next depot end node
				j = (short) leftOverEndNodes.nextSetBit(0);
			}
			else // j is a customer (or specific begin node)
			{
				j = partialCommonOrderD.get(i);
			}
			
			ArrayListSet remainingCustomers = new ArrayListSet(leftOverCustomers);
			remainingCustomers.clear(j);
			
			ArrayListSet expandedSet = new ArrayListSet(currentSubSetToExpand);
			expandedSet.set(j);
			
			// check if j not already in visited nodes 
			if(currentSubSetToExpand.get(j) == false)
			{
				if(enoughCapacity(remainingCustomers, expandedSet,currentCapacityVehicle,j) && timeFeasible(remainingCustomers, expandedSet,currentStateReleaseTime, distMatrix[i][j], j))
				{
					if(!(nodes.get(j) instanceof DepotEndNode))			
					{
						potentialFeasibleJExpansions.set(j);
						partialOrderFeasible = true;	
						numFeasPartialOrder++;
					}
				}
			}
		}
		if(!partialOrderExists || !partialOrderFeasible) // remove all partial order j from possible customers to visit if i is not in partial order D
		{
			// check if partial order was infeasible
			if(partialOrderExists)
			{
				// increment number of times partial order wasnt possible to enforce
				numInfeasPartialOrder ++;
			}
			
			// remove all partial order j
			for(short partialOrderI:partialCommonOrderD.keySet())													 																		
			{		
				// if node i of partial order pair has not been included yet
				if(leftOverCustomers.get(partialOrderI) == true)
				{
					// remove node j corresponding to pair with node i													
					leftOverCustomers.clear(partialCommonOrderD.get(partialOrderI));
				}
			}
			
			// find possible j's 
			if(nodes.get(i) instanceof DepotBeginNode)
			{
				if(i==(short) depotBeginNodes.previousSetBit(depotBeginNodes.length()))
				{
					if(leftOverCustomers.isEmpty())
					{
						potentialFeasibleJExpansions.set(leftOverEndNodes.nextSetBit(0));
					}
					else
					{
						potentialFeasibleJExpansions.or(leftOverCustomers);
					}
				}
				else
				{
					potentialFeasibleJExpansions.or(leftOverCustomers);
					potentialFeasibleJExpansions.set(leftOverEndNodes.nextSetBit(0));
				}	
			}
			else if(nodes.get(i) instanceof DepotEndNode)
			{
				potentialFeasibleJExpansions.set(leftOverBeginNodes.nextSetBit(0));
			}
			else if(!(nodes.get(i) instanceof DepotEndNode) && !(nodes.get(i) instanceof DepotBeginNode))
			{	
				if(leftOverBeginNodes.isEmpty())
				{
					if(leftOverCustomers.isEmpty())
					{
						potentialFeasibleJExpansions.set(leftOverEndNodes.nextSetBit(0));
					}
					else
					{
						potentialFeasibleJExpansions.or(leftOverCustomers);
					}
				}
				else
				{
					potentialFeasibleJExpansions.or(leftOverCustomers);
					potentialFeasibleJExpansions.set(leftOverEndNodes.nextSetBit(0));
				}
			}
		}
		
		return potentialFeasibleJExpansions;
	}
	
	public boolean enoughCapacity(ArrayListSet remainingCustomers, ArrayListSet currentSubSet, double currentCapacity, short nodeToAdd)
	{
		// initialize feasible boolean
		boolean isEnoughCapacity = false;

		if(currentCapacity-nodes.get(nodeToAdd).getDemand()>=0)
		{
			isEnoughCapacity = true;
		}

		return isEnoughCapacity;
	}
	
	public boolean timeFeasible(ArrayListSet remainingCustomers, ArrayListSet currentSubSet, double prevStateReleaseTime, double travelTimeToAdd, short nodeToAdd)
	{	
		// get arrival time at node j
		double arrivalTime = prevStateReleaseTime + travelTimeToAdd;  
		
		// initialize feasible boolean
		boolean isTimeFeasible = false;

		if(arrivalTime<= nodes.get(nodeToAdd).getEndTime())
		{
			isTimeFeasible = true;
		}
			
		return isTimeFeasible;
	}
	
	/**
	 * checks whether adding j after i is compatible with the 
	 * partial order D common to both parents which must be enforced 
	 * in this new individual solution
	 * @return - boolean for whether compatible
	 */
	public boolean partialOrderCompatible(short i, short j)
	{
		boolean isCompatible = false;
		
		// constraint on order for i
		if(partialCommonOrderD.containsKey(i))
		{
			// i and j match partial order constraint
			if(partialCommonOrderD.get(i) == j)
			{
				isCompatible = true;
			}
		}
		// no constraint on order for nodes i and j
		else if(!partialCommonOrderD.containsKey(i) && !partialCommonOrderD.containsValue(j))
		{
			isCompatible = true; 
		}
		
		return isCompatible;
	}
	
	public double getCapacityNewState(double currentCapacityVehicle, Node j)
	{
		double newCapacity;
		if(j instanceof DepotEndNode || j instanceof DepotBeginNode)
		{
			// new vehicle so new max capacity
			newCapacity = vehicleCapacity;
		}
		else
		{
			newCapacity = currentCapacityVehicle - j.getDemand();
		}
		
		return newCapacity;
	}
	
	public double getTimeNewState(double prevStateReleaseTime, double travelTimeToAdd, Node nodeToAdd)
	{
		double arrivalTime = prevStateReleaseTime+travelTimeToAdd;
		double newTime;
		if(nodeToAdd instanceof DepotEndNode || nodeToAdd instanceof DepotBeginNode)
		{
			// new vehicle can start at any time from depot
			newTime = 0;
		}
		else
		{
			// make sure wait is included if coming earlier than begin time of window
			newTime = Math.max(nodeToAdd.getBeginTime(),arrivalTime) + nodeToAdd.getServiceTime();
		}
		
		return newTime;	
	}
}
