import java.util.ArrayList;

public class Card implements CardInterface{
	private Values value;
	private Colors color;
	private boolean known;
	private PossibleCards possible;

	/*
	* constructors
	*/
	public Card(Values value, Colors color){
		this.value = value;
		this.color = color;
		this.known = false;
		this.possible = new PossibleCards();
	}
	
	public Card(String string){
		//TODO string = 3W, 5B, 2R, etc...
		char value = string.charAt(0);
		char color = string.charAt(1);
		switch(value){
			case '1' : this.value = Values.One;break;
			case '2' : this.value = Values.Two;break;
			case '3' : this.value = Values.Three;break;
			case '4' : this.value = Values.Four;break;
			case '5' : this.value = Values.Five;break;
			default : this.value = Values.Unknown;break;
		}
		switch(color){
			case 'R' : this.color = Colors.Red;break;
			case 'W' : this.color = Colors.White;break;
			case 'G' : this.color = Colors.Green;break;
			case 'Y' : this.color = Colors.Yellow;break;
			case 'B' : this.color = Colors.Blue;break;
			default : this.color = Colors.Unknown;break;
		}
		this.known = false;
		this.possible = new PossibleCards();
	}
	
	public Card(Card card){
		this.value = card.getValue();
		this.color = card.getColor();
		this.known = card.isKnown();
		this.possible = card.getPossibleCards();
	}

	@Override
	public Values getValue() {
		return value;
	}

	@Override
	public Colors getColor() {
		return color;
	}
	
	public PossibleCards getPossibleCards() {
		return new PossibleCards(possible);
	}

	public void update(Table table, Graveyard graveyard, ArrayList<Player> allPlayers, boolean[] mask) {
		possible.update(this, table, graveyard, allPlayers, mask);
		known = (possible.getKnowledgeCoef() == 3);
	}

	@Override
	public boolean isKnown() {
		return known;
	}

	@Override
	public boolean equals(Object card) {
		if(card==null) return false;
		if(card==this) return true;
		if(color == ((Card)card).getColor() && value == ((Card)(card)).getValue()) return true;
		return false;
	}

	@Override
	public void knowledge(Hint hint) {
		possible.addHint(hint);
	}
	
	public int getKnowledgeScore(Hint hint, Table table,Graveyard graveyard, ArrayList<Player> allPlayers,boolean[] mask){
		int initScore = possible.getKnowledgeCoef();
		PossibleCards simulatePossible = new PossibleCards(possible);
		simulatePossible.addHint(hint);
		simulatePossible.update(this, table, graveyard, allPlayers, mask);
		int newScore = simulatePossible.getKnowledgeCoef();
		return newScore - initScore;
	}
	
	public boolean isPlayable(Table table){
		return possible.isPlayable(table);
	}
	
	public boolean isDiscardable(Table table){
		return possible.isDiscardable(table);
	}
	
	@Override 
	public String toString(){
		return value.toString() + color.toString();
	}
	
	
}
