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
	 * Tabla de s�mbolos implementada como tabla hash.
	 * Clave: ID.
	 * Objeto asociado: entradaTS. 
	 */
	@SuppressWarnings("unchecked")
	private Hashtable ts;
	
	/**
	 * Siguiente direcci�n vac�a en la tabla.
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
	public boolean a�adeTS(String id, String clase, Propiedades props){
		entradaTS aux = new entradaTS(id, clase, props);
		// aux.getProps().setDir(dir);
		ts.put(id,aux);
		if (aux == null) return false;
		else {
			//dir++;
			return true;
		}
	}
	
	
	/** Funci�n que mira si existe una variable o una constante en la tabla.
	 * @param id identificador, clave de la tabla
	 * @return devuelve true si existe ese identificador
	 */
	public boolean existeID(String id){
		return ts.containsKey(id);
	}
	
	
	/**
	 * Acceso a una entrada de la tabla de s�mbolos.
	 * @param id Identificador cuya entrada en la tabla queremos obtener.
	 * @return Entrada correspondiente. Null si no existe.
	 */
	public entradaTS getEntrada(String id){
		return (entradaTS) ts.get(id);
	}
	

	
}
