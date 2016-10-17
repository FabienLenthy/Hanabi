import java.util.ArrayList;

public interface PlayerInterface {
	
	public Action turn();
	
	public void drawCard(Deck deck);
	
	public ArrayList<Card> getHand();
	
	public int getID();
	
	public int getNCards();
	
	public void leaveCard(int idx);
	
	public String toString();
	
}
