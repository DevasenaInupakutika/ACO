
//import Controller;

public class World {
	
	Node[][] world;
	int x_width;
	int y_width;
	Food food;
	
	Node startNode;
	
	public World(int x_width, int y_width)
	{
		this.x_width = x_width;
		this.y_width = y_width;
		world = new Node[x_width][y_width];
	}

	public Node getStartNode(){
		return startNode;
	}
	
	public int getXWidth(){
		return x_width;
	}
	
	public int getYWidth (){
		return y_width;
	}
	
	public void initializeWorld(Food f, int startX, int startY)
	{
		for(int i = 0; i < x_width; i++)
		{
			for(int j = 0; j < y_width; j++)
			{
				Node node = new Node(i, j);
				world[i][j] = node;
			}
		}
		startNode = world[startX][startY];

		//First check if the food is within the limits of the world
		//If it is beyond the boundary, set it atleast 5 units before the boundary
		if (f.lowerX >= x_width)
			f.lowerX = x_width - 5;
		if (f.lowerY >= y_width)
			f.lowerY = y_width - 5;
		
		int hx = f.lowerX+f.width;
		int hy = f.lowerY+f.height;;
		
		hx = (hx > x_width)? x_width : hx; 
		hy = (hy > y_width)? y_width : hy;
		
		for (int i = f.lowerX; i <hx; i++) {
			for (int j = f.lowerY; j < hy; j++) {
				try {
					world[i][j].setIsFood();
				} catch (Exception e) {
					//eat any index out of bound exception
				}
			}
		}
		//Set the food
		//This is actually not needed.
		this.food = f;
	}

	public void initializeObstacles(Obstacle f[])
	{
		for (int i = 0; i < f.length; i++) {
			//First check if the food is within the limits of the world
			//If it is beyond the boundary, set it atleast 5 units before the boundary
			if (f[i].lowerX >= x_width)
				f[i].lowerX = x_width - 5;
			if (f[i].lowerY >= y_width)
				f[i].lowerY = y_width - 5;
			
			int hx = f[i].lowerX+f[i].width;
			int hy = f[i].lowerY+f[i].height;;
			
			hx = (hx > x_width)? x_width : hx; 
			hy = (hy > y_width)? y_width : hy;
			
			for (int k = f[i].lowerX; k <hx; k++) {
				for (int j = f[i].lowerY; j < hy; j++) {
					try {
						world[k][j].setIsObstacle();
						
					} catch (Exception e) {
						//eat any index out of bound exception
					}
				}
			}
			
		}
	}

	public Node getNode (int x, int y){
		return world[x][y];
	}
	
	//This method was used before storing the sparse matrix, can be used for comparing the performance
	//Deprecated method, we use sparse matrices instead of this...
	public void evaporatePheromone(int steps, Controller controller) {
		for (int i = 0; i < x_width; i++) {
			for (int j = 0; j < y_width; j++) {
				Node node = world[i][j];
				if (!node.isFood())
					world[i][j].evaporateAndUpdateController(steps, controller);
			}
		}
	}
}
