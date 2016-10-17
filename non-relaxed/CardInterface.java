import java.util.ArrayList;

public interface CardInterface {
	public Values getValue();
	public Colors getColor();
	public void update(Table table, Graveyard graveyard, ArrayList<Player> allPlayers, boolean[] mask);
	public boolean isKnown();
	public boolean equals(Object card);
	public void knowledge(Hint hint);
	//public void infer();
	public String toString();
}
