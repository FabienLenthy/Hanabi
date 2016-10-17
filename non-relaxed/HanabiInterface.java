import java.util.ArrayList;

public interface HanabiInterface {

		public Hanabi applyAction(Action action);

		public ArrayList<Hanabi> findPossibleActions(); // return the next possible states
		
		public Player getMe();
		
		public ArrayList<Player> getOtherPlayers();
		
		public ArrayList<Player> getAllPlayers();
		
		public Graveyard getGraveyard();
		
		public Deck getDeck();
		
		public Table getTable();
		
		public int getNHintsLeft();
		
		public int getNBoom();
		
		public ArrayList<Card> getDangers();
		
		public Action getLastAction();
		
		public boolean isEOG();
		
		public boolean isCertain();
		
		public void removeMask();
		
		public int getStateScore();
		
		public String toString();
}
