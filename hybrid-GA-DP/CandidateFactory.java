package Hybrid_GA_DP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



//import classes from restricted DP package
import Restricted_DP.ArrayListSet;
import Restricted_DP.Node;
import Restricted_DP.DepotBeginNode;
import Restricted_DP.DepotEndNode;

public class CandidateFactory<T> 
{
	// list of customer nodes
	private List<Short> customerNodes;
	
	// list of left over customer nodes to use as initial insertion
	private List<Short> possibleInitialNodes;
	
	// distance matrix
	private double[][]  distances;
	
	// node mapping
	private Map<Short,Node> nodeMap;
	
	// depot begin and end nodes
	private ArrayListSet depotBeginNodes;
	private ArrayListSet depotEndNodes;
	
	// insertion parameters [alpha, mu, lambda]
	private double alpha;
	private double mu;
	private double lambda;
	
	// vehicle capacity
	private double vehicleCapacity;
	
	// performance measures map for all individuals
	private Map<T,Double[]> performanceMeasuresMap;
	// perforamance measures for the current inidividual - [numVehicles, distanceTravelled]
	private Double[] performanceMeasuresIndividual; 
	
	/**
	 * Constructor
	 * @param customerNodes
	 * @param distances
	 * @param nodeMap
	 * @param nodeIndexMap
	 * @param depotBeginNodes
	 * @param depotEndNodes
	 */
	public CandidateFactory(ArrayList<Short> customerNodes, double[][] distances, Map<Short,Node> nodeMap, double vehicleCapacity, 
			ArrayListSet depotBeginNodes, ArrayListSet depotEndNodes, Double[] insertionParameters)
	{
		this.customerNodes = customerNodes;
		this.possibleInitialNodes = new ArrayList<>(customerNodes);
		this.distances = distances;
		this.nodeMap = nodeMap;
		this.depotBeginNodes = depotBeginNodes;
		this.depotEndNodes = depotEndNodes;
		this.alpha = insertionParameters[0];
		this.mu = insertionParameters[1];
		this.lambda = insertionParameters[2];
		this.vehicleCapacity = vehicleCapacity;
		this.performanceMeasuresIndividual = new Double[2]; 
		this.performanceMeasuresMap = new HashMap<>();
		}

	/**
	 * Generates an individual for population using the insertion algorithm of Solomon
	 * @param rng
	 * @return
	 */
	public T generateRandomCandidate(Random rng) 
	{	
		// initialize list of left over nodes
		ArrayList<Short> unvisitedCustomers = new ArrayList<Short>(customerNodes);
		
		// depot begin and end node lists
		ArrayListSet depotBeginNodes = new ArrayListSet(this.depotBeginNodes);
		ArrayListSet depotEndNodes = new ArrayListSet(this.depotEndNodes);
		
		// initialize total travel distance
		double distanceTravelled = 0;
		// initialize number of vehicles used
		int numVehicles = 0;
		
		////////////////// intialize first route {o1,ri,d1} ////////////////
		ArrayList<Short> candidate = new ArrayList<>();

		// sum begin times
		double sumBeginTimes = 0;
		for(int i = 0; i < possibleInitialNodes.size(); i++)
		{
			sumBeginTimes += nodeMap.get(possibleInitialNodes.get(i)).getBeginTime();
		}

		// comparator for begin time of customer windows
		BeginTimeComparator earliestBeginComparator = new BeginTimeComparator(nodeMap);
		
		// sort possible initial customer nodes
		Collections.sort(possibleInitialNodes, earliestBeginComparator);
		
		// generate random number between 0 and total sum of begin times
		double rand = rng.nextDouble()*sumBeginTimes;
		
		// select initial customer to insert
		Short initialCustomer = null;
		for(int i = 0; i < possibleInitialNodes.size(); i++)
		{
			Short nextCustomer = possibleInitialNodes.get(i);
			rand -= nodeMap.get(nextCustomer).getBeginTime();
			
			if(rand<0)
			{
				// select current candidate
				initialCustomer = nextCustomer;
				
				// stop loop as initial customer found
				break;
			}
		}
		
		// insert first begin depot node
		candidate.add((short) depotBeginNodes.nextSetBit(0));
		// insert initial customer 
		candidate.add(initialCustomer);
		// insert first end depot node
		candidate.add((short) depotEndNodes.nextSetBit(0));
		
		// update distance travelled
		distanceTravelled += distances[(short) depotBeginNodes.nextSetBit(0)][initialCustomer] + distances[initialCustomer][(short) depotEndNodes.nextSetBit(0)];
		// update number of vehicles used
		numVehicles ++;
		
		// remove route depot nodes from possible routes
		depotBeginNodes.clear(depotBeginNodes.nextSetBit(0));
		depotEndNodes.clear(depotEndNodes.nextSetBit(0));
		
		// remove new initial customer from list of possible initial customers
		possibleInitialNodes.remove(initialCustomer);
		
		// remove initial customer from list of unvisited customers for current candidate
		unvisitedCustomers.remove(initialCustomer);
		//////////////////////////////////////////////////////////////
		
		// begin main algorithm
		
		// initialize remaining vehicle capacity
		double remainingCapacity = vehicleCapacity - nodeMap.get(initialCustomer).getDemand();
		
		// initialize index for start of current route within individual
		int routeStartIndex = 0;
		while(!unvisitedCustomers.isEmpty())
		{
			// initialize List of departure times for current route
			ArrayList<Double> departureTimes = new ArrayList<>();
			departureTimes.add(0.0);
			departureTimes.add(nodeMap.get(initialCustomer).getBeginTime() + nodeMap.get(initialCustomer).getServiceTime());
			
			boolean feasInsertionCurrentRoute = true;
			while(feasInsertionCurrentRoute)
			{
				// initialize next best customer and position
				Short bestCustomer = Short.MIN_VALUE; // use extreme min values for safety
				double bestCustomerValue = Double.NEGATIVE_INFINITY;
				int bestPosition = Integer.MIN_VALUE;
				
				// find next best customer to insert and best position for that customer
				for(Short nextCustomer : unvisitedCustomers)
				{
					Node customer = nodeMap.get(nextCustomer);
					
					// initialize min function value 
					double minPenalty = Double.POSITIVE_INFINITY;
					int minPenaltyPosition = Integer.MIN_VALUE;
					
					// find best position in route for current customer
					for(int i = routeStartIndex; i < candidate.size()-1; i++)
					{ 	
						// left neighbour
						short neighbourL = candidate.get(i);
						short neighbourR = candidate.get(i+1);
						
						// arrival time at right neighbour from left neighbour
						double currentArrivalTimeRNeighbour = departureTimes.get(i-routeStartIndex) + distances[neighbourL][neighbourR];
						// arrival time at inserted customer from left neighbour
						double arrivalTimeNewCustomer = departureTimes.get(i-routeStartIndex) + distances[neighbourL][nextCustomer];
						// arrival time at right neighbour from inserted customer
						double newArrivalTimeRNeighbour = Math.max(arrivalTimeNewCustomer, customer.getBeginTime()) + customer.getServiceTime() + distances[nextCustomer][neighbourR];
						
						if(isFeasibleInsertion(candidate.subList(routeStartIndex, candidate.size()), nextCustomer, neighbourL, remainingCapacity, arrivalTimeNewCustomer))
						{
							// right neighbour node
							Node neighbourRNode = nodeMap.get(neighbourR);
							
							// compute function value for current insert position
							double penalty = alpha*(distances[neighbourL][nextCustomer] + distances[nextCustomer][neighbourR] - mu*distances[neighbourL][neighbourR])
									+ (1 - alpha)*(Math.max(newArrivalTimeRNeighbour,neighbourRNode.getBeginTime()) - Math.max(currentArrivalTimeRNeighbour,neighbourRNode.getBeginTime()));
							
							// update min function value and corresponding insert position
							if(penalty < minPenalty)
							{
								minPenalty = penalty;
								minPenaltyPosition = i + 1;
							}
						}	
					}
					
					double currentCustomerValue = lambda*distances[depotBeginNodes.nextSetBit(0)][nextCustomer] - minPenalty;
					
					// check if best customer so far to insert
					if(currentCustomerValue > bestCustomerValue)
					{
						// update 'best customer to insert' information
						bestCustomer = nextCustomer;
						bestCustomerValue = currentCustomerValue;
						bestPosition = minPenaltyPosition;
					}	
				}
				
				// check if a feasible insertion was found				
				if(!(bestCustomer < 0))
				{
					// insert best customer
					candidate.add(bestPosition, bestCustomer);
					
					// update distance travelled
					distanceTravelled += distances[candidate.get(bestPosition - 1)][bestCustomer] + distances[bestCustomer][candidate.get(bestPosition + 1)] - distances[candidate.get(bestPosition - 1)][candidate.get(bestPosition + 1)]; 
					
					// remove inserted customer from unvisited
					unvisitedCustomers.remove(bestCustomer);
					
					// update departure times
					departureTimes = updateDepartureTimes(departureTimes, routeStartIndex, bestPosition, bestCustomer, candidate);
				}
				else // no feasible insertion - new route
				{
					feasInsertionCurrentRoute = false;
				}	
			}
			
			if(!unvisitedCustomers.isEmpty())
			{
				// update distance travelled - back to depot
				distanceTravelled += distances[candidate.get(candidate.size()-1)][(short) depotBeginNodes.nextSetBit(0)];
				
				// add new initialised route [oi,rp,di]
				candidate.add((short) depotBeginNodes.nextSetBit(0));
				
				Collections.sort(unvisitedCustomers, earliestBeginComparator);
				// add initial customer with earliest begin time										

				initialCustomer = unvisitedCustomers.get(0);
				candidate.add(initialCustomer);
			
				candidate.add((short) depotEndNodes.nextSetBit(0));
				
				// update distance travelled
				distanceTravelled += distances[(short) depotBeginNodes.nextSetBit(0)][initialCustomer] + distances[initialCustomer][(short) depotEndNodes.nextSetBit(0)];
				// update number of vehicles used
				numVehicles ++;
				
				// remove route depot nodes from possible routes
				
				depotBeginNodes.clear(depotBeginNodes.nextSetBit(0));
				depotEndNodes.clear(depotEndNodes.nextSetBit(0));
				
				// update unvisited customers
				unvisitedCustomers.remove(initialCustomer);
				// update index start of new route
				routeStartIndex = candidate.size() - 3;
			}
		}
		
		// add any last empty routes on end
		for(short j = (short) depotBeginNodes.nextSetBit( 0 ); j >= 0; j = (short) depotBeginNodes.nextSetBit( j + 1 ) ) 
		{
			candidate.add(j);
			candidate.add((short) depotEndNodes.nextSetBit(0));
			depotEndNodes.clear(depotEndNodes.nextSetBit(0));
		}
		
		// update distance travelled to complete GTR
		distanceTravelled += distances[candidate.get(candidate.size()-1)][this.depotBeginNodes.nextSetBit(0)];
		
		// add first begin node again at end to complete GTR
		candidate.add((short) this.depotBeginNodes.nextSetBit(0));
		
		// set performance measures for current inidividual
		performanceMeasuresIndividual[0] = (double) numVehicles;
		performanceMeasuresIndividual[1] = distanceTravelled;
		
		// return constructed individual (Tour)
		return (T) candidate;									
	}
	
	
	/*
	 * Checks that the two neighbours are compatible in terms of 
	 * the giant tour representation of a partial solution
	 */
	public boolean isFeasibleInsertion(List<Short> currentRoute, short customerToInsert, 
			short leftNeighbour, double remainingCapacity, double arrivalTimeNewCustomer)
	{
		boolean isFeasible = true;
		
		// customer to insert
		Node customer = nodeMap.get(customerToInsert);
		
		// check if begin time is feasible
		if(!(arrivalTimeNewCustomer <= customer.getEndTime()))
		{
			isFeasible = false;
		}
		
		// check if departure time is feasible if new customer wouldn't be the last node of route
		int indexLeftNeighbour = currentRoute.indexOf(leftNeighbour);
		
		if(indexLeftNeighbour != currentRoute.size()-2)
		{
			// initialize previous arrival time for customers after inserted customer
			double prevArrivalTime = arrivalTimeNewCustomer;
			// intialize begin time of prev customer
			double prevBeginTime = customer.getBeginTime();
			// initialize service time of prev customer
			double prevServiceTime = customer.getServiceTime();
			// initialize prev customer
			short prevCustomer = customerToInsert;
			
			// check feasibility for updated arrival time of each customer after inserted customer
			for(int i = indexLeftNeighbour; i<currentRoute.size() - 1; i++)
			{
				// initialize current customer for checking arrival time
				short currentCustomer = currentRoute.get(i + 1);
				
				// compute new arrival time for current customer
				double newArrivalTimeCurrentCustomer = Math.max(prevArrivalTime, prevBeginTime) + prevServiceTime + distances[prevCustomer][currentCustomer]; 
				
				// check if new arrival time still feasible
				if(!(newArrivalTimeCurrentCustomer <= nodeMap.get(currentCustomer).getEndTime()))
				{
					isFeasible = false;
					
					// insertion not feasible for ALL succeeding customers
					break;
				}
				
				// update prev customer info
				prevArrivalTime = newArrivalTimeCurrentCustomer;
				prevBeginTime = nodeMap.get(currentCustomer).getBeginTime();
				prevServiceTime =  nodeMap.get(currentCustomer).getServiceTime();
				prevCustomer = currentCustomer;		
			}
		}
		
		// check if capacity is feasible
		double newCapacity = remainingCapacity - customer.getDemand();
		if(newCapacity < 0)
		{
			isFeasible = false;
		}
			
		return isFeasible;
	}
	
	/**
	 * method updates departure times of all customers in route, succeeding the newly inserted customer (best customer)
	 * @param oldDepartureTimes
	 * @param bestPosition
	 * @param bestCustomer
	 * @param candidate
	 * @return
	 */
	public ArrayList<Double> updateDepartureTimes(ArrayList<Double> departureTimes, int routeStartIndex, int bestPosition, short bestCustomer, ArrayList<Short> candidate)
	{
		// first add departure time of inserted customer
		double arrivalTimeInsertedCustomer = departureTimes.get(bestPosition - routeStartIndex -1) + distances[candidate.get(bestPosition - 1)][bestCustomer];
		double departureTimeInsertedCustomer = Math.max(arrivalTimeInsertedCustomer, nodeMap.get(candidate.get(bestPosition-1)).getBeginTime()) + nodeMap.get(candidate.get(bestPosition-1)).getServiceTime();
		departureTimes.add((bestPosition - routeStartIndex), departureTimeInsertedCustomer);

		// intialize prev departure time - inserted customer
		double prevDepartureTime = departureTimeInsertedCustomer;
		// initialize prev customer
		short prevCustomer = bestCustomer;
		
		// update departure time for each customer after inserted customer
		for(int i = (bestPosition - routeStartIndex); i<departureTimes.size() - 1; i++)
		{
			// initialize current customer for checking arrival time
			short currentCustomer = candidate.get(i + 1);

			// compute new departure time for current customer
			double newDepartureTimeCurrentCustomer = Math.max(prevDepartureTime + distances[prevCustomer][currentCustomer], nodeMap.get(currentCustomer).getBeginTime()) + nodeMap.get(currentCustomer).getServiceTime(); 

			// update departure time
			departureTimes.set(i+1, newDepartureTimeCurrentCustomer);
			
			// update prev customer info
			prevDepartureTime = newDepartureTimeCurrentCustomer;
			prevCustomer = currentCustomer;		
		}
		
		return departureTimes;
	}
	
	
	/**
	 * generates n (populationSize) number of candidates randomly 
	 * @param populationSize
	 * @param rng
	 * @return
	 */
    public List<T> generateInitialPopulation(int populationSize, Random rng)
    {
        List<T> population = new ArrayList<>(populationSize);
        while (population.size() < populationSize)
        {
        	T newPotentialCandidate = generateRandomCandidate(rng);

    		// add performance measures
    		performanceMeasuresMap.put(newPotentialCandidate, performanceMeasuresIndividual);
    		population.add(newPotentialCandidate);	
        }
        return Collections.unmodifiableList(population);
    }
    
    /**
     * gets performance measures for the initial population to be evaluated
     * @return
     */
    public Map<T,Double[]> getPerformanceMeasures()
    {
    	return performanceMeasuresMap;
    }

}
