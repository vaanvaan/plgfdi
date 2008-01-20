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
	/**
	 * Codigo asignado al lexema si se ha reconocido como válido. P.E. asig para la cadena ':='.
	 */
	private String token;
	/** Constructor del analizador lexico.
	 * 
	 * @param file Nombre del fichero cuyo lexico va a ser analizado.
	 */
	public AnalizadorLexico(String file){
		this.archivo = AuxFun.getTextoFichero(file);
		this.archivo = this.archivo.toLowerCase()+'\0';
		this.numLinea = 1;
		this.lex = "";
		this.token = "";
		this.estado = 0;
		this.pos = 0;
	}
	
	/** Función que cambia el estado del analizador lexico, ademas de actualizar el lexema y la posicion
	 * de lectura del fichero.
	 * 
	 * @param state Nuevo estado al que pasa el analizador léxico.
	 */
	public void transita(int state){
		this.lex = lex.concat(archivo.substring(pos,1));
		this.pos++;
		this.estado = state;
		
	}
	
	/** Función que a partir de un lexema dado, devuelve el token asociado.
	 * 
	 * @param lexema Lexema a partir del cual se obtiene el token.
	 */
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
	
	public void scanner(){
		this.lex ="";
		this.estado = 0;
		boolean encontrado = false;
		while(!encontrado){
			char buf = this.archivo.charAt(pos);
			if(buf=='\n'){
				this.pos++;
				this.numLinea++;
			}else if(buf== '\r' || buf == '\t' || buf == ' '){
				this.pos++;
			}
			switch(this.estado){
				case 0:
					if(buf=='\n'){
						this.numLinea++;
						this.transita(0);
						this.lex = "";
					}else if(buf== '\r' || buf == '\t' || buf == ' '){
						this.transita(0);
						this.lex = "";
					}else if((buf>='a'&&buf>='z')||buf=='ñ'){
						this.transita(1);
					}else if(buf=='\''){
						this.transita(3);
					}else if(buf=='0'){
						this.transita(6);
					}else if(buf>='1'&&buf<='9'){
						this.transita(7);
					}else if(buf==':'){
						this.transita(10);
					}else if(buf=='+' || buf == '-' || buf =='*' || buf == '/'||buf=='('||buf==')'||buf==';'||buf=='='){
						this.transita(8);
					}else if(buf=='>'){
						this.transita(12);
					}else if(buf=='<'){
						this.transita(13);
					}else{
						//aqui ira un error(0)
					}
					
					break;
				case 1:
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					break;
				case 8:
					break;
				case 9:
					break;
				case 10:
					break;
				case 11:
					break;
				case 12:
					break;
				case 13:
					break;
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			AnalizadorLexico analizer = new AnalizadorLexico("C:/prueba.txt");
	}

}
