import java.util.Stack;

public interface DeckInterface {
	
	public Card drawCard(); //called when drawing a card
	public Boolean isEmpty(); //called at the beginning of each turn, if true, the player call decreaseCountDown
	public Boolean gameOver();
	public Stack<Card> getStack();
}
