/**
 * 
 */
package compilador;
import java.util.Vector;

/**Clase que representa la estructura de una lista de identificadores.
 * 
 * @author DaNieLooP
 *
 */
public class ListaID {
	private Vector listaID;
	
	/**
	 * Generamos una lista con un tamaño creciente de 1 elemento cada vez que se añade otro.
	 */
	public ListaID(){
		listaID = new Vector(0,1);
	}
	
	public void añadeID(String lex){
		listaID.addElement(lex);
	}
	
	public boolean contiene(String lex){
		return listaID.contains(lex);
	} 
	
	public int nElementos(){
		return listaID.size();
	}
	
	public boolean esVacia(){
		return listaID.isEmpty();
	}
	
	public String elementoAt(int i){
		return (String)listaID.elementAt(i);
	}
	
	public String primero(){
		return (String) listaID.firstElement();
	}
	
	public void eliminaPrimero(){
		listaID.removeElementAt(0);
	}
	
	public int tamaño(){
		return listaID.size();
	}
}
