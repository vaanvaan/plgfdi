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
		listaID = new Vector(100);
	}
	
	public void a�adeID(String lex){
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
	public void copiar(ListaID l){
		l = new ListaID();
		System.out.println(listaID.size());
		for(int i = 0;i<listaID.size();i++){
			l.a�adeID((String)listaID.elementAt(i));
		}
	}
}