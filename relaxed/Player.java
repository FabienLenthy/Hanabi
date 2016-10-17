import java.util.ArrayList;

public class Player implements PlayerInterface{
	private ArrayList<Card> hand;
	private int id;
	
	public static int nPlayers = 0;
	
	/** 
	* constructors 
	*/
	public Player(ArrayList<Card> hand, int id){
		this.hand = hand;
		this.id = id;
	}
	
	/**
	 * The player is created and draws cards from the deck
	 * @param deck
	 */
	public Player(Deck deck){
		this.id = nPlayers;
		nPlayers++;
		hand = new ArrayList<Card>();
		for(int i=0; i<Constants.NMAXCARDS; i++){
			hand.add(deck.drawCard());
		}
	}
	
	/**
	 * The player is created and draws cards from the deck and is given an ID
	 * @param deck
	 * @param id
	 */
	public Player(Deck deck, int id){
		this.id = id;
		hand = new ArrayList<Card>();
		for(int i=0; i<Constants.NMAXCARDS; i++){
			hand.add(deck.drawCard());
		}
	}
	
	/**
	 * Copy player
	 * @param player Player
	 */
	public Player(Player player){
		this.hand = new ArrayList<Card>();
		ArrayList<Card> hand = player.getHand();
		for(Card card: hand){
			this.hand.add(new Card(card));
		}
		this.id = player.getID();
	}

	@Override
	public ArrayList<Card> getHand() {
		return hand;
	}

	@Override
	public int getID() {
		return id;
	}
	
	/**
	 * Get number of cards in the hand
	 */
	@Override 
	public int getNCards() {
		return hand.size();
	}

	/**
	 * Draw a card
	 */
	@Override
	public void drawCard(Deck deck) {
		Card card = deck.drawCard();
		if(card!=null){
			hand.add(card);
		}
	}

	/**
	 * Play or discard a card
	 */
	@Override
	public void leaveCard(int idx) {
		hand.remove(idx);
	}
	
	@Override
	public String toString(){
		String s = "";
		for(Card card: hand){
			s += card.toString() + " ";
		}
		return s;
	}
}
