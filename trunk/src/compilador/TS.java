/**
 * 
 */
package compilador;
import java.util.Hashtable;

/**Clase que representa una tabla de simbolos.
 * 
 * @author yiropa
 *
 */
public class TS {
	/**
	 * Tabla de símbolos implementada como tabla hash.
	 * Clave: ID.
	 * Objeto asociado: entradaTS. 
	 */
	@SuppressWarnings("unchecked")
	private Hashtable ts;
	
	/**
	 * Siguiente dirección vacía en la tabla.
	 */
	private int dir;
	
	
	/**
	 * Constructora. Inicializa la tabla.
	 */
	@SuppressWarnings("unchecked")
	public TS(){
		ts = new Hashtable(20);
		dir = 0;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param id
	 * @param clase
	 * @param props
	 * @return
	 */
	public boolean añadeTS(String id, String clase, Propiedades props){
		entradaTS aux = new entradaTS(id, clase, props);
		// aux.getProps().setDir(dir);
		ts.put(id,aux);
		if (aux == null) return false;
		else {
			//dir++;
			return true;
		}
	}
	
	
	/** Función que mira si existe una variable o una constante en la tabla.
	 * @param id identificador, clave de la tabla
	 * @return devuelve true si existe ese identificador
	 */
	public boolean existeID(String id){
		return ts.containsKey(id);
	}
	
	
	/**
	 * Acceso a una entrada de la tabla de símbolos.
	 * @param id Identificador cuya entrada en la tabla queremos obtener.
	 * @return Entrada correspondiente. Null si no existe.
	 */
	public entradaTS getEntrada(String id){
		return (entradaTS) ts.get(id);
	}
	

	
}
