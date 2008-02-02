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
