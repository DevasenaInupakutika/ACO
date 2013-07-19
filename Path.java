import java.util.*;

//import Controller;

public class Path {

	private ArrayList<Edge> Edges;
	private double length = 0.0;
	private Edge lastEdge;
	
	public Path() {
		Edges = new ArrayList<Edge>();
		length = 0.0;
		lastEdge = null;
	}
	public void addEdge(Edge newEdge)
	{
		this.Edges.add(newEdge);
		lastEdge = newEdge;
	}
	
	public Edge getLastEdge (){
		return lastEdge;
	}
	
	public ArrayList<Edge> getEdges()
	{
		return this.Edges;
	}
	
	public double getLength()
	{
		Iterator itr = this.Edges.iterator();
		while(itr.hasNext())
		{
			Edge currentEdge = (Edge) itr.next();
			length = length + currentEdge.getLength();
		}
		return length;
	}
	
	public void updatePheromone (World world, LinkedList<Node> list){
		Iterator<Edge> itr = this.Edges.iterator();
		while(itr.hasNext())
		{
			Edge currentEdge = (Edge) itr.next();
			currentEdge.updatePheromone(world, list);
		}
	}
}
