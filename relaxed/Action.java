/**
 * 
 * Store here every information needed to apply an action
 *
 */
public class Action implements ActionInterface{
	private Card card;
	private int type;
	private int playerID;
	private int cardID;
	public static int PLAY = 0;
	public static int HINT = 1;
	public static int DISCARD = 2;
	
	/**
	 * Constructor
	 * @param card Targeted card
	 * @param type Type of action
	 * @param playerID Targeted player index in allPlayers (self if PLAY or DISCARD)
	 * @param cardID Index of card in player hand
	 */
	public Action(Card card, int type, int playerID, int cardID){
		this.card = card;
		this.type = type;
		this.playerID = playerID;
		this.cardID = cardID;
	}
	
	@Override
	public Card getCard() {
		return card;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public int getPlayerID() {
		return playerID;
	}

	@Override
	public int getCardID() {
		return cardID;
	}
	
	/**
	 * if the played/discarded card is known, return true
	 */
	@Override
	public boolean isCertain(){
		if(type==PLAY || type==DISCARD){
			if(!card.isKnown()){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString(){
		String s = "";
		switch(type){
		case 0:
			s = "playerID:" + playerID + " PLAY " + "cardID:" + cardID + " " + card.toString();	
			break;
		case 1:
			s = "playerID:" + playerID + " HINT " + "cardID:" + cardID + " " + card.toString();
			break;
		case 2:
			s = "playerID:" + playerID + " DISCARD " + "cardID:" + cardID + " " + card.toString();
			break;
		}
		return s;
	}
	
	public String demo(){
		String s = "";
		switch(type){
		case 0:
			s = "PLAY THIS CARD : " + card.toString();
			break;
		case 1:
			s = "GIVE INFORMATION TO PLAYER " + playerID + " ON THIS CARD : " + card.toString();
			break;
		case 2:
			s = "DISCARD THIS CARD : " + card.toString();
			break;
		}
		return s;
	}
	
}
