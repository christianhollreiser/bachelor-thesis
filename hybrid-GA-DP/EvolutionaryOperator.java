package Hybrid_GA_DP;
import java.util.List;
import java.util.Map;
import java.util.Random;


public interface EvolutionaryOperator<T,E>
{
	/**
	 * This is a general method for DP evolutionary operator
	 * @param partialCommonOrderD
	 * @param parents
	 * @param rng
	 * @return
	 */
	public T applyDP(Map<E,E> partialCommonOrderD, List<T> parents);
	
	
	/**
	 * This method is for the crossover operator to create 
	 * partial order D common to the parents 
	 * @param parents
	 * @param rng
	 * @return
	 */
	public Map<E,E> apply(List<T> parents, Random rng);
	
	/**
	 * This is general method for mutate operator
	 * @param partialCommonOrderD
	 * @param parents
	 * @param rng
	 * @param numberOfNodes
	 * @return
	 */
	public Map<E,E> apply(Map<E,E> partialCommonOrderD, List<T> parents, Random rng);
}
