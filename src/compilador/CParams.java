/**
 * 
 */
package compilador;

/**
 * @author DaNieLooP
 *
 */
public class CParams {
	private String modo;
	private String tipo;
	private int dir;
	
	/**
	 * @param modo
	 * @param tipo
	 * @param dir
	 */
	public CParams(String modo, String tipo, int dir) {
		this.modo = modo;
		this.tipo = tipo;
		this.dir = dir;
	}

	/**
	 * @return the modo
	 */
	public String getModo() {
		return modo;
	}

	/**
	 * @param modo the modo to set
	 */
	public void setModo(String modo) {
		this.modo = modo;
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
	 * @return the dir
	 */
	public int getDir() {
		return dir;
	}

	/**
	 * @param dir the dir to set
	 */
	public void setDir(int dir) {
		this.dir = dir;
	}
	
	
}
