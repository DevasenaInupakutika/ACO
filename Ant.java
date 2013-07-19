import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/*import Edge;
import Node;
import Path;
import World;*/

public class Ant extends Observable implements Runnable
{
	private int 		antID;    
	private Node 		curNode;
	private Node 		startNode;
	private Path 		bestPath;
	private Path 		curPath;
	private double		bestPathValue;
	
	private boolean foundFood;
	
	private Random pseudoRandom;
	
	//This specifies which direction the ant prefers more, it can be any of maxDir *3 possibilities
	//The 3 in the formula represents the North, East or the NE node.
	private int curDir;
	
	// This should be multiplied by 3 because we have 3 possible dirs
	//and we want to have maxDir probablities for each of the 3, hence maxDir * 3
	//More the directions more the nodes the ants will cover, but need to have more ants
	private static int maxDir = GlobalSettings.ANTS_MAX_DIR;
	
	//These are common to all ants, hence static.
	protected static int stepsPerIteration;
	protected static AntColony antColony;

	//Required to auto generate antID
    private static int antCounter = 0;
    
    //Store the boundary x, y for faster computation
    private static int maxX, maxY;
    
    private static World graph;
    
    //Choice-2: enables ants to select nodes more randomly, ie no particular direction is give more weightage.
    //Allowed values right now are 1,2. Default is 1
    private static int STATE_TRANSITION_RULE_NUMBER = GlobalSettings.STATE_TRANSITION_RULE_NUMBER;
    
    //This is used to avoid loops in case of random transition rule
    private Node prevNode;

    //Used in Rule_3
    private boolean isReverse;

    //Used in Rule_4
    private char movingDir='E', lookFor='O';//implies look for obstacle in East
    
    private void construct (Node nStartNode){
        antCounter++;
        antID    = antCounter;
        startNode = nStartNode;
        curNode = startNode;
        
        foundFood = false;
        bestPath = null;
        curPath = new Path();
        bestPathValue = -1;
        
        pseudoRandom = new Random();
        prevNode = null;
        isReverse = false;
        changeDir();
    }
    
    private void changeDir(){
        double d = pseudoRandom.nextDouble();
        
        curDir = (int)(d*maxDir*3.0);
    }
    
    //This constructor is to automatically decide the state
    public Ant(Node nStartNode) {
    	construct(nStartNode);
    }
    
    public static void initialize(AntColony ac, int stepsPerIter) {
    	Ant.antColony = ac;
    	Ant.stepsPerIteration = stepsPerIter;
    	graph = antColony.getGraph();
    	maxX = graph.getXWidth()-1;
    	maxY = graph.getYWidth()-1;
    }
    
    public void start()
    {
        Thread thread = new Thread(this);
        thread.setName("Ant " + antID);
        thread.start();
    }

    public  Path getBestPath()
    {
    	return this.bestPath;
    }
    
    public double getBestPathLength()
    {
    	return this.bestPathValue;
    }
    
    //the functioning of an ant
    public void run()
    {
    	
    	int stepCounter = 0;
    	
        // repeat till the no. of steps per iter is complete
        while(stepCounter < stepsPerIteration && !foundFood)
        {
            Node newNode;
            
            // synchronize the access to the ant graph
            synchronized(graph)
            {
                // apply the State Transition Rule
                newNode = stateTransitionRule(curNode);
            }
                        
            synchronized(graph)
            {
            	//update the curPath
                updateCurPath(curNode, newNode);
            }
            
            // update the current node
            curNode = newNode;
            stepCounter++;
        }
        synchronized (antColony) {

        	if (foundFood) {
        		antColony.updateBestPath(bestPath, bestPathValue);
        		foundFood = false;
        	}
        	// update the observer
        	antColony.update(this, null);
        }
    }
    
    //This is the tow three (N,NE,E) dirs and each dir having max_dir proablities
    private Node stateTransitionRule_1(Node n){
    	//First need to do boundary condition
    	if (n.getX() >= maxX || n.getY() >= maxY) {
    		//We set the ant to start node and 
    		//change the direction when we hit the boundary or find food
    		changeDir();
    		return startNode;
    	}


    	Node[] neighbours;
    	neighbours = getAllneighbours(n);

    	//Do found food condition    	
    	for (int i = 0; i < neighbours.length; i++) {
    		if (neighbours[i].isFood()) {
    			changeDir();
    			//Set found food
    			this.setFoundFood(true);			
    			//Again set the ant to the start node
    			return startNode;
    		}
    	}
    	Node randNode = exploreTransitionRule(neighbours);

    	return followTrailTransitionRule(randNode, neighbours);

    }
    
    //This is the one with Random transition rule where we select 5 neighbours, and 
    //upon hitting boundary we reset the x to zero but not y
    private Node stateTransitionRule_2(Node n){


    	//First need to do boundary condition
    	if (n.getX() >= maxX || n.getY() >= maxY) {
    		//Crossed the bottom of the screen, hence go back to start
    		if (n.getY() >=  maxY) {
    			return startNode;
    		} else {// just sweep the world along the x again
    			return graph.getNode(0, n.getY());
    		}
    	}


    	Node[] neighbours;
    	neighbours = get5Neighbours(n);

    	//Do found food condition    	
    	for (int i = 0; i < neighbours.length; i++) {
    		if (neighbours[i].isFood()) {
    			//Set found food
    			this.setFoundFood(true);			
    			//Again set the ant to the start node
    			return startNode;
    		}
    	}
    	
    	Node randNode = randomTransitionRule(neighbours);

    	return followTrailTransitionRule(randNode,neighbours);
    }

    //This is similar to rule_2 but instead of setting x to 0 after hitting the boundary,
    // We go in reverse
    private Node stateTransitionRule_3(Node n){


    	//First need to do boundary condition
    	if (n.getY() >= maxY) {//Crossed the bottom of the screen, hence go back to start    		
    			return startNode;
    	} else if (n.getX() >= maxX){
    		isReverse = true;
    	} else if (n.getX() <= 0) {
    		isReverse = false;
    	}


    	Node[] neighbours;
    	if (isReverse)
    		neighbours = get5NeighboursReverse(n);
    	else
    		neighbours = get5Neighbours(n);

    	//Do found food condition    	
    	for (int i = 0; i < neighbours.length; i++) {
    		if (neighbours[i].isFood()) {
    			//Set found food
    			this.setFoundFood(true);			
    			//Again set the ant to the start node
    			return startNode;
    		}
    	}
    	
    	Node randNode = randomTransitionRule(neighbours);

    	return followTrailTransitionRule(randNode,neighbours);
    }
    private Node getNeighbour(int x, int y,char dir) {
    	switch (dir) {
		case 'E':
			return graph.getNode(x+1, y);
		case 'N':
			return graph.getNode(x, y-1);
		case 'S':
			return graph.getNode(x, y+1);
		default:
			break;
		}
    	return null;
    }
    
    private char[] chooseNorthOrSouth(){
    	char arr[] = new char[2];
    	if (pseudoRandom.nextDouble()<0.5) {
    		arr[0] = 'N';
    		arr[1] = 'S';
    	} else {
    		arr[0] =  'S';
    		arr[1] = 'N';
    	}
    	return arr;
    }
    
    //This one is different, in this ants move straight till they encounter a obstacle.
    private Node stateTransitionRule_4(Node n){
    	
    	int x = n.getX(); int y = n.getY();
    	Node randNode;
    	//First need to do boundary condition
    	if (x >= maxX || y >= maxY) {
    		return startNode;
    	}
    	
    	switch (lookFor) {
		case 'O':
	    	//Get the Moving dir node
	    	randNode = getNeighbour(x, y, movingDir);
	    	if (randNode.isObstacle()){
	    		char arr[] = chooseNorthOrSouth();
	    		lookFor = 'E';
	    		randNode = getNeighbour(x,y,arr[0]);
	    		movingDir = arr[0];
	    		
	    		Node nodeArr[] = {randNode,getNeighbour(x,y,arr[1])};
	    		//choose the one with most pheromone
	    		Node followNode = followTrailTransitionRule(randNode, nodeArr);
	    		if (followNode != randNode) {
	    			movingDir = arr[1];
	    			randNode = followNode;
	    		}
	    	}	
	    	break;
	    	
		case 'E':
			randNode = getNeighbour(x, y, lookFor);
			if (!randNode.isObstacle()) {
				lookFor = (movingDir=='N')?'S':'N';
				movingDir = 'E';
			}
			randNode = getNeighbour(x,y,movingDir);
			break;
		case 'N':
		case 'S':
			randNode = getNeighbour(x, y, lookFor);
			if (!randNode.isObstacle()) {
				movingDir = lookFor;
				lookFor = 'Y';
			}
			randNode = getNeighbour(x,y,movingDir);
			break;
		case 'Y':
			if (y == startNode.getY()) {
				movingDir = 'E';
				lookFor = 'O';
			}
			randNode = getNeighbour(x,y,movingDir);
			break;
		default:
			System.out.println("Error in Rule 4");
			randNode = getNeighbour(x,y,movingDir);
			break;
		}
    	
    	if (randNode.isFood()) {
    		this.setFoundFood(true);			
    		movingDir = 'E';lookFor = 'O';
			return startNode;
    	}
    	return randNode;
    }
    //Given a node, find the possible next nodes and choose one among them based on some rule
    public Node stateTransitionRule(Node n) {

    	//The default rule is 1
    	switch (Ant.STATE_TRANSITION_RULE_NUMBER) {
		case 1:
			return stateTransitionRule_1(n);
		case 2:
			return stateTransitionRule_2(n);
		case 3:
			return stateTransitionRule_3(n);
		case 4:
			return stateTransitionRule_4(n);
		default:
			return stateTransitionRule_1(n);
		}
    }
    
    private Node randomTransitionRule(Node[] neighbours) {
    	double d;
    	int index=0;
    	while (true) {
	    	d = pseudoRandom.nextDouble();
	    	//Here we know the length of neighbours is 5 ie N,NE,E,S,SE nodes
	    	index = (int)(d*5.0);
	    	if (neighbours[index] != prevNode) {
	    		prevNode = neighbours[index];
	    		return neighbours[index];
	    	}
	    	
    	}
    }
    
    private Node exploreTransitionRule(Node[] neighbours) {
    	
    	//Here we explore only based on probability
    	double d = pseudoRandom.nextDouble();
    	
    	int range = maxDir;
    	
    	/*Here p1 is the probability of selecting the node in in the curDir direction.
    	 * So for each of the 3 directions the curDir%range will be from 0 - range,
    	 * Hence the probability of selecting cur direction varies from 0.4 to 0.9
    	 */
    	double p1 = GlobalSettings.PROB_CUR_DIR_LOWER + (curDir%range +  1)*(GlobalSettings.PROB_CUR_DIR_LOWER/(range));
//    	System.out.println(p1);

    	//curDir is any value between 0 and range * 3
    	int index = (int)(curDir/range);

    	if (d < p1) {
    		return neighbours[index];
    	} else if (d < (1 + p1)/2){
    		return neighbours[(index+1)%3];
    	} else {
    		return neighbours[(index+2)%3];
    	}    		
    }

    private Node[] get5Neighbours(Node n){
    	Node[] neighbours = new Node[5];
    	int x = n.getX(), y = n.getY();
    	
    	neighbours[0]=graph.getNode(x+1, y);
    	neighbours[1]=graph.getNode(x, y+1);
    	neighbours[2]=graph.getNode(x+1, y+1);
    	if (y>0) {
    		neighbours[3]=graph.getNode(x, y-1);
    		neighbours[4]=graph.getNode(x+1, y-1);
    	}else {
    		neighbours[3]=graph.getNode(x, y+1);
    		neighbours[4]=graph.getNode(x+1, y+1);
    	}
    	
    	
    	return neighbours;
    }

    private Node[] get5NeighboursReverse(Node n){
    	Node[] neighbours = new Node[5];
    	int x = n.getX(), y = n.getY();
    	
    	neighbours[0]=graph.getNode(x-1, y);
    	neighbours[1]=graph.getNode(x, y+1);
    	neighbours[2]=graph.getNode(x-1, y+1);
    	if (y>0) {
    		neighbours[3]=graph.getNode(x, y-1);
    		neighbours[4]=graph.getNode(x-1, y-1);
    	}else {
    		neighbours[3]=graph.getNode(x, y+1);
    		neighbours[4]=graph.getNode(x-1, y+1);
    	}
    	
    	return neighbours;
    }
    
    private Node[] getAllneighbours(Node n){
    	Node[] neighbours = new Node[3];
    	int x = n.getX(), y = n.getY();
    	
    	neighbours[0]=graph.getNode(x+1, y);
    	neighbours[1]=graph.getNode(x, y+1);
    	neighbours[2]=graph.getNode(x+1, y+1);
    	
    	return neighbours;
    }
        
    private Node followTrailTransitionRule (Node randNode, Node[] neighbours){
    	Node pherNode = neighbours[0];
    
    	//Food was never found
    	if (antColony.getBestPathValue() == -1)
    		return randNode;
    	
    	for (int i = 1; i < neighbours.length; i++) {
        	//Lower the index the higher the value of pheromone
			if (neighbours[i].getPheromoneIndex() < pherNode.getPheromoneIndex()){
				pherNode = neighbours[i];
			}
		}

    	//Actually there is no pheromone in any neighbour, so just return random node
    	if (pherNode.getPheromoneIndex() == Node.LOWEST_PHEROMONE_INDEX)
    		return randNode;

    	
    	//We now pick the pheromone node with higher probablity
    	double d = pseudoRandom.nextDouble();
    	
    	/*
    	 * We cud increase or decrease the probablity, but 0.7 seems to be better in finding 
    	 * shorter paths, increasing further, all ants follow the trail but dont find new shorterpath
    	 * Decreasing it, the ants wander more randomly, even though the trail is right thre.
    	 */
    	if (d <= GlobalSettings.PROB_PHEROMONE_NODE)
    		return pherNode;
    	else
    		return randNode;
    }

    public void setFoundFood(boolean b){
    	foundFood = b;
    }
    public boolean isFoundFood (){
    	return foundFood;
    }
    
    public Node getCurNode(){
    	return curNode;
    }
    
    //Having chosen a next node, update the state of the ant by updating:
    //curPath, curPathValue etc.
    public void updateCurPath(Node curNode, Node newNode) {
    	//First check if the new node is the start node.
    	if (newNode == startNode) {
    		//if food is found then we need to update bestpath
    		if (foundFood) {
    			updateBestPath();
    		} else {
        		if (DebugSettings.ALL_PATH_LENGTHS) {
        			DebugSettings.writeToFile("Not ending in food Path Len = "+curPath.getLength());
        		}
    		}
    		curPath = new Path();
    		//Here it gives outOfMemory exception after some time..
    		//System.gc();
    		
    		return;
    	}
    	
    	//Check if the new node falls on the same edge,
    	//We do this by taking the slope of the last edge and see our edge slope is the same
    	try {
			int x = newNode.getX(), y = newNode.getY(); 
			Edge lastEdge = curPath.getLastEdge();
			Node source = lastEdge.getSource();
			Node dest = lastEdge.getDest();
			int sx = source.getX(), sy  = source.getY();
			int dx = dest.getX(), dy = dest.getY();
            
			// Check if src, dest and new node are on the same line
			if ((sx == dx && x == dx && y>dy) || (sy == dy && y == dy && x>dx)){
				//update the lastEdge
				lastEdge.updateDestNode(newNode);
			} else if (sx != dx && sy != dy && x != dx && y != dy) {
				//update the lastEdge
				lastEdge.updateDestNode(newNode);
			} else {
				//create a new edge
				curPath.addEdge(new Edge(curNode,newNode));
			}
		} catch (NullPointerException e) {
			//This is the first edge
			curPath.addEdge(new Edge(curNode, newNode));
		}
    	
    }
    
    private void updateBestPath (){
    	double curLen = curPath.getLength();
		if (DebugSettings.ALL_PATH_LENGTHS) {
			DebugSettings.writeToFile("Ending in food Path Len = "+curLen);
		}
    	if (bestPath == null || bestPathValue > curLen){
    		this.bestPath = curPath;
    		bestPathValue = curLen;
    	}
    	updateAntState(this.curPath, this.bestPath);
    }
    
    //update the bestPath and bestPathValue
    //Also update the pheromone content for the new path here
    public void updateAntState(Path curPath, Path bestPath){
    	curPath.updatePheromone(graph, antColony.nodesWithPheromone);    	
    }
    
}
