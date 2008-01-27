/**
 * 
 */
package compilador;
import java.util.Vector;
/**
 * @author DaNieLooP
 *
 */
public class ListaID {
	private Vector listaID;
	
	/**
	 * Generamos una lista con una capacidad inicial de 100 elementos. De momento vamos a hacer esta lista estatica
	 * puesto que no vamos a crear programas muy grandes.
	 */
	public ListaID(){
		Vector listaID = new Vector(100);
	}
	
	public void añadeID(String lex){
		listaID.add(lex);
	}
	
	public boolean esVacia(){
		return listaID.isEmpty();
	}
	
	public String primero(){
		return (String) listaID.firstElement();
	}
	
	public void eliminaPrimero(){
		listaID.removeElementAt(0);
	}
}
