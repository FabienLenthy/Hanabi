
public enum Values {
	One(1), Two(2), Three(3), Four(4), Five(5), Unknown(0);
	
	private int value;
	
	private Values(int v){
		this.value = v;
	}
	public Values next(){
		if(value > 0 && value < 5){
			return int2Values(value+1);
		}
		return this;
	}
	public Values int2Values(int v){
		switch(v){
		case 1 : return One;
		case 2 : return Two;
		case 3 : return Three;
		case 4 : return Four;
		case 5 : return Five;
		default : return Unknown;
		}
	}
	public int getIntValue(){
		return value;
	}
	
	public boolean isBelow(Values v){
		return v.getIntValue()>=value;
	}
	
	public String toString(){
		return "" + value;
	}
	
}
