import java.util.ArrayList;
import java.util.Stack;

public interface TableInterface {

	public ArrayList<Stack<Card>> getPlayedCards();
	public ArrayList<Card> getWantedCards();
	
	public void addCard(Card card); 
	public int score();
	public String toString();
	public boolean contains(Card card);
}
