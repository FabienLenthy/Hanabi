import java.util.ArrayList;

/**This class represents a state of the game and its different components*/
public class Hanabi implements HanabiInterface{
	private ArrayList<Player> allPlayers; //All the players in the game
	private Player me; //The player who is currently playing
	private ArrayList<Player> otherPlayers; //The three other players 
	private boolean[] mask; //Used in the planning algorithm to model whose cards are known by the simulated players (when player A 
	private Graveyard graveyard; //discarded cards (and wrongly played cards)
	private Deck deck; //cards in the deck, to be drawn by the players
	private Table table; //cards played
	private int nHintsLeft; 
	private int nBoom; //number of lives, when we have 3 booms, the game is over
	private ArrayList<Card> dangers; //combination color/value whose instances are almost all in the graveyard
	private ArrayList<Card> dead; //combination color/value whose instances are all in the graveyard (ie cannot be played anymore)
	private Action lastAction; //The action that lead to the current state
	
	public Hanabi(ArrayList<Player> allPlayers, Player me,ArrayList<Player> otherPlayers, boolean[] mask, Graveyard graveyard, 
			Deck deck, Table table, int nHintsLeft, int nBoom, ArrayList<Card> dangers, ArrayList<Card> dead, Action lastAction){
		this.allPlayers = allPlayers;
		this.me = me;
		this.otherPlayers = otherPlayers;
		this.mask = mask;
		this.graveyard = graveyard;
		this.deck = deck;
		this.table = table;
		this.nHintsLeft = nHintsLeft;
		this.nBoom = nBoom;
		this.dangers = dangers;
		this.dead = dead;
		this.lastAction = lastAction;
	}
	
	@Override
	/**
	 * Transition function, returns the state resulting from applying one action to a given state
	 */
	public Hanabi applyAction(Action action) {
		
		//we first begin with copying all the attributes of the current state
		Player currentMe = new Player(me);
		ArrayList<Player> newAllPlayers = new ArrayList<Player>();
		for(int i=0; i<allPlayers.size(); i++){
			if(i==currentMe.getID()) newAllPlayers.add(currentMe); 
			//when modifying currentMe, its occurrence in this list will be modified too (copied by reference)
			else newAllPlayers.add(new Player(allPlayers.get(i)));
		}
		ArrayList<Player> newOtherPlayers = new ArrayList<Player>();
		for(int k=1; k<Constants.NPLAYERS; k++){
			int id = (me.getID()+k)%Constants.NPLAYERS;
			//newOrderPlayers is sorted in the order of the next players to play
			newOtherPlayers.add(new Player(newAllPlayers.get(id)));
		}
		boolean[] newMask = new boolean[Constants.NPLAYERS];
		for(int i=0; i<mask.length; i++){
			newMask[i] = mask[i];
		}
		//The playing player does not know about his cards (unless he was given some clues about them)
		newMask[currentMe.getID()] = false; 
		Graveyard newGraveyard = new Graveyard(graveyard);
		Deck newDeck = new Deck(deck);
		if (newDeck.isEmpty()){
			newDeck.decrease();//if the deck is empty, we decrease the count down
		}
		Table newTable = new Table(table);
		int newNHintsLeft = nHintsLeft;
		int newNBoom = nBoom;
		ArrayList<Card> newDangers = new ArrayList<Card>();
		for(Card card: dangers){
			newDangers.add(new Card(card));
		}
		ArrayList<Card> newDead = new ArrayList<Card>();
		for(Card card: dead){
			newDead.add(new Card(card));
		}
		Card card = action.getCard();
		int type = action.getType();
		int cardID = action.getCardID();
		int playerID = action.getPlayerID();
		
		//we then modify the attribute of the new state depending on what the action is
		if(type == Action.PLAY){
			boolean isPlayed = false;
			//we check if the card we wanted to play can be played
			for(int i = 0; i < Constants.NCOLORS; i++){
				if(table.getWantedCards().get(i).equals(card)){
					if(card.getValue() == Values.Five){
						newNHintsLeft++;
					}
					newTable.addCard(card);
					currentMe.leaveCard(cardID);
					currentMe.drawCard(newDeck);
					isPlayed = true;
					/* update dangers */
					newDangers.remove(card);
					break;
				}
			}
			if(!isPlayed){//if we tried to play a wrong card
				newNBoom++; //loosing a life, when we have 3 "booms" the game is over
				newGraveyard.add(card); //the wrong card goes to the graveyard
				/* update dangers */
				if(card.getValue()!=Values.One && !newDangers.contains(card) &&
						!table.contains(card)){
					newDangers.add(card);
				}
				if(card.getValue()==Values.One && graveyard.contains(card) &&
						!newDangers.contains(card)){
					newDangers.add(card);
				}
				if(dangers.contains(card)) {
					newDead.add(card);
				}
				currentMe.leaveCard(cardID);
				currentMe.drawCard(newDeck);
			}
		}
		else if(type == Action.HINT){
			//give an hint to the player playerID about one of his card
			newAllPlayers.get(playerID).getHand().get(cardID).knowledge();
			newNHintsLeft--;
		}
		else if(type == Action.DISCARD){
			newGraveyard.add(card);
			currentMe.leaveCard(cardID);
			currentMe.drawCard(newDeck);
			newNHintsLeft++; //discarding gives an hint
			/* update dangers */
			if(card.getValue()!=Values.One && !newDangers.contains(card) &&
					!table.contains(card)){
				newDangers.add(card);
			}
			if(card.getValue()==Values.One && graveyard.contains(card) &&
					!newDangers.contains(card)){
				newDangers.add(card);
			}
			if(dangers.contains(card)) {
				newDead.add(card);
			}
		}
		Player newMe = new Player(newAllPlayers.get((me.getID()+1)%Constants.NPLAYERS)); //next player
		return new Hanabi(newAllPlayers, newMe, newOtherPlayers, newMask, newGraveyard, 
				newDeck, newTable, newNHintsLeft, newNBoom, newDangers, newDead, action);
	}
	
	@Override
	/**
	 * Returns all the states that one can reach from the current state
	 * */
	public ArrayList<Hanabi> findPossibleActions() {
		ArrayList<Hanabi> nextStates = new ArrayList<Hanabi>();
		if(isEOG()) return nextStates; //No nextStates if current state
		
		//All the play actions (one for each card in the current player's hand
		for(int i = 0; i < me.getNCards(); i++){
			Action play = new Action(me.getHand().get(i), Action.PLAY, me.getID(), i);
			nextStates.add(applyAction(play));
		}
		
		//all the hint actions (we can give one hint for each card in each player's hand)
		if(nHintsLeft > 0){
			for(int k = 1; k < Constants.NPLAYERS; k++){
				int id = (me.getID()+k)%Constants.NPLAYERS;
				Player other = allPlayers.get(id);
				if(!mask[other.getID()]) continue;
				for(int i = 0; i < other.getNCards(); i++){
					Action hint = new Action(other.getHand().get(i), Action.HINT, other.getID(), i);
					nextStates.add(applyAction(hint));
				}
			}
		}
		
		//all the discard action (we can only discard if there is less hint than the max number)
		//one discard action for each card in the player's hand
		if(nHintsLeft < Constants.NMAXHINTS){
			for(int i = 0; i < me.getNCards(); i++){
				Action play = new Action(me.getHand().get(i), Action.DISCARD, me.getID(), i);
				nextStates.add(applyAction(play));
			}
		}
		return nextStates;
	}
	
	@Override
	public Player getMe(){
		return me;
	}
	
	@Override
	public ArrayList<Player> getOtherPlayers() {
		return otherPlayers;
	}
	
	@Override
	public ArrayList<Player> getAllPlayers(){
		return allPlayers;
	}
	
	@Override
	public Graveyard getGraveyard() {
		return graveyard;
	}
	@Override
	public Deck getDeck() {
		return deck;
	}
	
	@Override
	public Table getTable() {
		return table;
	}
	
	@Override
	public int getNHintsLeft() {
		return nHintsLeft;
	}
	
	@Override
	public int getNBoom() {
		return nBoom;
	}
	
	@Override
	public ArrayList<Card> getDangers(){
		return dangers;
	}
	
	@Override
	public Action getLastAction(){
		return lastAction;
	}

	@Override
	/**
	 * Check if the current state is the end of the game
	 * There are three game ending conditions : the players made a number of mistakes greater than 3,
	 * all the values of all the color have been played, 
	 * or the deck is empty and the current player is the player who drew the last card
	 * */
	public boolean isEOG() {
		if(nBoom >= Constants.NMAXBOOMS || this.table.score() == 25 || this.deck.countDown() == 0){
			return true;
		}
		return false;
	}

	@Override
	/**
	 * Set the mask so that the only cards we cannot know are the one of the current player.
	 * In deed when we use the applyAction function to an hypothetical state 
	 * (when the current player is trying to infer the sequence of actions of all the player), 
	 * the mask has more and more false values as the current player does not know his cards but also have 
	 * to take into account that the next player does not know his card either 
	 * when trying to predict his action.
	 */
	public void removeMask() {
		for(int i=0; i<mask.length; i++){
			mask[i] = i!=me.getID();
		}
	}
	
	@Override
	/**
	 * Check if the current state is certain, 
	 * that is to say, if the last action performed was an action whose results are well known
	 */
	public boolean isCertain(){
		return lastAction.isCertain();
	}
	
	/**
	 * The valuation function. Gives a score to the current state based on a heuristic 
	 * including the different elements of the game
	 */
	public int getStateScore(){
		int score = Constants.BONUS_CARD_ON_TABLE *table.score();
		score -= Constants.MALUS_CARD_ILLPLAYED *nBoom;
		score += Constants.BONUS_HINT *nHintsLeft;
		score -= Constants.MALUS_DANGEROUS_CARDS *dangers.size();
		score -= Constants.MALUS_ALL_DISCARDED_CARDS *dead.size();
		if ((this.getLastAction() != null) && (this.getLastAction().getCard().isKnown())){
			score -= Constants.MALUS_LAST_ACTION_ALREADY_GIVEN_HINT;
		}
		return score;
	}
	
	@Override
	/**
	 * Print a state
	 * */
	public String toString(){
		String s = "";
		String l = "";
		for(int i=0; i<Constants.NPLAYERS; i++){
			if(mask[i]){
				s += allPlayers.get(i).toString() + "||ID:" + allPlayers.get(i).getID() + "\n";
			}
			else{
				l = "";
				for(Card card : allPlayers.get(i).getHand()){
					if(card.isKnown()){
						l += card.toString() + " ";
					}
					else{
						l += "**" + " ";
					}
				}
				s += l + "||ID:" + allPlayers.get(i).getID();
				if(me.getID()==i){
					s += "||Its me Mario!";
				}
				s += "\n";
			}
		}
		s += Constants.SEP + "\n";
		s += table.toString() + "\n";
		s += Constants.SEP + "\n";
		s += graveyard.toString() + "\n";
		l = "";
		for(Card card: dangers){
			l += card.toString() + " ";
		}
		s += l + "||DANGER" + "\n";
		if(lastAction!=null){
			s += lastAction.toString() + "\n";
		}
		String m = "";
		for(boolean b: mask){
			m += b + " ";
		}
		s += m + "\n";
		s += "score:" + table.score() + " countdown:" + deck.countDown() + "\n";
		s += "stateScore:" + this.getStateScore() + " hints:" + nHintsLeft + "\n";
		return s;
	}
	
	/**
	 * The forward search function, returns the sequence of action that should 
	 * lead to the best state according to the current player. 
	 * The first element is the action that the current player will perform.
	 **/
	public ArrayList<Action> forwardSearch(){
		ArrayList<Hanabi> possibleStates = this.findPossibleActions();
		int score = this.getStateScore();
		Path bestPath = new Path(Integer.MIN_VALUE, new ArrayList<Action>()); //the best path in the states tree
		for (Hanabi state : possibleStates){
			ArrayList<Action> actions = new ArrayList<Action>();
			actions.add(state.getLastAction());
			Path newPath = state.maxValue(1, score, actions);
			if (newPath.getScore() > bestPath.getScore()){
				bestPath = new Path(newPath.getScore(), new ArrayList<Action>(newPath.getActions()));
			}
		}
		return bestPath.getActions();
	}
	
	public Path maxValue(int depth, int score, ArrayList<Action> actions){
		if (!this.isCertain()){
			if(lastAction.getType() == Action.PLAY) {
				//we played an card without knowing about it
				return new Path(score -50 + lastAction.getCardID(), actions); 
			}
			else if(lastAction.getType() == Action.DISCARD){
				//we discarded a card without knowing about it
				return new Path(score -10 - lastAction.getCardID(), actions);
			}
		}
		if (this.isEOG()){
			if (table.score() == 25){
				return new Path(Integer.MAX_VALUE, actions);
			} else if (this.deck.countDown() == 0){
				return new Path(table.score() - 10*this.nHintsLeft, actions);
			} else{
				return new Path(Integer.MIN_VALUE + table.score(), actions);
			}
		}
		int newScore = this.getStateScore();
		if (depth > 3){
			//we limit our search to a depth 3, because we do not have enough information on the state in the following turns
			return new Path(newScore, actions);
		}
		
		ArrayList<Hanabi> possibleStates = this.findPossibleActions();
		Path bestPath = new Path(Integer.MIN_VALUE, new ArrayList<Action>());
		for (Hanabi state : possibleStates){
			ArrayList<Action> newActions = new ArrayList<Action>(actions);
			newActions.add(state.getLastAction());
			Path newPath = state.maxValue(depth+1, newScore, newActions);
			if (newPath.getScore() > bestPath.getScore()){
				bestPath = new Path(newPath.getScore(), new ArrayList<Action>(newPath.getActions()));
			}
		}
		return bestPath;
	}
	
	public String demo(){
		String s = "";
		String l = "";
		for(int i=0; i<Constants.NPLAYERS; i++){
			if(mask[i]){
				s += "   Player " + allPlayers.get(i).getID() + " : ";
				s += allPlayers.get(i).toString();
			}
			else{
				if(me.getID()==i){
					s += "-> ";
				}
				else{
					s += "   ";
				}
				l = "Player " + allPlayers.get(i).getID() + " : ";
				for(Card card : allPlayers.get(i).getHand()){
					if(card.isKnown()){
						l += card.toString() + " ";
					}
					else{
						l += "__" + " ";
					}
				}
				s += l;	
			}
			if(allPlayers.get(i).getHand().size() < 4){
				s += "   ";
			}
			if(i == 0){
				s += "|| " + table.printTable();
			}
			else if(i == 1){
				s += "|| " + "HINTS : " + nHintsLeft;
			}
			else if(i == 2){
				s += "|| " + "TRASH : " + graveyard.toString();
			}
			else if(i == 3){
				s += "|| " + "SCORE : " + table.score();
			}
			
			s += "\n";
			
		}
		return s;
	}
	
}
