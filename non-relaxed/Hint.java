import java.util.ArrayList;

public class Hint {
	
	Colors color;
	Values value;
	boolean anti;
	boolean certain;
	
	public Hint(Colors color, boolean anti){
		this.color = color;
		this.value = Values.Unknown;
		this.anti = anti;
		this.certain = true;
	}
	
	public Hint(Values value, boolean anti){
		this.color = Colors.Unknown;
		this.value = value;
		this.anti = anti;
		this.certain = true;
	}
	
	public Hint(Hint hint, boolean anti){
		this.color = hint.getColor();
		this.value = hint.getValue();
		if(anti) this.anti = !hint.getAnti();
		else this.anti = hint.getAnti();
		this.certain = true;
	}
	
	public Hint(Colors color, boolean anti, boolean certain){
		this.color = color;
		this.value = Values.Unknown;
		this.anti = anti;
		this.certain = certain;
	}
	
	public Hint(Values value, boolean anti, boolean certain){
		this.color = Colors.Unknown;
		this.value = value;
		this.anti = anti;
		this.certain = certain;
	}
	
	public Colors getColor(){
		return color;
	}
	
	public Values getValue(){
		return value;
	}
	
	public boolean getAnti(){
		return anti;
	}
	
	public boolean getCertain(){
		return certain;
	}
}
