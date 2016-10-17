import java.util.ArrayList;

public class Path {

	private int score;
	private ArrayList<Action> actions;
	
	public Path(int score, ArrayList<Action> actions){
		this.score =score;
		this.actions = actions;
	}
 
	public int getScore(){
		return this.score;
	}
	
	public ArrayList<Action> getActions(){
		return this.actions;
	}
}
