import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Tests {
	
	private Hanabi initState;

	public Tests(){
	}
	/**
	 * initialize every object of Hanabi
	 */
	public void init(){
		Table table = new Table();
		Deck deck = new Deck();
		Graveyard graveyard = new Graveyard();
		Player cybill = new Player(deck, 0);
		Player fabien = new Player(deck, 1);
		Player quentin = new Player(deck, 2);
		Player bastien = new Player(deck, 3);
		ArrayList<Player> allPlayers = new ArrayList<Player>();
		allPlayers.add(cybill);
		allPlayers.add(fabien);
		allPlayers.add(quentin);
		allPlayers.add(bastien);
		ArrayList<Player> otherPlayers = new ArrayList<Player>();
		otherPlayers.add(fabien);
		otherPlayers.add(quentin);
		otherPlayers.add(bastien);
		boolean[] mask = {false, true, true, true};
		ArrayList<Card> dangers = new ArrayList<Card>();
		dangers.add(new Card("5R"));
		dangers.add(new Card("5G"));
		dangers.add(new Card("5B"));
		dangers.add(new Card("5Y"));
		dangers.add(new Card("5W"));
		ArrayList<Card> dead = new ArrayList<Card>();
		this.initState = new Hanabi(allPlayers, cybill, otherPlayers, mask, graveyard, deck, table, Constants.NMAXHINTS, 0, dangers, dead, null,0);

	}
	
	public void init(String[] stringDeck){
		Table table = new Table();
		Deck deck = new Deck(stringDeck);
		Graveyard graveyard = new Graveyard();
		Player cybill = new Player(deck, 0);
		Player fabien = new Player(deck, 1);
		Player quentin = new Player(deck, 2);
		Player bastien = new Player(deck, 3);
		ArrayList<Player> allPlayers = new ArrayList<Player>();
		allPlayers.add(cybill);
		allPlayers.add(fabien);
		allPlayers.add(quentin);
		allPlayers.add(bastien);
		ArrayList<Player> otherPlayers = new ArrayList<Player>();
		otherPlayers.add(fabien);
		otherPlayers.add(quentin);
		otherPlayers.add(bastien);
		boolean[] mask = {false, true, true, true};
		ArrayList<Card> dangers = new ArrayList<Card>();
		dangers.add(new Card("5R"));
		dangers.add(new Card("5G"));
		dangers.add(new Card("5B"));
		dangers.add(new Card("5Y"));
		dangers.add(new Card("5W"));
		ArrayList<Card> dead = new ArrayList<Card>();
		this.initState = new Hanabi(allPlayers, cybill, otherPlayers, mask, graveyard, deck, table, Constants.NMAXHINTS, 0, dangers, dead, null,0);

	}
	
	/**
	 * print average score over nTimes iteration of game
	 * @param nTimes Number of iteration
	 * @return Average score
	 */
	public float averageScore(int nTimes){
		float sum = 0;
		for (int t = 0 ; t <nTimes ; t++){
			this.init();
			ArrayList<Action> newPath = this.initState.forwardSearch();
			Hanabi nextState = this.initState.applyAction(newPath.get(0));
			nextState.removeMask();
			while(!nextState.isEOG()){
				newPath = nextState.forwardSearch();
				nextState = nextState.applyAction(newPath.get(0));
				nextState.removeMask();
			}
			sum += nextState.getTable().score();
		}
		return sum/(float)nTimes;
	}
	
	/**
	 * print all states in a game
	 */
	public void printGame(){
		this.init();
		System.out.println(initState.demo());
		//Scanner scanner = new Scanner(System.in);
		//scanner.nextLine();
		
		ArrayList<Action> newPath = initState.forwardSearch();
		Action chosenAction = newPath.get(0);
		System.out.println("PLAYER " + initState.getMe().getID() + " CHOSE TO " + chosenAction.demo() );
		for(int i = 1; i < newPath.size(); i++){
			System.out.println("\tPLAYER " + initState.getMe().getID() + " EXPECTS PLAYER " + 
					+ (initState.getMe().getID() + i) % 4 + " TO " +  newPath.get(i).demo());
		}
		System.out.println("\n\n\n\n\n\n\n\n\n\n");
		
		Hanabi nextState = initState.applyAction(chosenAction);
		nextState.removeMask();
		
		while(!nextState.isEOG()){
			System.out.println(nextState.demo());
			
			newPath = nextState.forwardSearch();
			chosenAction = newPath.get(0);
			System.out.println("PLAYER " + nextState.getMe().getID() + " CHOSE TO " + chosenAction.demo());
			for(int i = 1; i < newPath.size(); i++){
				System.out.println("\tPLAYER " + nextState.getMe().getID() + " EXPECTS PLAYER " + 
			+ (nextState.getMe().getID() + i) % 4 + " TO " +  newPath.get(i).demo());
			}
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("\n\n\n\n\n\n\n\n\n\n");
			
			nextState = nextState.applyAction(newPath.get(0));
			nextState.removeMask();
		}
		System.out.println(nextState.demo());
		
		System.out.println();
	}
	
	/**
     * Print 4 best actions to do according to the first simulation from the state initState
     */
	public void printDecisionMaking(){
		this.init();
		ArrayList<Action> newPath = initState.forwardSearch();
		for(Action action : newPath){
			System.out.println(action.toString());
		}
		System.out.println();
	}
	
    /**
     * Test a set of parameters on 10 randomly shuffled decks
     * @return mean score
     */
	public float heuristicTestValues(int cardOnTable, int hint, int illPlayedCard, int dangerCard, int deadCards, int alreadyGiven){
		Constants.BONUS_CARD_ON_TABLE = cardOnTable;
		Constants.BONUS_HINT = hint;
		Constants.MALUS_CARD_ILLPLAYED = illPlayedCard;
		Constants.MALUS_DANGEROUS_CARDS = dangerCard;
		Constants.MALUS_ALL_DISCARDED_CARDS = deadCards;
		Constants.MALUS_LAST_ACTION_ALREADY_GIVEN_HINT = alreadyGiven;
		
		float score = this.averageScore(10);
		
		System.out.println("***Test Heuristic Parameters***");
		System.out.println("BONUS_CARD_ON_TABLE = " + cardOnTable);
		System.out.println("BONUS_HINT = " + hint);
		System.out.println("MALUS_CARD_ILLPLAYED = " + illPlayedCard);
		System.out.println("MALUS_DANGEROUS_CARDS = " + dangerCard);
		System.out.println("MALUS_ALL_DISCARDED_CARDS = " + deadCards);
		System.out.println("MALUS_LAST_ACTION_ALREADY_GIVEN_HINT = " + alreadyGiven);
		System.out.println("\t***Average score = " + score + "***\n");
		
		return score;
	}
	

	/**
     * Test many sets of parameters, print the best set
     */
	public void heuristicTests(){
		float bestScore = Integer.MIN_VALUE;
		int bestCardOnTable = 100;
		int bestHint = 1;
		int bestIllPlayedCard = 25;
		int bestDangerCard = 0;
		int bestDeadCards = 25;
		int bestAlreadyGiven = 10;
		for (int cardOnTable = 100 ; cardOnTable <= 200 ; cardOnTable += 50){
			for (int hint = 0; hint <= 10 ; hint += 5){
				for (int illPlayedCard = 50 ; illPlayedCard <= 150 ; illPlayedCard += 50){
					for (int dangerCard = 0 ; dangerCard <= 40 ; dangerCard +=20){
						for (int deadCards = 50 ; deadCards <= 150 ; deadCards += 50){
							for (int alreadyGiven = 0 ; alreadyGiven <=60 ;  alreadyGiven += 30){
								float score = heuristicTestValues(cardOnTable, hint, illPlayedCard, dangerCard, deadCards, alreadyGiven);
								if (score > bestScore){
									bestScore = score;
									bestCardOnTable = cardOnTable;
									bestHint = hint;
									bestIllPlayedCard = illPlayedCard;
									bestDangerCard = dangerCard;
									bestDeadCards = deadCards;
									bestAlreadyGiven = alreadyGiven;
								}
							}
						}
					}
				}
			}
		}
		
		System.out.println("\n******Best results*****");
		System.out.println("BONUS_CARD_ON_TABLE = " + bestCardOnTable);
		System.out.println("BONUS_HINT = " + bestHint);
		System.out.println("MALUS_CARD_ILLPLAYED = " + bestIllPlayedCard);
		System.out.println("MALUS_DANGEROUS_CARDS = " + bestDangerCard);
		System.out.println("MALUS_ALL_DISCARDED_CARDS = " + bestDeadCards);
		System.out.println("MALUS_LAST_ACTION_ALREADY_GIVEN_HINT = " + bestAlreadyGiven);
		System.out.println("\t***Average score = " + bestScore + "***\n");
	}
	
	public void demo(){
		this.init(Constants.demoDeck);
		System.out.println(initState.demo());
		//Scanner scanner = new Scanner(System.in);
		//scanner.nextLine();
		
		ArrayList<Action> newPath = initState.forwardSearch();
		Action chosenAction = newPath.get(0);
		System.out.println("PLAYER " + initState.getMe().getID() + " CHOSE TO " + chosenAction.demo() );
		for(int i = 1; i < newPath.size(); i++){
			System.out.println("\tPLAYER " + initState.getMe().getID() + " EXPECTS PLAYER " + 
					+ (initState.getMe().getID() + i) % 4 + " TO " +  newPath.get(i).demo());
		}
		System.out.println("\n\n\n\n\n\n\n\n\n\n");
		
		Hanabi nextState = initState.applyAction(chosenAction);
		nextState.removeMask();
		
		while(!nextState.isEOG()){
			System.out.println(nextState.demo());
			
			newPath = nextState.forwardSearch();
			chosenAction = newPath.get(0);
			System.out.println("PLAYER " + nextState.getMe().getID() + " CHOSE TO " + chosenAction.demo());
			for(int i = 1; i < newPath.size(); i++){
				System.out.println("\tPLAYER " + nextState.getMe().getID() + " EXPECTS PLAYER " + 
			+ (nextState.getMe().getID() + i) % 4 + " TO " +  newPath.get(i).demo());
			}
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("\n\n\n\n\n\n\n\n\n\n");
			
			nextState = nextState.applyAction(newPath.get(0));
			nextState.removeMask();
		}
		System.out.println(nextState.demo());
		
		System.out.println();
	}
	
}
