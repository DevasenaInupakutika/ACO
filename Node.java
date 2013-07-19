/*import Controller;
import GlobalSettings;*/


public class Node {

	private int x;
	private int y;


	//Here instead of re-computing the pheromone everytime, We store precomputed values
	// in an array, and the current pheromone level is the index to this array.
	//We only store upto N+1 levels, ofcourse the highest pher level will be at index=0
	//N should be chosen such that it (0.9)^N+1 tends to zero...
	//It has been found that a value of 49 is reasonable because ==> 0.9^(49+1) = 0.005 ~ 0
	public final static int LOWEST_PHEROMONE_INDEX = GlobalSettings.LOWEST_PHEROMONE_INDEX;
	
	//This is a static computation and does not affect the simulation, but this variable
	// is no longer used... Instead we cud just use the index to know the pheromone level.
	public final static double[] PHEROMONE_LEVELS = new double[LOWEST_PHEROMONE_INDEX+1];
	static {
		//Populate pheromone levels varying from 1 --> 0
		PHEROMONE_LEVELS[0]=1;

		for (int i = 1; i < PHEROMONE_LEVELS.length-1; i++) {
			PHEROMONE_LEVELS[i]=PHEROMONE_LEVELS[i-1]*0.9;
		}
		PHEROMONE_LEVELS[PHEROMONE_LEVELS.length-1]=0;
	}

	//This holds the index of the level. and -1 implies that it is food
	//This is better than storing the double as it saves 4byts per node.
	private int pheromoneIndex;

	//Nodes with food have pheromoneIndex = FOOD_VALUE, generally -1
	private static final int FOOD_VALUE = -1;
	//Nodes with obstacle have pheromoneIndex = OBSTACLD_VALUE, generally -2
	private static final int OBSTACLE_VALUE = -2;

	public Node(int x, int y)
	{
		this.x = x;
		this.y = y;
		//Start with the lowest value of pheromone, 
		//i.e. PHEROMONE_LEVELS[LOWEST_PHEROMONE_INDEX] = 0 
		pheromoneIndex = LOWEST_PHEROMONE_INDEX;
	}

	public int getPheromoneIndex(){
		return pheromoneIndex;
	}

	public void setPheromoneIndex(int index){
		this.pheromoneIndex = index;
	}

	public void evaporatePheromone(int steps){
		
		//For the first time when we are evaporating, we just evaporate by one step
		//Otherwise evaporate by the no of steps. The reason is because in Ant.run(),
		// we exit the loop when we find food and that is when we set the pheromone to 0.
		//But commented why?? -> For improving performance, because generally "steps = 1".
/*		if (pheromoneIndex == 0) {
			pheromoneIndex++;
		} else {*/
			pheromoneIndex = (pheromoneIndex + steps);
			pheromoneIndex = (pheromoneIndex>LOWEST_PHEROMONE_INDEX)?LOWEST_PHEROMONE_INDEX:pheromoneIndex;
		//}
	}
	

	//Returns true, if the controller was updated.
	public boolean evaporateAndUpdateController(int steps, Controller controller){
		//A seperate method to do the evaporation and then we update the controller
		// We cud copy the method here to avoid context switching overhead
		this.evaporatePheromone(steps);
		
		//Tell the controller abt the pheromone only if we have some thing in this node
		//Also there is one importance to this if condition, the moment i remove this 'if',
		// ants stop appearing, dont know why, strange.
		if (pheromoneIndex != LOWEST_PHEROMONE_INDEX) {
			controller.setPheromoneAt(this.x, this.y, pheromoneIndex,LOWEST_PHEROMONE_INDEX);
			return true;
		}
		return false;
	}
	
	public void setIsFood (){
		this.pheromoneIndex = FOOD_VALUE;
	}

	public boolean isFood(){
		return pheromoneIndex == FOOD_VALUE;
	}

	public void setIsObstacle (){
		this.pheromoneIndex = OBSTACLE_VALUE;
	}

	public boolean isObstacle(){
		return pheromoneIndex == OBSTACLE_VALUE;
	}
	
	public int getX()
	{
		return this.x;
	}

	public int getY()
	{
		return this.y;
	}
	
	@Override
	public String toString() {

		return "x = "+x+" y = "+y;
	}	
}
