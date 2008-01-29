package compilador;

public class Tupla {
	private Object[] tupla;
	public Tupla(int n){
		tupla = new Object[n];
	}
	public void setnTupla(int n,Object b){
		tupla[n]=b;
	}
	public Object getnTupla(int n){
		return tupla[n];
	}
}
