import java.util.ArrayList;

public class Player implements PlayerInterface{
	private ArrayList<Card> hand;
	private int id;
	
	public static int nPlayers = 0;
	
	/* 
	* constructors 
	*/

	public Player(Deck deck){
		this.id = nPlayers;
		nPlayers++;
		hand = new ArrayList<Card>();
		for(int i=0; i<Constants.NMAXCARDS; i++){
			hand.add(deck.drawCard());
		}
	}
	
	public Player(Player player){
		this.hand = new ArrayList<Card>();
		ArrayList<Card> hand = player.getHand();
		for(Card card: hand){
			this.hand.add(new Card(card));
		}
		this.id = player.getID();
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
	
	@Override
	public Action turn() {
		return null;
	}

	@Override
	public ArrayList<Card> getHand() {
		return hand;
	}

	@Override
	public int getID() {
		return id;
	}
	
	@Override 
	public int getNCards() {
		return hand.size();
	}

	@Override
	public void drawCard(Deck deck) {
		Card card = deck.drawCard();
		if(card!=null){
			hand.add(card);
		}
	}

	@Override
	public void leaveCard(int idx) {
		hand.remove(idx);
	}
	
	public ArrayList<Card> getKnown() {
		ArrayList<Card> knownCards = new ArrayList<Card>();
		for(Card card: hand){
			if(card.isKnown()){
				knownCards.add(card);
			}
		}		
		return knownCards;
	}
	
	public void takeHint(Hint hint, Table table){
		boolean meta = false;
		if(hint.getColor() == Colors.Unknown && hint.getValue() != Values.Unknown){
			for(int i = hand.size()-1; i >= 0; i--){
				Card card = new Card(hand.get(i));
				if(hand.get(i).getValue() == hint.getValue()){
					card.knowledge(hint);
					if(!meta){
						for(Card wanted : table.getWantedCards()){
							if(!meta && wanted.getValue() == hint.getValue()){
								meta = true;
								if(card.getPossibleCards()
								.getPossibilities(wanted.getColor().toInt(),wanted.getValue().getIntValue()-1) > 0){
									card.knowledge(new Hint(wanted.getColor(),false,false));
								}
							}
									
						}
					}
				}
				else card.knowledge(new Hint(hint,true));
				hand.set(i, card);
			}
		}
		else if(hint.getColor() != Colors.Unknown && hint.getValue() == Values.Unknown){
			for(int i = hand.size()-1; i >= 0; i--){
				Card card = new Card(hand.get(i));
				if(card.getColor() == hint.getColor()) card.knowledge(hint);
				else card.knowledge(new Hint(hint,true));
				if(!meta){
					for(Card wanted : table.getWantedCards()){
						if(!meta && wanted.getColor() == hint.getColor() && wanted.getValue() != Values.Unknown &&
								card.getPossibleCards()
								.getPossibilities(wanted.getColor().toInt(),wanted.getValue().getIntValue()-1) > 0){
							meta = true;
							card.knowledge(new Hint(wanted.getValue(),false,false));
						}
					}
				}
				hand.set(i, card);
			}
		}
	}
	
	public void update(Table table, Graveyard graveyard, ArrayList<Player> allPlayers, boolean[] mask){
		for(int i = 0; i < hand.size(); i++){
			Card card = new Card(hand.get(i));
			card.update(table, graveyard, allPlayers, mask);
			hand.set(i,card);
		}
	}
	
	public int getHintScore(Hint hint, Table table,Graveyard graveyard, ArrayList<Player> allPlayers,boolean[] mask){
		int score = 0;
		for(Card card : hand){
			if(table.getWantedCards().contains(card)) 
				score += card.getKnowledgeScore(hint, table, graveyard, allPlayers, mask);
		}
		return score;
	}
	
	@Override
	public String toString(){
		String s = "";
		for(Card card: hand){
			s += card.toString() + " ";
		}
		return s;
	}
	
	public static void reset(){
		nPlayers = 0;
	}
}
