package Restricted_DP;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Class for testing the restricted DP heuristic on instances for different parameter input
 * @author Christianhollreiser
 *
 */

public class TestingClass 
{
	private PrintWriter pw;
	
	public TestingClass()
	{
		
	}
	
	public void main() throws FileNotFoundException 
	{
		// Table 1
		// max number of expanded states to take to next stage
		int E = 160;
		int H = 100000;
		
		// print writer
		pw = new PrintWriter("Instance Class Solutions E_n H_100000 NEW.txt");
		
		// c1
		runProgramForInstanceSet("C1", 9, E, H);
		// c2 
		runProgramForInstanceSet("C2", 8, E, H);
		// r1
		runProgramForInstanceSet("R1", 12, E, H);
		// r2
	    runProgramForInstanceSet("R2", 11, E, H);
		// rc1
		runProgramForInstanceSet("RC1", 8, E, H);
		// rc2
		runProgramForInstanceSet("RC2", 8, E, H);
		
		//Table 2
				
		ArrayList<Integer> Es = new ArrayList<>();
		Es.add(20);
		Es.add(10);
		Es.add(5);
		
		for(int i = 0; i<Es.size();i++)
		{
			
			E = Es.get(i);
			
			// print writer
			String title = "Instance Class Solutions E_" +  E + " H_100000 NEW.txt";
			pw = new PrintWriter(title);
			
			// c1
			runProgramForInstanceSet("C1", 9, E, H);
			// c2 
			runProgramForInstanceSet("C2", 8, E, H);
			// r1
			runProgramForInstanceSet("R1", 12, E, H);
			// r2
		    runProgramForInstanceSet("R2", 11, E, H);
			// rc1
			runProgramForInstanceSet("RC1", 8, E, H);
			// rc2
			runProgramForInstanceSet("RC2", 8, E, H);	
		}
				
		// Table 3
		 E = 5;
		 H = 1000000;
		
		// print writer
		String title = "Instance Class Solutions E_5 H_1mil NEW.txt";
		pw = new PrintWriter(title);
		
		// c1
		runProgramForInstanceSet("C1", 9, E, H);
		// c2 
		runProgramForInstanceSet("C2", 8, E, H);
		// r1
		runProgramForInstanceSet("R1", 12, E, H);
		// r2
	    runProgramForInstanceSet("R2", 11, E, H);
		// rc1
		runProgramForInstanceSet("RC1", 8, E, H);
		// rc2
		runProgramForInstanceSet("RC2", 8, E, H);
	}
	
	
	public void runProgramForInstanceSet(String instanceSet, int numInstances, int E, int H)
	{
		// number of vehicles
		int numVehicles = 16;
		
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
			String file = instanceSet + string + i + ".txt";  //CHANGE BACK to ".txt"
			// get file
			File f = new File(file);
			
			long starttime = System.currentTimeMillis();
			
			// run algorithm with file and specified max (initial) number of vehicles
			Main run = new Main((short) numVehicles,f,H,E); 
			run.runAlgorithms();
			
			long endtime = System.currentTimeMillis();
			
			// solution array
			Double[] solution = run.getSolution().values().iterator().next();
			ArrayList<Short> optimalTour = run.getSolution().keySet().iterator().next(); 
			
			pw.print("Instance: " + instanceSet+ string +i);
			pw.print(" " + solution[0]);
			pw.print(" " + solution[1]);
			pw.println(" " + (endtime - starttime)/1000);
			pw.flush();
		}
	}

}
