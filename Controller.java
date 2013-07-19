
public interface Controller {

	public void setStartOfIteration();
	
	public void setEndOfIteration();
	
	public void setAntAt(int x, int y);
	
	//Highest pheromone content when levelIndex is 0
	void setPheromoneAt(int x, int y, int levelIndex, int lowestIndex);
}
