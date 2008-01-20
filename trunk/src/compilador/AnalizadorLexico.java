/**
 * 
 */
package compilador;

/**
 * @author DaNieLooP
 *
 */
public class AnalizadorLexico {
	/**
	 * String que almacena el fichero de donde se leen los componentes lexicos.
	 */
	private String archivo;
	/**
	 * Entero que almacena el número de linea en la que nos encontramos por si hay que lanzar
	 * algún error.
	 */
	private int numLinea;
	/**
	 * Cadena de caracteres que contiene el lexema de la cadena analizada.
	 */
	private String lex;
	/**
	 * Estado en el que se encuentra el analizador léxico.
	 */
	private int estado;
	/**
	 * Posición en la que se encuentra el analizador léxico dentro de la cadena a analizar.
	 */
	private int pos;
	
	
	/** Constructor del analizador lexico.
	 * 
	 * @param file Nombre del fichero cuyo lexico va a ser analizado.
	 */
	public AnalizadorLexico(String file){
		this.archivo = AuxFun.getTextoFichero(file);
		this.numLinea = 1;
		this.lex = "";
		this.estado = 0;
		this.pos = 0;
	}
	
	public void transita(int state){
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
