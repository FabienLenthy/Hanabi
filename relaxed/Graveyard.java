import java.util.ArrayList;

public class Graveyard implements GraveyardInterface{
	private ArrayList<Card> trash;
	
	/*
	 * constructors
	 */
	
	public Graveyard() {
		this.trash = new ArrayList<Card>();
	}
	
	/**
	 * Copy graveyard
	 * @param graveyard Graveyard
	 */
	public Graveyard(Graveyard graveyard){
		this.trash = new ArrayList<Card>();
		ArrayList<Card> trash = graveyard.getTrash();
		for(Card card: trash){
			this.trash.add(new Card(card));
		}
	}
		
	/**
	 * add an element to the graveyard
	 */
	@Override
	public void add(Card card) {
		trash.add(card);
	}
	
	@Override
	public ArrayList<Card> getTrash(){
		return trash;
	}
	
	@Override
	public String toString(){
		String s = "";
		for(int i=0; i<trash.size(); i++){
			s += trash.get(i).toString() + " ";
		}
		return s;
	}
	
	/**
	 * Test if a card is in the graveyard
	 */
	@Override
	public boolean contains(Card card){
		return trash.contains(card);
	}

}
