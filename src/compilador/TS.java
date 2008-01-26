/**
 * 
 */
package compilador;
import java.util.Hashtable;
/**
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
	 * Constructora. Inicializa la tabla.
	 */
	@SuppressWarnings("unchecked")
	public TS(){
		ts = new Hashtable();
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Añade una entrada nueva en la tabla de símbolos.
	 * @param id Identificador del elemento. Es la clave de la tabla hash.
	 * @param tipoDec Variable (VAR) o constante (CONST).
	 * @param tipo Tipo del elemento (entero, booleano, real, caracter).
	 * @param dir Dirección de la memoria de datos donde se encuentra el valor del elemento.
	 * @return True si se añade con éxito, False si ya existe un elemento con la misma ID.
	 */
	public boolean añadeTS(String id, String tipoDec, String tipo, int dir){
		entradaTS aux = (entradaTS) ts.put(new entradaTS(id, tipoDec, tipo, dir), id);
		if (aux == null) return false;
		else return true;
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
