import java.util.ArrayList;
import java.util.Arrays;

public class PossibleCards {
	private int[][] matrix;
	ArrayList<Hint> hints;
	
	public PossibleCards(){
		matrix = new int[5][5];
		for(int i = 0; i < 5; i++){
			matrix[i] = new int[]{3,2,2,2,1}; 
		}
		hints = new ArrayList<Hint>();
	}
	
	public PossibleCards(PossibleCards possibilities){
		this.matrix = new int[5][5];
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++){
				this.matrix[i][j] = possibilities.getPossibilities(i,j);
			}
		}
		this.hints = new ArrayList<Hint>();
		for (Hint hint : possibilities.getHints()){
			this.hints.add(new Hint(hint, false));
		}
	}
	
	public int[][] getPossibilities(){
		int[][] newMatrix = new int[5][5];
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++){
				newMatrix[i][j] = getPossibilities(i,j);
			}
		}
		return newMatrix;
	}
	
	public int getPossibilities(int i, int j){
		return matrix[i][j];
	}
	
	public ArrayList<Hint> getHints(){
		ArrayList<Hint> newHints = new ArrayList<Hint>();
		for (Hint hint : hints){
			newHints.add(new Hint(hint, false));
		}
		return newHints;
	}
	
	public void set(int i, int j, int value){
		if(value >= 0) matrix[i][j] = value;
	}
	
	public void addHint(Hint hint){
		hints.add(hint);
	}
	
	public boolean isPlayable(Table table){
		ArrayList<Card> wantedCards = table.getWantedCards();
		ArrayList<Colors> possibleColors = new ArrayList<Colors>
		(Arrays.asList(Colors.Red,Colors.Green,Colors.Blue,Colors.Yellow,Colors.White));
		ArrayList<Values> possibleValues = new ArrayList<Values>
		(Arrays.asList(Values.One,Values.Two,Values.Three,Values.Four,Values.Five));
		for(Colors color: possibleColors){
			for(Values value : possibleValues){
				if(getPossibilities(color.toInt(),value.getIntValue()-1) > 0){
					if(!wantedCards.contains(new Card(value,color))) return false;
				}
			}
		}
		return true;
	}
	
	public boolean isDiscardable(Table table){
		ArrayList<Colors> possibleColors = new ArrayList<Colors>
		(Arrays.asList(Colors.Red,Colors.Green,Colors.Blue,Colors.Yellow,Colors.White));
		ArrayList<Values> possibleValues = new ArrayList<Values>
		(Arrays.asList(Values.One,Values.Two,Values.Three,Values.Four,Values.Five));
		for(Colors color: possibleColors){
			for(Values value : possibleValues){
				if(getPossibilities(color.toInt(),value.getIntValue()-1) != 0){
					if(!table.contains(new Card(value,color))) return false;
				}
			}
		}
		return true;
	}
	
	public void update(Card thisCard, Table table, Graveyard graveyard, ArrayList<Player> allPlayers, boolean[] mask){
		matrix = new int[5][5];
		if(thisCard.isKnown()){
			matrix[thisCard.getColor().toInt()][thisCard.getValue().getIntValue()-1] = 1;
			return;
		}
		ArrayList<Colors> possibleColors = new ArrayList<Colors>
		(Arrays.asList(Colors.Red,Colors.Green,Colors.Blue,Colors.Yellow,Colors.White));
		ArrayList<Values> possibleValues = new ArrayList<Values>
		(Arrays.asList(Values.One,Values.Two,Values.Three,Values.Four,Values.Five));
		int nbrHint = hints.size();
		
		if(nbrHint > 0){
			for(int h = 0; h < nbrHint; h++){
				if(hints.get(h).getColor() == Colors.Unknown && hints.get(h).getValue() != Values.Unknown){
					if(hints.get(h).getAnti()) {
						possibleValues.remove(hints.get(h).getValue());
					}
					else {
						possibleValues = new ArrayList<Values>(Arrays.asList(hints.get(h).getValue()));
					}
				}
				else if(hints.get(h).getColor() != Colors.Unknown && hints.get(h).getValue() == Values.Unknown){
					if(hints.get(h).getAnti()) {
						possibleColors.remove(hints.get(h).getColor());
					}
					else {
						possibleColors = new ArrayList<Colors>(Arrays.asList(hints.get(h).getColor()));
					}
				}
			}
		}
		int[] lines = new int[]{3,2,2,2,1};
		for(Colors i : possibleColors){
			for(Values j : possibleValues){
				set(i.toInt(),j.getIntValue()-1,lines[j.getIntValue()-1]);
			}
		}
		ArrayList<Card> trash = graveyard.getTrash();
		ArrayList<Card> allCards = new ArrayList<Card>();
		allCards.addAll(trash);
		for(int p = 0; p < 4; p++){
			if(mask[p]) allCards.addAll(allPlayers.get(p).getHand());
			else allCards.addAll(allPlayers.get(p).getKnown());
		}
		for(Colors i : possibleColors){
			for(Values j : possibleValues){
				if(table.contains(new Card(j,i))) 
					set(i.toInt(), j.getIntValue()-1, matrix[i.toInt()][j.getIntValue()-1]-1);
			}
		}
		for(Card card : allCards){
			Colors i = card.getColor();
			Values j = card.getValue();
			if(possibleColors.contains(i) & possibleValues.contains(j))
				set(i.toInt(),j.getIntValue()-1, matrix[i.toInt()][j.getIntValue()-1]-1);
		}
		if(this.getKnowledgeCoef() == -1){
			ArrayList<Hint> newHints = new ArrayList<Hint>();
			for(Hint hint : hints){
				if(hint.getCertain()){
					newHints.add(hint);
				}
			}
			hints = new ArrayList<Hint>(newHints);
		}
	}
	
	public int getKnowledgeCoef(){
		int coef = 0;
		int knownColor = -1;
		int knownValue = -1;
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++){
				if(getPossibilities(i,j) > 0){
					if(knownColor == -1) knownColor = i;
					else knownColor = -2;
					if(knownValue == -1) knownValue = i;
					else knownValue = -2;
				}
			}
		}
		if(knownValue >= 0) coef += 1;
		if(knownColor >= 0) coef += 2;
		if(knownColor == -1 && knownValue == -1){
			coef = -1;
		}
		return coef;
	}
	
	
	
	public String toString(){
		String s = "";
		for(int[] line : matrix){
			for(int i : line){
				s += i;
			}
			s += "\n";
		}
		return s;
	}
}
