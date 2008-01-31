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
	 * Añade una entrada nueva en la tabla de símbolos.
	 * @param id Identificador del elemento. Es la clave de la tabla hash.
	 * @param tipoDec Variable (VAR) o constante (CONST).
	 * @param tipo Tipo del elemento (entero, booleano, real, caracter).
	 * @param valor String con el valor del identificador en ese momento.
	 * @return True si se añade con éxito, False si ya existe un elemento con la misma ID.
	 */
	private boolean añadeTS(String id, String tipoDec, String tipo,String valor){
		entradaTS aux = new entradaTS(id, tipoDec, tipo,valor, dir); 
		ts.put(id,aux);
		if (aux == null) return false;
		else {
			dir++;
			return true;
		}
	}
	
	public boolean añadeLista(ListaID lista,String tipoDec,String tipo, String valor){
		boolean auxB=true;
		while(!lista.esVacia() && auxB){
			String idAux = lista.primero();
			lista.eliminaPrimero();
			auxB = añadeTS(idAux,tipoDec,tipo,valor);
		}
		return auxB;
	}
	
	/** Función que mira si existe una variable o una constante en la tabla.
	 * @param id identificador, clave de la tabla
	 * @return devuelve true si existe ese identificador
	 */
	public boolean existeID(String id){
		return ts.containsKey(id);
	}
	
	
	/** Función que devuelve el tipo del identificador, si este existe.
	 * @param id identificador del que se quiere saber su tipo.
	 * @return devuelve un string con el tipo, o error si no existe ese identificador.
	 */
	/**
	private String tipoID(String id){
		if(existeID(id)){
			entradaTS aux = (entradaTS) getEntrada(id);
			return aux.getTipo();
		}else{
			return "error";
		}
	}
	**/
	
	/** Función que devuelve si el identificador es una constante o una variable, o error si no existe.
	 * @param id Nombre del identificador del cual se quiere saber si es variable o constante.
	 * @return Devuelve un string diciendo si es variable, constante, o error si no existe.
	 */
	public String tipoDecl(String id){
		if(existeID(id)){
			entradaTS aux = (entradaTS) getEntrada(id);
			return aux.getTipoDec();
		}else{
			return "error";
		}
	}
	
	/**
	 * Acceso a una entrada de la tabla de símbolos.
	 * @param id Identificador cuya entrada en la tabla queremos obtener.
	 * @return Entrada correspondiente. Null si no existe.
	 */
	private entradaTS getEntrada(String id){
		return (entradaTS) ts.get(id);
	}
	
	public String devuelveTipo(String id){
		entradaTS aux = (entradaTS) getEntrada(id);
		return aux.getTipo();
	}
	
	/**
	private String devuelveTipoDecl(String id){
		entradaTS aux = (entradaTS) getEntrada(id);
		return aux.getTipoDec();
	}
	**/
	
	public int getDir(String id){
		entradaTS aux = (entradaTS) getEntrada(id);
		return aux.getDir();
	}
	
}
