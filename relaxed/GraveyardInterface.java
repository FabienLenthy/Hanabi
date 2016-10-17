import java.util.ArrayList;

public interface GraveyardInterface {
	public void add(Card card);
	public ArrayList<Card> getTrash();
	public String toString();
	public boolean contains(Card card);
}
