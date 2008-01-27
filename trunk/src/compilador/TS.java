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
	 * Tabla de s�mbolos implementada como tabla hash.
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
	 * A�ade una entrada nueva en la tabla de s�mbolos.
	 * @param id Identificador del elemento. Es la clave de la tabla hash.
	 * @param tipoDec Variable (VAR) o constante (CONST).
	 * @param tipo Tipo del elemento (entero, booleano, real, caracter).
	 * @param valor String con el valor del identificador en ese momento.
	 * @param dir Direcci�n de la memoria de datos donde se encuentra el valor del elemento.
	 * @return True si se a�ade con �xito, False si ya existe un elemento con la misma ID.
	 */
	public boolean a�adeTS(String id, String tipoDec, String tipo,String valor,int dir){
		entradaTS aux = (entradaTS) ts.put(new entradaTS(id, tipoDec, tipo,valor, dir), id);
		if (aux == null) return false;
		else return true;
	}
	
	public boolean a�adeLista(ListaID lista,String tipoDec,String tipo,int dir){
		boolean auxB=true;
		while(!lista.esVacia() || !auxB){
			String idAux = lista.primero();
			lista.eliminaPrimero();
			auxB = a�adeTS(idAux,tipoDec,tipo,"",dir);
			dir++;
		}
		return auxB;
	}
	
	/** Funci�n que mira si existe una variable o una constante en la tabla.
	 * @param id identificador, clave de la tabla
	 * @return devuelve true si existe ese identificador
	 */
	public boolean existeID(String id){
		return ts.containsKey(id);
	}
	
	/** Funci�n que devuelve el tipo del identificador, si este existe.
	 * @param id identificador del que se quiere saber su tipo.
	 * @return devuelve un string con el tipo, o error si no existe ese identificador.
	 */
	public String tipoID(String id){
		if(existeID(id)){
			entradaTS aux = (entradaTS) getEntrada(id);
			return aux.getTipo();
		}else{
			return "error";
		}
	}
	
	/** Funci�n que devuelve si el identificador es una constante o una variable, o error si no existe.
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
	 * Acceso a una entrada de la tabla de s�mbolos.
	 * @param id Identificador cuya entrada en la tabla queremos obtener.
	 * @return Entrada correspondiente. Null si no existe.
	 */
	public entradaTS getEntrada(String id){
		return (entradaTS) ts.get(id);
	}
	
}
