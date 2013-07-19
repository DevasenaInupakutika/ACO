

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class LaunchObstacle implements Controller{
	
	private BufferedImage currentBI;
	private int width, height;
	private AntColony ac;
	private Food food;
	private int noOfObstacles;
	private Obstacle obstacles[];
	private World world;
	private DisplayPanel mainPanel;
	private JFrame frame;
	
	private final static int WINDOW_WIDTH = 900;
	private final static int WINDOW_HEIGHT = 700;
	private final static int COLOR_RED_VAL = 255 << 16;
	private final static int COLOR_GB_VAL = 255|(255<<8);
	
	/**
	 * @param width
	 * @param height
	 */
	/**
	 * @param width
	 * @param height
	 */
	public LaunchObstacle(int width, int height) {
		
		//Setup the global settings,
		GlobalSettings.FOOD_X = width-10;
		GlobalSettings.FOOD_Y = height/2 - GlobalSettings.FOOD_HEIGHT/2;
		
		GlobalSettings.STATE_TRANSITION_RULE_NUMBER = 4;
		GlobalSettings.TOTAL_ANTS = 100;
		GlobalSettings.STEPS_PER_ITERATION = 2;
		GlobalSettings.EVAPORATION_STEPS_PER_ITERAION = 1;
		GlobalSettings.LOWEST_PHEROMONE_INDEX = 40;
		GlobalSettings.PROB_PHEROMONE_NODE = 1;
		
		
		this.width = width;
		this.height = height;

		//Create world
		world = new World(width, height);
		
		//create food, at the given location
		food =  new Food(GlobalSettings.FOOD_X,GlobalSettings.FOOD_Y,GlobalSettings.FOOD_WIDTH,GlobalSettings.FOOD_HEIGHT);
		
		world.initializeWorld(food, 0, height/2);
		
		noOfObstacles = 5;
		obstacles = new Obstacle[noOfObstacles];
		
		//Create an array of obstacles; //TODO run a loop and generate random obstacles
		obstacles[0] = new Obstacle(50, height/2-50, 30, 80);
		obstacles[1] = new Obstacle(150, height/2-200, 20, 240);
		obstacles[2] = new Obstacle(260, height/2 - 25, 40, 130);
		obstacles[3] = new Obstacle(390, height/2 - 40, 20, 50);
		obstacles[4] = new Obstacle(490, height/2 - 3, 55, 15);
			
		world.initializeObstacles(obstacles);
		
		//Create ant colony, Nants  = 2nd arg and 50000 iterations
		ac = new AntColony(world, GlobalSettings.TOTAL_ANTS, GlobalSettings.TOTAL_ITERATIONS, this);
		
		//Initialize ant, steps per iteration
		Ant.initialize(ac, GlobalSettings.STEPS_PER_ITERATION);
		
		//Creating the panels can be done outside the swing thread
		createGUI();
		
		//This is done so that the gui is in a separate thread
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				frame.setVisible(true);
			}
		});
	}

	public void start () {
		/*Runnable r = new Runnable() {
			
			@Override
			public void run() {
				ac.start();
			}
		};
		Thread t  = new Thread(r);
		t.start();*/
		//Actually, i think we dont need to create the thread as above,
		//The following line shld be sufficient
		ac.start();
	}

	private void createGUI() {
		frame = new JFrame("Ant Colony Optimization");

		//THe currentBI has to be available before we create the panel
		this.drawInitialImage();
		
		mainPanel = new DisplayPanel(currentBI, new Dimension(this.width, this.height));
		JScrollPane p = new JScrollPane(mainPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		int w,h;
		
		//Adjust the size of the scroll panel
		w = (this.width < WINDOW_WIDTH)? this.width : WINDOW_WIDTH;
		h = (this.height < WINDOW_HEIGHT)? this.height : WINDOW_HEIGHT;
		//Note: The size is inclusive of the width of the scroll bar, hence we incr by 5
		p.setPreferredSize(new Dimension(w+5, h+5));
		frame.setContentPane(p);


		frame.pack();
		frame.setLocation(25,0);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}


	private void drawInitialImage(){
		currentBI = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2 = currentBI.createGraphics();
		//Default color is white, hence we have a white background
		g2.fillRect(0, 0, width, height);
		
		//Draw the food next
		//Lets take blue for food
		g2.setColor(Color.blue);
		g2.fillRect(food.lowerX, food.lowerY, food.width, food.height);
		
		//Draw the obstacles
		//Lets take color brown for obstacle
		g2.setColor(new Color(95, 251, 49));//some color got from paint
		
		for (int i = 0; i < obstacles.length; i++) {
			g2.fillRect(obstacles[i].lowerX, obstacles[i].lowerY, obstacles[i].width, obstacles[i].height);
		}
		
		int legendX = 120;
		int legendY = 100;
		int gap = 25;
		
		//We now write the LEGEND,
		g2.fillRect(legendX, height-legendY+gap-6, 30, 5);
		g2.setColor(Color.blue);
		g2.fillRect(legendX, height-legendY+(2*gap)-6, 30, 5);
		
		g2.setColor(Color.black);
		
		String legend = "LEGEND:";
		g2.drawChars(legend.toCharArray(), 0, legend.length(), legendX, height-legendY);
		
		legend = " OBSTACLE";
		g2.drawChars(legend.toCharArray(), 0, legend.length(), legendX+30, height-legendY+gap);
		
		legend = " FOOD";
		g2.drawString(legend, legendX+30, height-legendY+(2*gap));
		g2.dispose();
		
	}
	

	@Override
	public void setAntAt(int x, int y) {
		synchronized (currentBI) {
			Graphics2D g2 = currentBI.createGraphics();
			//Ants are black
			g2.setColor(Color.black);
			g2.fillRect(x, y, 2, 2);			
			g2.dispose();
		}
	}

	//The start and end of iteration methods need not be synchronized
	@Override
	public void setStartOfIteration() {
		//Create a new image when an iteration starts
		drawInitialImage();
	}
	
	@Override
	public void setEndOfIteration() {
		//No more changes to the Image, hence flush its resources.
		//Not sure if it has any performance gain.
		currentBI.flush();
		mainPanel.setImage(currentBI);
	}

	@Override
	public void setPheromoneAt(int x, int y, int level_index, int lowest_index) {
		synchronized (currentBI) {
			int mask = (int) (((float)level_index / lowest_index) * COLOR_GB_VAL);
			
			//The larger the mask, the more white the color wud be
			//Pheromone red color, todo : color changes as the level reduces
			currentBI.setRGB(x, y, (int)(COLOR_RED_VAL|mask));
		}		
	}
	public static void main(String[] args) {
		LaunchObstacle l = new LaunchObstacle(650,600);
		l.start();
	}
}
