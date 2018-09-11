package Hybrid_GA_DP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class EvolutionEngine<T,E>
{
	private final EvolutionPipeline<T,E> evolutionScheme;
    private final SelectionStrategy<T> selectionStrategy;
    private final CandidateFactory<T> candidateFactory;
    private final Random rng;
    
    // performance measures of the current generation
    private Map<T,Double[]> currentGenerationPM;

    /**
     * Constructor
     * @param candidateFactory
     * @param evolutionScheme
     * @param fitnessEvaluator
     * @param selectionStrategy
     * @param rng
     */
    public EvolutionEngine(CandidateFactory<T> candidateFactory, EvolutionPipeline<T,E> evolutionScheme, SelectionStrategy<T> selectionStrategy, Random rng)
    {
		this.evolutionScheme = evolutionScheme;
		this.selectionStrategy = selectionStrategy;
		this.candidateFactory = candidateFactory;
		this.rng = rng;
	}

    
    /**
     * 
     * @param populationSize
     * @param eliteCount
     * @param numberOfGenerations
     * @return
     */
    public List<Candidate<T>> evolvePopulation(int populationSize, int eliteCount, int numberOfGenerations)
    {
       
    	// initialize current generation number 
        int currentGenerationNum = 0;

        // get initial population
        List<T> population = candidateFactory.generateInitialPopulation(populationSize, rng);
        
        // set initial generation performance measures
        currentGenerationPM = candidateFactory.getPerformanceMeasures();

        while (currentGenerationNum<numberOfGenerations)												
        { 
        	// print generation
        	System.out.println("Generation: " + (currentGenerationNum + 1));
        	
        	// evolve population
            population = nextEvolutionStep(population, eliteCount, rng);

	        // update current generation number
	        currentGenerationNum++; 
        }

        // evaluate final population
        List<Candidate<T>> finalEvaluatedPopulation = evaluatePopulation(currentGenerationPM, population); 
        
        // return final population
        return finalEvaluatedPopulation;
    }


    /**
     * evaluates each individual in the given population 
     * returning each individual as a candidate object holding the fitness and individual (tour)  
     * @param population - given population
     * @return - returns candidate population
     */
    public List<Candidate<T>> evaluatePopulation(Map<T,Double[]> performanceMeasures, List<T> population)
    {
        List<Candidate<T>> evaluatedPopulation = new ArrayList<Candidate<T>>(population.size());

        // add candidates to candidate population
        for (T individual : population)
        {
        	// get performance measures for candidate
        	short numVehiclesIndividual = (short) ((double) performanceMeasures.get(individual)[0]);
        	double totalDistanceIndividual = performanceMeasures.get(individual)[1];
        	
        	// create candidate
        	Candidate<T> newCandidate = new Candidate<>(individual, totalDistanceIndividual, numVehiclesIndividual);
        	
        	evaluatedPopulation.add(newCandidate); 
        }
        
        // comparator for evaluation
     	EvaluationComparator<T> evalComparator = new EvaluationComparator<>();
     		
        // evaluate candidate population
        for (Candidate<T> candidate : evaluatedPopulation)
        {
        	// evaluate fitness of candidate
        	candidate.evaluate(evaluatedPopulation, evalComparator);
        }
        
        
        
        return evaluatedPopulation;
    }
    
    
    /**
     * performs an evolution step to obtain new generation
     * @param evaluatedPopulation
     * @param eliteCount
     * @param rng
     * @return
     */
    public List<T> nextEvolutionStep(List<T> population, int eliteCount, Random rng)
    {	
    	// initialize new evolved selected population
		List<T> selectedEvolvedPopulation = new ArrayList<>(population.size());
		
		// evolve the population
		List<T> evolvedPopulation = evolutionScheme.applyOperators(evaluatePopulation(currentGenerationPM, population), rng);
		
		// get performance measures of evolved population
		currentGenerationPM = evolutionScheme.getEvolvedPopPM();

		// evaluate population
		List<Candidate<T>> evaluatedPopulation = evaluatePopulation(currentGenerationPM, evolvedPopulation);
		
		Collections.sort(evaluatedPopulation);
		
		// perform possible elitist selection.
		List<T> elite = new ArrayList<>(eliteCount);
		Iterator<Candidate<T>> iterator = evaluatedPopulation.iterator();			
		while (elite.size() < eliteCount)
		{
			elite.add(iterator.next().getTour());
		}
		
		// select the individuals to form the population that will be evolved to create the next generation
		selectedEvolvedPopulation.addAll(selectionStrategy.select(evaluatedPopulation, population.size() - eliteCount, rng));
		
		
		// add any elite individuals to the evolved population.
		selectedEvolvedPopulation.addAll(elite);
		
		return selectedEvolvedPopulation;
    }

}
