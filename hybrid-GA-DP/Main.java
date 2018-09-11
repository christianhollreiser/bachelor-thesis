package Hybrid_GA_DP;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Restricted_DP.ArrayListSet;
import Restricted_DP.DepotBeginNode;
import Restricted_DP.DepotEndNode;
import Restricted_DP.Input;
import Restricted_DP.Node;
import Restricted_DP.RestrictedDPAlgorithmGA;

/**
 * This class prepares all the necessary input and elements for the hybrid GA-DP algorithm
 * Also runs the algorithm
 * @author Christianhollreiser
 *
 */
public class Main 
{
	// distances matrix
	private double[][] distances;
	
	// all nodes
	private List<Short> nodes;
	
	// node mapping
	private Map<Short,Node> nodeMap;
	
	// depot begin nodes
	private ArrayListSet depotBeginNodes; 
	private ArrayListSet depotEndNodes;
	
	// customer nodes
	private ArrayList<Short> customerNodes;
	
	// vehicle capacity
	private double vehicleCapacity;
	
	/**
	 * Constructor
	 */
	public Main()
	{
		
	}
	
	/**
	 * method to run the GADP algorithm
	 * @param instance
	 * @param H
	 * @param E
	 * @param numVehicles
	 * @param popSize
	 * @param numMatings
	 * @param numGenerations
	 * @param numElite
	 * @param pM
	 * @param randomSeed
	 * @return
	 */
	public Candidate<ArrayList<Short>> runGADP(File instance, int H, int E, short numVehicles, int popSize, int numMatings, int numGenerations, int numElite, double pM, int randomSeed)
	{
		
		//////////////////////////////////// Input arguments for restricted DP ////////////////////////////////////////////
		
		// name of instance file
		// String file = "R102.txt";
		
		// get instance file
		//File instance = new File(file);
		
		// restriction parameters
		//int H = 1000;
		//int E = 160;
		
		// number of vehicles
		//short numVehicles = 30;
		
		// initialize restricted DP object
		RestrictedDPAlgorithmGA restrictedDP = initializeDP(instance, numVehicles, H, E);
		
		//////////////////////////////////// Input arguments for evolution engine /////////////////////////////////////////
		
		//////////////////////// parameters for evolution engine ///////////////////
		
		// parameters for insertion heuristic for initialization
		Double[] insertionParameters = new Double[3];
		insertionParameters[0] = 1.0; // alpha
		insertionParameters[1] = 1.0; // mu
		insertionParameters[2] = 2.0; // lambda
		
		// population size
		int populationSize = popSize;
		
		// number of crossovers/matings
		int numberOfMatings = numMatings;
		
		// probability to remove arc from common order D
		double mutationProbability = pM;	
		
		// elite count - number of individuals to keep from previous generation
		int eliteCount = numElite;
		
		// number of generations
		int numberOfGenerations = numGenerations;
		
		// seed for rng
		int seed = randomSeed;
		
		// lambda for fitness function - TODO: NOT USING ATM 
		double lambda = 0.5;
		///////////////////////////////////////////////////////////////////////////
		
		/////////////////////// methods for evolution engine //////////////////////
		
		// (1) candidate factory for initial solution
		CandidateFactory<ArrayList<Short>> candidateFactory = new CandidateFactory<>(customerNodes, distances, nodeMap, vehicleCapacity, depotBeginNodes, depotEndNodes, insertionParameters); //TODO: CREATE AND ADD THE NEEDED ARGUMENTS
		
		// (2) evolution operators 
		
		// - initialize list of evolutionary operators
		List<EvolutionaryOperator<ArrayList<Short>, Short>> operators = new LinkedList<>();
		
		// - add crossover operator
		EvolutionaryOperator<ArrayList<Short>, Short> crossover = new Crossover();
		operators.add(crossover);
		
		// - add partial order D mutation operator																						
		EvolutionaryOperator<ArrayList<Short>, Short> mutateD = new MutatePartialOrderD_P(mutationProbability);
		operators.add(mutateD);
		
		// - add DP operator
		EvolutionaryOperator<ArrayList<Short>, Short> crossoverDP = new CrossoverDP<>(restrictedDP);
		operators.add(crossoverDP);
		
		// - pass operators to evolution pipeline operator to handle mutation then crossover by DP
		EvolutionPipeline<ArrayList<Short>,Short> pipeline = new EvolutionPipeline<>(operators, numberOfMatings);			
		
		// (4) - selection procedure TODO: EITHER: use this selection procedure as a separate step after all new candidates generated OR: do as in paper and actively select during crossover procedure 
		SelectionStrategy<ArrayList<Short>> selectionProcedure = new SelectionStrategy<>();
		
		// (5) - random number generator - we use the Mersenne Twister due to its speed and statistical soundness
		Random rng = new  Random(seed);
		//////////////////////////////////////////////////////////////////////////////

		// initialize GA-DP engine
		EvolutionEngine<ArrayList<Short>,Short> engine = new EvolutionEngine<>(candidateFactory, pipeline, selectionProcedure,rng); 
	
		// evolve population for specified number of generations and obtain final evolved population
		List<Candidate<ArrayList<Short>>> finalGeneration = engine.evolvePopulation(populationSize, eliteCount, numberOfGenerations);
		
		// sort final population on fitness and select best
		Collections.sort(finalGeneration);
		Candidate<ArrayList<Short>> bestIndividual = finalGeneration.get(0);
		
		return bestIndividual;
		
		// print performance measures
		//System.out.println("Num Vehicles: " + bestIndividual.getNumVehicles());
		//System.out.println("Distance Travelled: " + bestIndividual.getDist());
		
	
	}
	
	/**
	 * intializes object for restricted DP algorithm with specified parameters
	 * @param f
	 * @param numV
	 * @param H
	 * @param E
	 * @return
	 */
	public RestrictedDPAlgorithmGA initializeDP(File f, short numV, int H, int E)
	{
		// File f = new File("C101.txt");
		Input input = new Input(f,numV, (short) 101);  
		
		ArrayListSet cities = input.getCityNodes();
		customerNodes = input.getCustomers();
		depotBeginNodes = input.getDepotBeginNodes();
		depotEndNodes = input.getDepotEndNodes();
		
		// get node mapping
		nodeMap = input.getNodeMapping();
		
		// get distance matrix
		distances = input.getDistanceMatrix();
		
		// make list of nodes
		nodes = new ArrayList<>(nodeMap.keySet());
		
		cities.or(depotBeginNodes);
		cities.or(depotEndNodes);
	
		vehicleCapacity = input.getVehicleCapacity();
		

		// set pre and successor for end depot nodes
		for(short i = (short) depotEndNodes.nextSetBit( 0 ); i >= 0; i = (short) depotEndNodes.nextSetBit( i + 1 )) 
		{
			DepotBeginNode pred = (DepotBeginNode) nodeMap.get(i-numV);
			
			// successor
			DepotBeginNode suc;
			if(i==depotEndNodes.previousSetBit(depotEndNodes.length()))
			{
				suc = (DepotBeginNode) nodeMap.get(depotBeginNodes.nextSetBit(0)); // first begin depot
			}
			else
			{
				suc = (DepotBeginNode) nodeMap.get(i+1-numV);
			}
			
			// set pre and successor node
			((DepotEndNode) nodeMap.get(i)).setPreDepot(pred);
			((DepotEndNode) nodeMap.get(i)).setSucDepot(suc);
		}
		
		// set pre and successor for begin depot nodes
		for(short i = (short) depotBeginNodes.nextSetBit( 0 ); i >= 0; i = (short) depotBeginNodes.nextSetBit( i + 1 )) 
		{
			// predecessor 
			DepotEndNode pred;
			if(i== depotBeginNodes.nextSetBit(0))
			{
				int index = depotEndNodes.previousSetBit(depotEndNodes.length());
				pred = (DepotEndNode) nodeMap.get(index);
			}
			else
			{
				int index = i-1;
				pred = (DepotEndNode) nodeMap.get(index + numV);
			}
			
			// successor
			DepotEndNode suc = (DepotEndNode) nodeMap.get(i + numV);
			
			// set pre and successor node
			((DepotBeginNode) nodeMap.get(i)).setPreDepot(pred);
			((DepotBeginNode) nodeMap.get(i)).setSucDepot(suc);
		}
		
		
		RestrictedDPAlgorithmGA restrictedDP = new RestrictedDPAlgorithmGA(nodeMap, cities,depotBeginNodes,depotEndNodes, H , E, vehicleCapacity, distances);
		
		return restrictedDP;
	}
}
