import java.util.ArrayList;

public class Hanabi implements HanabiInterface{
	private ArrayList<Player> allPlayers;
	private Player me;
	private ArrayList<Player> otherPlayers;
	private boolean[] mask;
	private Graveyard graveyard;
	private Deck deck;
	private Table table;
	private int nHintsLeft;
	private int nBoom;
	private ArrayList<Card> dangers;
	private ArrayList<Card> dead;
	private Action lastAction;
	private int hintScore;
	
	public Hanabi(ArrayList<Player> allPlayers, Player me,ArrayList<Player> otherPlayers, boolean[] mask, 
			Graveyard graveyard, Deck deck, Table table, int nHintsLeft, int nBoom, ArrayList<Card> dangers,
			ArrayList<Card> dead, Action lastAction, int hintScore){
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
		this.hintScore = hintScore;
	}
	
	@Override
	public Hanabi applyAction(Action action) {
		
		ArrayList<Player> newAllPlayers = new ArrayList<Player>();
		Player newMe = new Player(me);
		for(int p = 0; p < Constants.NPLAYERS; p++){
			Player player;
			if(p == me.getID()) player = newMe;
			else player = new Player(allPlayers.get(p));
			newAllPlayers.add(player);
		}
		/*for(int i=0; i<allPlayers.size(); i++){
			if(i==newMe.getID()) newAllPlayers.add(newMe);
			else newAllPlayers.add(new Player(allPlayers.get(i)));
		}
		allPlayers = newAllPlayers;*/
		ArrayList<Player> newOtherPlayers = new ArrayList<Player>();
		for(int k=1; k<Constants.NPLAYERS; k++){
			int id = (me.getID()+k)%Constants.NPLAYERS;
			newOtherPlayers.add(new Player(newAllPlayers.get(id)));
		}
		boolean[] newMask = new boolean[Constants.NPLAYERS];
		for(int i=0; i<mask.length; i++){
			newMask[i] = mask[i];
		}
		newMask[newMe.getID()] = false;
		Graveyard newGraveyard = new Graveyard(graveyard);
		Deck newDeck = new Deck(deck);
		if (newDeck.isEmpty()){
			newDeck.decrease();
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
		int newHintScore = 0;
		Card card = action.getCard();
		int type = action.getType();
		int cardID = action.getCardID();
		int playerID = action.getPlayerID();
		if(type == Action.PLAY){
			boolean isPlayed = false;
			for(int i = 0; i < Constants.NCOLORS; i++){
				if(table.getWantedCards().get(i).equals(card)){
					if(card.getValue() == Values.Five){
						newNHintsLeft++;
					}
					newTable.addCard(card);
					newMe.leaveCard(cardID);
					newMe.drawCard(newDeck);
					isPlayed = true;
					/* update dangers */
					newDangers.remove(card);
					break;
				}
			}
			if(!isPlayed){
				newNBoom++;
				newMe.leaveCard(cardID);
				newMe.drawCard(newDeck);
				newGraveyard.add(card);
				/* update dangers */
				if(card.getValue()!=Values.One && !newDangers.contains(card) &&
						!table.contains(card)){
					newDangers.add(card);
				}
				if(card.getValue()==Values.One && newGraveyard.contains(card) &&
						!newDangers.contains(card)){
					newDangers.add(card);
				}
				if(dangers.contains(card)) {
					newDead.add(card);
				}
			}
		}
		else if(type == Action.HINT_COLOR){
			Hint hint = new Hint(card.getColor(), false);
			newHintScore = newAllPlayers.get(playerID).getHintScore(hint, newTable, newGraveyard, newAllPlayers, mask);
			Player newPlayer = newAllPlayers.get(playerID);
			newPlayer.takeHint(hint, newTable);
			newAllPlayers.set(playerID, newPlayer);
			newNHintsLeft--;
		}
		else if(type == Action.HINT_VALUE){
			Hint hint = new Hint(card.getValue(), false);
			newHintScore = newAllPlayers.get(playerID).getHintScore(hint, newTable, newGraveyard, newAllPlayers, mask);
			Player newPlayer = newAllPlayers.get(playerID);
			newPlayer.takeHint(hint,newTable);
			newAllPlayers.set(playerID, newPlayer);
			newNHintsLeft--;
		}
		else if(type == Action.DISCARD){
			newGraveyard.add(card);
			newMe.leaveCard(cardID);
			newMe.drawCard(newDeck);
			newNHintsLeft++;
			/* update dangers */
			if(card.getValue()!=Values.One && !newDangers.contains(card) &&
					!table.contains(card)){
				newDangers.add(card);
			}
			if(card.getValue()==Values.One && newGraveyard.contains(card) &&
					!newDangers.contains(card)){
				newDangers.add(card);
			}
			if(newDangers.contains(card)) {
				newDead.add(card);
				newDangers.remove(card);
			}
		}
		newMe = new Player(newAllPlayers.get((me.getID()+1)%Constants.NPLAYERS));
		return new Hanabi(newAllPlayers, newMe, newOtherPlayers, newMask, newGraveyard, 
				newDeck, newTable, newNHintsLeft, newNBoom, newDangers, newDead, action, newHintScore);
	}
	
	@Override
	public ArrayList<Hanabi> findPossibleActions() {
		ArrayList<Hanabi> nextStates = new ArrayList<Hanabi>();
		
		ArrayList<Player> newAllPlayers = new ArrayList<Player>();
		for(int p = 0; p < Constants.NPLAYERS; p++){
			Player player;
			if(p == me.getID()) player = me;
			else player = new Player(allPlayers.get(p));
			player.update(table, graveyard, allPlayers, mask);
			newAllPlayers.add(player);
		}
		allPlayers = newAllPlayers;
		if(isEOG()) return nextStates;
		for(int i = 0; i < me.getNCards(); i++){
			Action play = new Action(new Card(me.getHand().get(i)), Action.PLAY, me.getID(), i);
			nextStates.add(applyAction(play));
		}
		if(nHintsLeft > 0){
			for(int k = 1; k < Constants.NPLAYERS; k++){
				int id = (me.getID()+k)%Constants.NPLAYERS;
				Player other = allPlayers.get(id);
				if(!mask[other.getID()]) continue;
				for(int i = 0; i < other.getNCards(); i++){
					Action giveHintColor = new Action(new Card(other.getHand().get(i)), Action.HINT_COLOR, other.getID(), i);
					Action giveHintValue = new Action(new Card(other.getHand().get(i)), Action.HINT_VALUE, other.getID(), i);
					nextStates.add(applyAction(giveHintColor));
					nextStates.add(applyAction(giveHintValue));
				}
			}
		}
		if(nHintsLeft < Constants.NMAXHINTS){
			for(int i = 0; i < me.getNCards(); i++){
				Action play = new Action(new Card(me.getHand().get(i)), Action.DISCARD, me.getID(), i);
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
	
	public boolean[] getMask(){
		boolean[] newMask = new boolean[4];
		for(int i = 0; i < 4; i++){
			newMask[i] = mask[i];
		}
		return newMask;
	}

	@Override
	public boolean isEOG() {
		if(nBoom >= Constants.NMAXBOOMS || this.table.score() == 25 || this.deck.countDown() == 0){
			return true;
		}
		return false;
	}

	@Override
	public void removeMask() {
		for(int i=0; i<mask.length; i++){
			mask[i] = i!=me.getID();
		}
	}
	
	@Override
	public boolean isCertain(){
		return lastAction.isCertain();
	}
	
	public int getStateScore(){
		int score = 1000*table.score();
		score -= Math.pow(100,nBoom);
		score += nHintsLeft;
		score -= 10*dangers.size();
		for(Card card : dead){
			score -= (6-100*card.getValue().getIntValue());
		}
		return score;
	}
	
	public int getBonusScore(Hanabi oldState, int depth){
		if (lastAction != null){
			if((lastAction.getType() == Action.HINT_COLOR || 
				(lastAction.getType() == Action.HINT_VALUE))){
				if(hintScore == 0 || 
						lastAction.getCard().isDiscardable(oldState.getTable()) || 
						lastAction.getCard().isPlayable(oldState.getTable())) return -100;
				int bonus = 0;
				int knowledge = lastAction.getCard().getPossibleCards().getKnowledgeCoef();
				//knowledge == 1 -> value is known -> we want lastAction.getType() = ACTION.HINT_COLOR = 1
				//knowledge == 2 -> color is known -> we want lastAction.getType() = ACTION.HINT_VALUE = 2
				if(oldState.getTable().getWantedCards().contains(lastAction.getCard()) && 
						(knowledge == lastAction.getType() || knowledge == 0))
					bonus = 100;
				return bonus + hintScore;
			}
			else if(lastAction.getType() == Action.PLAY) {
				Card card = new Card(lastAction.getCard());
				Hint previousHint = oldState.getHint();
				if(previousHint != null) card.knowledge(previousHint);
				card.update(oldState.getTable(), oldState.getGraveyard(), oldState.getAllPlayers(), oldState.getMask());
				if(lastAction.getCard().isPlayable(oldState.getTable())) {
					return (int)Math.pow(10,7-depth);
				}
				else {
					return -500;
				}
			}
			else if(lastAction.getType() == Action.DISCARD){
				Card card = new Card(lastAction.getCard());
				Hint previousHint = oldState.getHint();
				if(previousHint != null) card.knowledge(previousHint);
				card.update(oldState.getTable(), oldState.getGraveyard(), oldState.getAllPlayers(), oldState.getMask());
				if(card.isDiscardable(oldState.getTable())) {
					return 10;
				}
				else {
					return -20;
				}
			}
		}
		return 0;
	}
	
	public Hint getHint(){
		if(lastAction == null || lastAction.getType() == Action.PLAY || lastAction.getType() == Action.DISCARD) return null;
		Card targetCard = lastAction.getCard();
		if(lastAction.getType() == Action.HINT_COLOR){
			return new Hint(targetCard.getColor(), false);
		}
		else{
			return new Hint(targetCard.getValue(), false);
		}
	}
	
	@Override
	public String toString(){
		String s = "";
		if(lastAction!=null){
			s += lastAction.toString() + " ||" +  this.getStateScore() + "\n";
		}
		String l = "";
		for(int i=0; i<Constants.NPLAYERS; i++){
			if(mask[i]){
				s += allPlayers.get(i).toString() + "||ID:" + allPlayers.get(i).getID() + " knowledge:";
				for(Card card : allPlayers.get(i).getHand()){
					s += "*" + card.getPossibleCards().getKnowledgeCoef() + " ";
				}
				s += "\n";
			}
			else{
				l = "";
				for(Card card : allPlayers.get(i).getHand()){
					if(card.isKnown()){
						l += card.toString() + " ";
					}
					else{
						l += "*" + card.getPossibleCards().getKnowledgeCoef() + " ";
					}
				}
				s += l + "||ID:" + allPlayers.get(i).getID() + " knowledge:";
				for(Card card : allPlayers.get(i).getHand()){
					s += "*" + card.getPossibleCards().getKnowledgeCoef() + " ";
				}
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
		String m = "";
		for(boolean b: mask){
			m += b + " ";
		}
		s += m + "\n";
		s += "score:" + table.score() + " countdown:" + deck.countDown();
		s += " hints:" + nHintsLeft + " boom:" + nBoom + "\n";
		return s;
	}
	
	public ArrayList<Action> forwardSearch(){
		ArrayList<Hanabi> possibleStates = this.findPossibleActions();
		int score = this.getStateScore();
		int bonus = 0;
		Path bestPath = new Path(Integer.MIN_VALUE, new ArrayList<Action>());
		for (Hanabi state : possibleStates){
			ArrayList<Action> actions = new ArrayList<Action>();
			actions.add(state.getLastAction());
			Path newPath = state.maxValue(1, score, actions, bonus, this);
			//System.err.println(state.getLastAction().toString() + " " + newPath.getScore());
			if (newPath.getScore() > bestPath.getScore()){
				bestPath = new Path(newPath.getScore(), newPath.getActions());
			}
		}
		return bestPath.getActions();
	}
	
	public Path maxValue(int depth, int score, ArrayList<Action> actions, int bonus, Hanabi oldState){
		Action newLastAction = actions.get(actions.size()-1);
		int newBonus = this.getBonusScore(oldState, depth);
		if (!newLastAction.isCertain()){
			/*if (deck.countDown() == 0){
				System.err.println(newLastAction.toString() + "||" + newBonus);
			}*/
			return new Path(score + newBonus + bonus - newLastAction.getCardID(), actions);
		}
		if (this.isEOG()){
			if (table.score() == 25){
				//System.err.println(newLastAction.toString() + "||" + newBonus);
				return new Path(Integer.MAX_VALUE, actions);
			} else if (deck.countDown() == 0){
				//System.err.println(newLastAction.toString() + "||" + newBonus);
				return new Path(table.score() + bonus + newBonus, actions);
			} else{
				//System.err.println(newLastAction.toString() + "||" + newBonus);
				return new Path(Integer.MIN_VALUE + table.score(), actions);
			}
		}
		int newScore = this.getStateScore() + bonus;
		if (depth > 3){
			return new Path(score + bonus, actions);
		}
		
		//System.err.println(depth + "||" + newLastAction + "||" + newScore);
		
		ArrayList<Hanabi> possibleStates = this.findPossibleActions();
		Path bestPath = new Path(Integer.MIN_VALUE, new ArrayList<Action>());
		for (Hanabi state : possibleStates){
			ArrayList<Action> newActions = new ArrayList<Action>();
			for(Action action : actions){
				newActions.add(action);
			}
			newActions.add(state.getLastAction());
			Path newPath = state.maxValue(depth+1, newScore, newActions, newBonus-100, this);
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
				s += "|| " + "HINTS : " + nHintsLeft + " || BOOMS : " + nBoom;
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
