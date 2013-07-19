

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JScrollPane;



public class Launcher implements Controller{
	
	private BufferedImage currentBI;
	private int width, height;
	private AntColony ac;
	private Food food;
	private World world;
	private DisplayPanel mainPanel;
	private JFrame frame;
	
	private final static int WINDOW_WIDTH = 900;
	private final static int WINDOW_HEIGHT = 700;
	private final static int COLOR_RED_VAL = 255 << 16;
	private final static int COLOR_GB_VAL = 255|(255<<8);
	
	public Launcher(int width, int height) {
		
		this.width = width;
		this.height = height;

		//Create world
		world = new World(width, height);
		
		//create food, at the given location
		food =  new Food(GlobalSettings.FOOD_X,GlobalSettings.FOOD_Y,GlobalSettings.FOOD_WIDTH,GlobalSettings.FOOD_HEIGHT);
		
		//initialize world, start node has to be 0,0 because our ants dont go 360 degrees
		world.initializeWorld(food, 0, 0);
		
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
		frame.setLocation(0,0);

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
		Launcher l = new Launcher(GlobalSettings.WORLD_HEIGHT,GlobalSettings.WORLD_HEIGHT);
		l.start();
	}
}
