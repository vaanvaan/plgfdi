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
	 * Entero que almacena el n�mero de linea en la que nos encontramos por si hay que lanzar
	 * alg�n error.
	 */
	private int numLinea;
	/**
	 * Cadena de caracteres que contiene el lexema de la cadena analizada.
	 */
	private String lex;
	/**
	 * Estado en el que se encuentra el analizador l�xico.
	 */
	private int estado;
	/**
	 * Posici�n en la que se encuentra el analizador l�xico dentro de la cadena a analizar.
	 */
	private int pos;
	/**
	 * Codigo asignado al lexema si se ha reconocido como v�lido. P.E. asig para la cadena ':='.
	 */
	private String token;
	/** Constructor del analizador lexico.
	 * 
	 * @param file Nombre del fichero cuyo lexico va a ser analizado.
	 */
	public AnalizadorLexico(String file){
		this.archivo = AuxFun.getTextoFichero(file);
		this.archivo = this.archivo.toLowerCase();
		this.numLinea = 1;
		this.lex = "";
		this.token = "";
		this.estado = 0;
		this.pos = 0;
	}
	
	public void transita(int state){
		this.lex = lex.concat(archivo.substring(pos,1));
		this.pos++;
		this.estado = state;
		
	}
	public void token(String lexema){
		if(lexema == "("){
			this.token = "lparen";
		}else if(lexema == ")"){
			this.token = "rparen";
		}else if(lexema == ":"){
			this.token = "dosp";
		}else if(lexema == ";"){
			this.token = "semip";
		}else if(lexema == "+"){
			this.token = "suma";
		}else if(lexema == "-"){
			this.token = "resta";
		}else if(lexema == "*"){
			this.token = "prod";
		}else if(lexema == "div"){
			this.token = "div";
		}else if(lexema == "mod"){
			this.token = "mod";
		}else if(lexema == "/"){
			this.token = "DivReal";
		}else if(lexema == "="){
			this.token = "eq";
		}else if(lexema == "<>"){
			this.token = "ne";
		}else if(lexema == ">"){
			this.token = "gt";
		}else if(lexema == ">="){
			this.token = "ge";
		}else if(lexema == "<"){
			this.token = "lt";
		}else if(lexema == "<="){
			this.token = "le";
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
