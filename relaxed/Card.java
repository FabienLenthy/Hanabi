
public class Card implements CardInterface{
	private Values value;
	private Colors color;
	private boolean known;

	/**
	 * Constructor
	 * @param value Card value
	 * @param color Card color
	 */
	public Card(Values value, Colors color){
		this.value = value;
		this.color = color;
		this.known = false;
	}
	
	/**
	 * Constructor
	 * @param string Card name
	 */
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
	}
	
	/**
	 * Contructor to copy card
	 * @param card Card
	 */
	public Card(Card card){
		this.value = card.getValue();
		this.color = card.getColor();
		this.known = card.isKnown();
	}

	@Override
	public Values getValue() {
		return value;
	}

	@Override
	public Colors getColor() {
		return color;
	}

	@Override
	public boolean isKnown() {
		return known;
	}

	/**
	 * See if 2 cards have the same value and same color
	 */
	@Override
	public boolean equals(Object card) {
		if(card==null) return false;
		if(card==this) return true;
		if(color == ((Card)(card)).getColor() && value == ((Card)(card)).getValue()) return true;
		return false;
	}

	/**
	 * Give the player information on this card
	 */
	@Override
	public void knowledge() {
		known = true;
	}
	
	@Override 
	public String toString(){
		return value.toString() + color.toString();
	}
	
}
