public enum Colors {
	Red, Green, Blue, Yellow, White, Unknown;
	
	public String toString(){
		switch(this){
		case Red:
			return "R";
		case Green:
			return "G";
		case Blue:
			return "B";
		case Yellow:
			return "Y";
		case White:
			return "W";
		default:
			return "*";
		}
	}
	
	public String toFullString(){
		switch(this){
		case Red:
			return "RED";
		case Green:
			return "GREEN";
		case Blue:
			return "BLUE";
		case Yellow:
			return "YELLOW";
		case White:
			return "WHITE";
		default:
			return "*";
		}
	}
	
	
	public int toInt(){
		switch(this){
		case Red:
			return 0;
		case Green:
			return 1;
		case Blue:
			return 2;
		case Yellow:
			return 3;
		case White:
			return 4;
		default:
			return -1;
		}
	}
}
