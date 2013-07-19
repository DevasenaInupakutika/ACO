
public class GlobalSettings {

	/* =========================== World related parameters ========================= */
	public static  int WORLD_WIDTH = 600;
	public static  int WORLD_HEIGHT = 600;
	//The location and the size of the food
	public static  int FOOD_X = 500;
	public static  int FOOD_Y = 250;
	public static  int FOOD_WIDTH = 25;
	public static  int FOOD_HEIGHT = 25;
	
	/*========================= Ant colony related parameters ========================*/
	public static  int TOTAL_ANTS = 200;
	//This parameter indicates how long to run the simulation before exiting
	public static  int TOTAL_ITERATIONS = 50000;
	
	//Implies the observer will keep updating the global shortest path with pheromone after 
	// every iteration, irrespective of whether an ant has followed or not. This helps ants to find
	// better paths, although there is an overhead of storing these nodes in the sparse matrix.
	// See AntColony.globalUpdatingRule
	public static boolean REINFORCE_SHORTEST_PATH = true;

	//This is basically the evaporation rate, and ideally it should be = STEPS_PER_ITER
	//But doing so, may result in very fast evaporation of the pheromone and might confuse the poor ants
	public static int EVAPORATION_STEPS_PER_ITERAION = 2;
	
	/*================================= Ant related parameters =======================*/
	
	//Choice-1: This is the tow 3 (N,NE,E) dirs and each dir having max_dir proablities
    //Choice-2: enables ants to select nodes more randomly, ie no particular direction is give more weightage.
    //Allowed values right now are 1,2. Default is 1
    public static int STATE_TRANSITION_RULE_NUMBER = 1;
	
	//This is the number of steps the ants take before reporting to the observer
	//The higher the value, the faster will be the simulation.
	public static  int STEPS_PER_ITERATION = 2;
	
	//There are 3 possible directions an ant can take, and in each of these
	//it can have max_dir possile directins. 9 has been found to be a reasonable value
	//Basically each ant will randomly choose a one of the 3 directions (N, NE,E) and
	// it can have max_dir different probablities within this direction.
	public static  int ANTS_MAX_DIR = 9; //Suggestion : do not change this.
	
   	//Here this is the minimum probability of selecting the node in in the curDir direction.
	//Again 0.4 has been found to be a reasonable value for this
	public static  double PROB_CUR_DIR_LOWER = 0.4; //Suggestion : do not change this.
	//This is the maximum probablity of selecting the node, Hence the probability of selecting 
	// cur direction varies from lower to upper value, generally between lower and (lower + higher)
	public static  double PROB_CUR_DIR_HIGHER = 0.3; //Suggestion : do not change this.
	
	//This is the probability of selecting a node with pheromone vs randomly selecting a node
	//Higher value of this means the ants just keep following the trail and dont find new paths
	//Very low value implies the ants keep wandering very randomly, hence 0.7 has bee found to give better results
	public static  double PROB_PHEROMONE_NODE = 0.9;

	/*=========================== Node related parameters ===============================*/
	//Here instead of re-computing the pheromone everytime, We store precomputed values
	// in an array, and the current pheromone level is the index to this array.
	//We only store upto N+1 levels, ofcourse the highest pher level will be at index=0
	//N should be chosen such that, (0.9)^N+1 tends to zero...
	//It has been found that a value of 49 if reasonable because ==> 0.9^(49+1) =0.005
	//Note: This also impacts the rate of evaporation. And more the spi lower will be the rate
	public static  int LOWEST_PHEROMONE_INDEX = 99;
	
}
