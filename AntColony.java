import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/*import Node;
import Path;
import World;*/


public class AntColony implements Observer
{
	protected Ant[]    ants;
	protected World world;
	
	protected int      numAnts;
    protected int      antCounter;
    
    protected int      iterCounter;
    protected int      iterations;

    private double globalBestpathValue;
    private Path globalBestPath;
    private int numAntsStarted;
    
    private int numberOfPaths;

    private static final int ANTS_PER_BATCH = 1;
    
    //This is the one passed from the UI
    protected Controller controller;

    //This is the sparse matrix that sir was talking about...
    protected LinkedList<Node> nodesWithPheromone;
    
    public AntColony(World graph, int nAnts, int nIterations, Controller controller)
    {
        world = graph;
        numAnts = nAnts;
        
        iterations = nIterations;
        globalBestpathValue = -1;
        globalBestPath = null;
        this.controller = controller;
        nodesWithPheromone = new LinkedList<Node>();
    }
    
    public synchronized void start()
    {
        // creates all ants
        ants  = createAnts(world, numAnts);
        
        iterCounter = 0;
        
        // loop for all iterations
        while(iterCounter < iterations)
        {
            //Used for calculating the rate of change of path len
            numberOfPaths=0;
            
        	controller.setStartOfIteration();
        	
            // run an iteration
            iteration();
            try
            {
                wait();
            }
            catch(InterruptedException ex)
            {
                ex.printStackTrace();
            }
            
            // synchronize the access to the graph
            synchronized(world)
            {
                // apply global updating rule
                globalUpdatingRule();
            }

    		if (DebugSettings.PATH_LEN_RATE && numberOfPaths>0) {
    			
    			DebugSettings.writeToFile("Rate of change of path len = "+numberOfPaths);
    		}
            controller.setEndOfIteration();
        }
        DebugSettings.closeFile();
        System.out.println("===================== END OF SIMULATION ======================");
        
    }
    
    private void iteration()
    {
        antCounter = 0;
        
        //increment the iterCounter by 1
        iterCounter++;
        
        numAntsStarted +=ANTS_PER_BATCH;
        if (numAntsStarted > numAnts) {
        	numAntsStarted = numAnts;
        }
        
        //start all the ants
        for(int i = 0; i < numAntsStarted; i++)
        {
            ants[i].start();
        }
    }
    
    
	public int getAnts()
    {
        return ants.length;
    }
    
	public World getGraph()
    {
        return world;
    }
	
    public int getIterations()
    {
        return iterations;
    }
    
    public int getIterationCounter()
    {
        return iterCounter;
    }
    
        
    public synchronized void update(Observable ant, Object obj)
    {
    	Ant a = (Ant)ant;
    	Node n = a.getCurNode();
    	
    	controller.setAntAt(n.getX(), n.getY());
    	
        antCounter++;
        
        if(antCounter == numAntsStarted)
        {          
            notify();       
        }
    }
    
    //find the best global path length
    public double getBestPathValue()
    {    			
        return this.globalBestpathValue;
    }
      
    public boolean done()
    {
        return iterCounter == iterations;
    }

	public void updateBestPath(Path bestPath, double bestPathValue) {
		numberOfPaths++;

		if (globalBestpathValue == -1 || bestPathValue < globalBestpathValue)
		{
			globalBestpathValue = bestPathValue;
			globalBestPath = bestPath;
			System.out.println("************Current Best Path ******** "+ globalBestpathValue);
		}
	}
	
    //find the best global path
    public Path getBestPath(){return globalBestPath;}
          
    //method to create the ants
    protected Ant[] createAnts(World graph, int ants) {
    	Ant[] antArr = new Ant[ants];
    	for (int i = 0; i < antArr.length; i++) {
			antArr[i] = new Ant(graph.getStartNode());
		}
    	numAntsStarted = 0;
    	return antArr;
    }
    
    //method to update the pheromone globally (pheromone evaporation)
    protected void globalUpdatingRule(){
    	
    	//Nothing to do if there is not even a single ant which has found the food
    	if (globalBestpathValue < 0)
    		return;
    	
    	//Keep reinforcing the shortest path, this is a hack for better convergence,
    	//But it cud also be possible in reality, where there are dedicated ants to just follow the 
    	//shortest path trail. Who knows!!
    	if (GlobalSettings.REINFORCE_SHORTEST_PATH)
    		globalBestPath.updatePheromone(world, nodesWithPheromone);
    	

    	//This method was used before storing the sparse matrix, can be used for comparing the performance
    	//world.evaporatePheromone(1, controller);
    	
    	//The above method is no longer used, instead use the folowing
    	//Traverse thru the nodes in the list and evaporate
    	for (Iterator iterator = nodesWithPheromone.iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			//The 1st arg is actually stepsPerIteration, basically evaporation rate
	    	//But giving the actual steps per iter seemed to evaporate very fast
			//False Implies the node has completely evaporated, hence remove it from the list
			if (node.evaporateAndUpdateController(GlobalSettings.EVAPORATION_STEPS_PER_ITERAION, controller) == false)
			{
				iterator.remove();
			}
		}

    }

}
