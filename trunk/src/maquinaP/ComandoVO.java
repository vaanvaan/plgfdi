package maquinaP;

/**
 * 
 * @author PLG GRUPO Nº13
 * 
 * Clase que nos proporciona Value Objects para poder procesar las instrucciones
 * de la maquina P.
 * 
 */
public class ComandoVO {
	private String accion;
	private String operando;

	/**
	 * Obtiene la acción de la instrucción
	 * 
	 * @return la acción que debe realizar la máquina P
	 */
	public String getAccion() {
		return accion;
	}

	/**
	 * Fija la acción
	 * 
	 * @param accion
	 *            nombre de la acción
	 */
	public void setAccion(String accion) {
		this.accion = accion;
	}

	/**
	 * Obtiene el operando
	 * 
	 * @return el operando asociado a la acción
	 */
	public String getOperando() {
		return operando;
	}

	/**
	 * Fija el operando de la acción
	 * 
	 * @param operando
	 *            el operando correspondiente.
	 */
	public void setOperando(String operando) {
		this.operando = operando;
	}
}
