package pruebas.Util;

public class Tuple<X, Y> {
	private X first;
	private Y second;

	public Tuple(X key, Y value) {
		this.first = key;
		this.second = value;
	}
	
	public void setFirst(X key){
		this.first = key; 
	}
	
	public X getFirst(){
		return first; 
	}
	
	public void setSecond(Y value){
		this.second = value; 
	}
	
	public Y getSecond(){
		return second; 
	}
}
