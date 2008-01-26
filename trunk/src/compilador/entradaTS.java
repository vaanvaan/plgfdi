/**
 * 
 */
package compilador;

/**
 * 
 * @author yiropa
 *
 */
public class entradaTS {
	
	private String id;
	private String tipoDec;
	private String tipo;
	private int dir;
	
	/**
	 * Constructora. Pide los datos para rellenar la entrada.
	 * @param id Identidicador.
	 * @param tipoDec Variable (VAR) o Constante (CONST).
	 * @param tipo Tipo del elemento (entero, booleano, real, caracter).
	 * @param dir Dirección de la memoria de datos donde se encuentra el valor del elemento. 
	 */
	public entradaTS(String id, String tipoDec, String tipo, int dir){
		this.id = id;
		this.tipoDec = tipoDec;
		this.tipo = tipo;
		this.dir = dir;
	}
	
	/**
	 * Acceso al identificador.
	 * @return ID
	 */
	public String getID(){
		return this.id;
	}
	
	/**
	 * Acceso al tipo de declaración.
	 * @return VAR o CONST
	 */
	public String getTipoDec(){
		return this.tipoDec;
	}
	
	/**
	 * Acceso al tipo de datos.
	 * @return entreo, booleano, real, caracter.
	 */
	public String getTipo(){
		return this.tipo;
	}
	
	/**
	 * Acceso a la dirección de memoria.
	 * @return dir
	 */
	public int getDir(){
		return this.dir;
		}
	
}
