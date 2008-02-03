package maquinaP;

/**
 * 
 * @author PLG GRUPO N�13
 * 
 * Clase que nos proporciona Value Objects para poder procesar las instrucciones
 * de la maquina P.
 * 
 */
public class ComandoVO {
	private String accion;
	private String operando;

	/**
	 * Obtiene la acci�n de la instrucci�n
	 * 
	 * @return la acci�n que debe realizar la m�quina P
	 */
	public String getAccion() {
		return accion;
	}

	/**
	 * Fija la acci�n
	 * 
	 * @param accion
	 *            nombre de la acci�n
	 */
	public void setAccion(String accion) {
		this.accion = accion;
	}

	/**
	 * Obtiene el operando
	 * 
	 * @return el operando asociado a la acci�n
	 */
	public String getOperando() {
		return operando;
	}

	/**
	 * Fija el operando de la acci�n
	 * 
	 * @param operando
	 *            el operando correspondiente.
	 */
	public void setOperando(String operando) {
		this.operando = operando;
	}
}
