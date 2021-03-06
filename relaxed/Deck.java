import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck implements DeckInterface{
	
	/**
	 * stack : all cards in deck
	 * countDown : to 0 when end of the game
	 */
	private Stack<Card> stack;
	private int countDown;

	/**
	 * constructors
	 */
	public Deck(){
		stack = new Stack<Card>();
		// randomly shuffle indices [0:49]
		List<Integer> indices = new ArrayList<Integer>();
		for(int i=0; i<Constants.NDECKCARDS; i++){
			indices.add(i);
		}
		Collections.shuffle(indices);
		for(int i=0; i<Constants.NDECKCARDS; i++){
			stack.push(new Card(Constants.DECKINITDEFAULT[indices.get(i)]));
		}
		this.countDown = Constants.NPLAYERS;

	}
	
	/**
	 * Constructor
	 * @param string List of all cards
	 */
	public Deck(String[] string){
		stack = new Stack<Card>();
		for(int i=0; i<string.length; i++){
			stack.push(new Card(string[i]));
		}
		this.countDown = Constants.NPLAYERS;

	}
	
	/**
	 * Constructor to copy deck
	 * @param deck Deck
	 */
	public Deck(Deck deck){
		this.stack = (Stack<Card>) deck.getStack().clone();
		this.countDown = deck.countDown();

	}
		
	/**
	 * Draw a card from the deck
	 */
	@Override
	public Card drawCard() {
		if(this.isEmpty()){
			return null;
		}
		else{
			return stack.pop();
		}
	}

	/**
	 * Test if the deck is empty
	 */
	@Override
	public Boolean isEmpty() {
		return stack.isEmpty();
	}
	
	@Override 
	public Stack<Card> getStack(){
		return stack;
	}
	
	/*
	 * static functions
	 */
	
	/**
	 *  Return true if the string passed is a valid deck 
	 */
	public static Boolean isValid(String string[]){
		// 3 Ones, 2 Twos, 2 Threes, 2 Fours, 1 Fives
		int[] valid = {3, 2, 2, 2, 1};
		int[] NY = {0, 0, 0, 0, 0};
		int[] NB = {0, 0, 0, 0, 0};
		int[] NW = {0, 0, 0, 0, 0};
		int[] NR = {0, 0, 0, 0, 0};
		int[] NG = {0, 0, 0, 0, 0};
		
		for(int i=0; i<string.length; i++){
			int value = Character.getNumericValue(string[i].charAt(0));
			char color = string[i].charAt(1);
			switch(color){
			case 'Y': NY[value-1]++;break;
			case 'B': NB[value-1]++;break;
			case 'W': NW[value-1]++;break;
			case 'R': NR[value-1]++;break;
			case 'G': NG[value-1]++;break;
			}
		}
		if((Arrays.equals(NY, valid) && Arrays.equals(NB, valid))
			&& ((Arrays.equals(NW, valid) && Arrays.equals(NR, valid)) && Arrays.equals(NG, valid))){
			return true;
		}
		else{
			return false;
		}		
	}
	
	/**
	 * Decrement the countDown
	 */
	public void decrease(){
		this.countDown = this.countDown - 1;
	}
	
	public int countDown(){
		return countDown;
	}
	
}
