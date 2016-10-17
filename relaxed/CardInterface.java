
public interface CardInterface {
	public Values getValue();
	public Colors getColor();
	public boolean isKnown();
	public boolean equals(Object card);
	public void knowledge();
	//public void infer();
	public String toString();
}
