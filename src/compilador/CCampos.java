package compilador;

/**
 * @author DaNieLooP
 *
 */
public class CCampos {
	private String id;
	private String tipo;
	private int desp;
	
	/**
	 * @param id
	 * @param tipo
	 * @param desp
	 */
	public CCampos(String id, String tipo, int desp) {
		this.id = id;
		this.tipo = tipo;
		this.desp = desp;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the desp
	 */
	public int getDesp() {
		return desp;
	}

	/**
	 * @param desp the desp to set
	 */
	public void setDesp(int desp) {
		this.desp = desp;
	}
	
	
	
}
