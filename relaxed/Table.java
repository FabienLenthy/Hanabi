import java.util.ArrayList;
import java.util.Stack;

public class Table implements TableInterface {
	private ArrayList<Stack<Card>> playedCards;
	private ArrayList<Card> wantedCards;
		
	/**
	 * Init table. playedCards takes all played cards, wantedCards takes all the cards that can be played
	 */
	public Table(){
		this.playedCards = new ArrayList<Stack<Card>>();
		for(int i=0; i<5; i++){
			this.playedCards.add(new Stack<Card>());
		}
		this.wantedCards = new ArrayList<Card>();
		wantedCards.add(new Card("1R"));
		wantedCards.add(new Card("1G"));
		wantedCards.add(new Card("1B"));
		wantedCards.add(new Card("1Y"));
		wantedCards.add(new Card("1W"));
	}
	
	/**
	 * Copy table
	 * @param table Table
	 */
	public Table(Table table){
		this.playedCards = new ArrayList<Stack<Card>>();
		for(int i=0; i<5; i++){
			this.playedCards.add(new Stack<Card>());
		}
		this.wantedCards = new ArrayList<Card>();
		ArrayList<Stack<Card>> playedCards = table.getPlayedCards();
		for(int i=0; i<playedCards.size(); i++){
			for(int j=0; j<playedCards.get(i).size(); j++){
				this.playedCards.get(i).add(new Card(playedCards.get(i).get(j)));
			}
		}
		ArrayList<Card> wantedCards = table.getWantedCards();
		for(int i=0; i<wantedCards.size(); i++){
			this.wantedCards.add(new Card(wantedCards.get(i)));
		}
	}
	
	@Override
	public ArrayList<Stack<Card>> getPlayedCards() {
		return playedCards;
	}

	@Override
	public ArrayList<Card> getWantedCards() {
		return wantedCards;
	}

	/**
	 * Add a card on the table
	 */
	@Override
	public void addCard(Card card) {
		Colors color = card.getColor();
		Values value = card.getValue();
		playedCards.get(color.toInt()).push(card);
		if(value == Values.Five) wantedCards.set(color.toInt(), new Card(Values.Unknown, color));
		else wantedCards.set(color.toInt(), new Card(value.next(), color));
	}

	/**
	 * count the number of card in playedCards, indirectly
	 */
	@Override
	public int score() {
		int score = 0;
		for(Card card : wantedCards){
			Values cardValue = card.getValue();
			if(cardValue == Values.Unknown) score += 5;
			else score += cardValue.getIntValue()-1;
		}
		return score;
	}
	
	@Override
	public String toString(){
		String s = "";
		for(int i=0; i<playedCards.size(); i++){
			if(playedCards.get(i).isEmpty()){
				s += "TT ";
				continue;
			}
			s += playedCards.get(i).peek().toString() + " ";
		}
		s += "||TABLE";
		s += "\n";
		for(Card card: wantedCards){
			s += card.toString() + " ";
		}
		s += "||WANTED";
		return s;
	}
	
	/**
	 * Check if a card has been played
	 */
	@Override
	public boolean contains(Card card){
		Colors color = card.getColor();
		if(playedCards.get(color.toInt()).isEmpty()){
			return false;
		}
		if(card.getValue().isBelow(playedCards.get(color.toInt()).peek().getValue())){
			return true;
		}
		return false;
	}
	
	public String printTable(){
		String s = "TABLE : ";
		for(int i=0; i<playedCards.size(); i++){
			if(playedCards.get(i).isEmpty()){
				s += "__ ";
				continue;
			}
			s += playedCards.get(i).peek().toString() + " ";
		}
		return s;
	}
}
