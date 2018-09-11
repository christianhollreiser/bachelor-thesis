package Hybrid_GA_DP;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
/**
 * Class for testing the GA-DP heuristic on instances for different parameter input
 * @author Christianhollreiser
 *
 */

public class TestingClassGADP
{
	private PrintWriter pw;
	
	public TestingClassGADP()
	{
		
	}
	
	public void main() throws FileNotFoundException 
	{
		// file name for solutions
		String title = "Instance Class Solutions GA-DP pop_15 mate_20 gen_25 H_1000 E_5 pM_0.05.txt";	
			
		// print writer to print solutions
		pw = new PrintWriter(title);

		// Benchmark Instances
		
		// r2
	    runProgramForInstanceSet("R2", 11);
		// rc2
		runProgramForInstanceSet("RC2", 8);
		// r1 
		runProgramForInstanceSet("R1", 12);
		// c2 
		runProgramForInstanceSet("C2", 8);
		// c1
		runProgramForInstanceSet("C1", 9);
		// rc1
		 runProgramForInstanceSet("RC1", 8); 
		
	}
	
	
	public void runProgramForInstanceSet(String instanceSet, int numInstances)
	{
		////////// parameters /////////
		int H = 1000;
		int E = 10;
		short numVehicles = 30;
		int popSize = 15;
		int numMatings= 20;
		int numGenerations = 25;
		int numElite = 0;
		double pM = 0.05;
		int randomSeed = 12345;
		///////////////////////////////
		
		for(int i = 1; i<=numInstances; i++)
		{
			String string;
			if(i<10)
			{
				string = "0";
			}
			else
			{
				string = "";
			}
			// define which file/instance to use
			String file = instanceSet + string + i + ".txt";  
			// get file
			File f = new File(file);
			
			long starttime = System.currentTimeMillis();
			
			// run algorithm with file and specified max (initial) number of vehicles
			Main GADPalgorithm = new Main(); 
			Candidate<ArrayList<Short>> solution = GADPalgorithm.runGADP(f, H, E, numVehicles, popSize, numMatings, numGenerations, numElite, pM, randomSeed);
	
			long endtime = System.currentTimeMillis();
			
			System.out.println("Instance: " + instanceSet+ string + i +  " " + solution.getNumVehicles() + " " + solution.getDist() + " " + (endtime - starttime)/1000);
			
			pw.print("Instance: " + instanceSet+ string +i);
			pw.print(" " + solution.getNumVehicles());
			pw.print(" " + solution.getDist());
			pw.println(" " + (endtime - starttime)/1000);
			pw.flush();
		}

	}
}
