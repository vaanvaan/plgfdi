/**
 * 
 */
package compilador;

import java.util.ArrayList;

/**
 * @author DaNieLooP
 *
 */
public class PilaTS {
	
	private ArrayList<TS> stackTS;
	private int cima;
	
	/**
	 * Constructor de PilaTS que crea una pila vacía.
	 */
	public PilaTS(){
		stackTS = new ArrayList<TS>();
		cima = -1;
	}
	
	/**
	 * Crea una nueva TS por encima de la que habia antes, y el marcador cima aumenta(para saber
	 * el nº de TS que hay en la pila de TS).
	 */
	public void creaTS(){
		TS tsAux = new TS();
		stackTS.add(tsAux);
		cima++;
	}
	
	/**
	 * 
	 * @param level Nivel donde se quiere añadir el ID.
	 * @param id Parte que identifica unívocamente a la variable, constante o procedimiento.
	 * @param clase Define si se trata de una variable o parámetro por referencia(var),
	 *  constante (const), un procedimiento (proc), o parámetro por valor (pvar).
	 * @param props Contiene una tupla de propiedades referentes a la declaración.
	 */
	public void añadeID(int level,String id,String clase,Propiedades props){
		TS aux = (TS)stackTS.get(level);
		aux.añadeTS(id, clase, props);
	}
	
	public TS getTSnivel(int level){
		TS aux = null;
		if (level<=cima){
			aux = stackTS.get(level);
		}
		return aux;
	}

}
