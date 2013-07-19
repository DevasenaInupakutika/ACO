
import java.util.LinkedList;

//import Controller;

public class Edge{
	
	private Node source;
	private Node dest;
	
	//constructor
	public Edge(Node source, Node dest)
	{
		this.source = source;
		this.dest = dest;
	}
	
	
	public double computeLength()
	{
		double xSource = this.source.getX();
		double xDesination = this.dest.getX();
		double ySource = this.source.getY();
		double yDesination = this.dest.getY();
		
		//distance between source and destination ==>
		//root{(x1-x2)^2 + (y1-y2)^2}
		double length = Math.sqrt((Math.pow((xSource-xDesination), 2)) + (Math.pow((ySource-yDesination), 2)));
		
		return length;
	}

	public Node getSource(){
		return source;
	}
	
	public Node getDest(){
		return dest;
	}
	
	public void updateDestNode(Node dest){
		this.dest = dest;
	}
	
	public double getLength()
	{
		double length = this.computeLength();
		
		return length;
	}


	public void updatePheromone(World world, LinkedList<Node> list) {
		int destX = dest.getX();
		int destY = dest.getY();
		//Note that the edges can be of slope 0, 1 and inf
		int x = source.getX();
		int y = source.getY();
		//Loop till we reach the dest node
		
		while (x != destX || y != destY){
			Node node = world.getNode(x, y);
			//Need to add this node to the list, but first check if this already has pheromone.
			//If it had pheromone then it wud have already been in the list
			if (node.getPheromoneIndex() == Node.LOWEST_PHEROMONE_INDEX) {
				//New node,so add to list
				list.add(node);
			}
			node.setPheromoneIndex(0);
			x++;
			if (x>destX)
				x = destX;
			y++;
			if (y>destY)
				y = destY;
		}
	}
	
	
}
