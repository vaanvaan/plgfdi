/**
 * 
 */
package compilador;

/**Clase que representa a cada entrada de la tabla de simbolos.
 * 
 * @author yiropa
 *
 */
public class entradaTS {
	
	private String id;
	private String clase;
	private Propiedades props;
	
	/**
	 * 
	 * @param id Parte que identifica unívocamente a la variable, constante o procedimiento.
	 * @param clase Define si se trata de una variable o parámetro por referencia(var),
	 *  constante (const), un procedimiento (proc), o parámetro por valor (pvar).
	 * @param props Contiene una tupla de propiedades referentes a la declaración.
	 */
	public entradaTS(String id, String clase,Propiedades props){
		this.id = id;
		this.clase = clase;
		this.props = props;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the clase
	 */
	public String getClase() {
		return clase;
	}

	/**
	 * @return the props
	 */
	public Propiedades getProps() {
		return props;
	}

}
