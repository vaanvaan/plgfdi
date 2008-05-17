/**
 * 
 */
package compilador;

/**
 * Clase que se encarga de analizar los tokens de un archivo y proporcionarselos al analizador sintactico.
 * 
 * @author DaNieLooP
 *
 */
public class AnalizadorLexico {
	/**
	 * String que almacena el fichero de donde se leen los componentes lexicos.
	 */
	private String archivo;
	/**
	 * Cadena de caracteres que contiene el lexema de la cadena analizada.
	 */
	private String lex;
	/**
	 * Estado en el que se encuentra el analizador lï¿½xico.
	 */
	private int estado;
	/**
	 * Codigo asignado al lexema si se ha reconocido como vï¿½lido. P.E. asig para la cadena ':='.
	 */
	private String token;
	/** Constructor del analizador lexico.
	 * 
	 * @param file Nombre del fichero cuyo lexico va a ser analizado.
	 */
	public AnalizadorLexico(String file){
		try{
			this.archivo = AuxFun.getTextoFichero(file);
			// FIXME hay que cambiar el lower case y hacerlo en otro sitio para que funcione char
			this.archivo = this.archivo.toLowerCase()+'\0';
			Global.inicializa();
			this.lex = "";
			this.token = "";
			this.estado = 0;
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	/** Funcion que cambia el estado del analizador lexico, ademas de actualizar el lexema y la posicion
	 * de lectura del fichero.
	 * 
	 * @param state Nuevo estado al que pasa el analizador lï¿½xico.
	 */
	public void transita(int state){
		this.lex = lex + archivo.charAt(Global.getGlobalPos()); 
		Global.aumentaCol();
		Global.aumenteGlobalPos();
		this.estado = state;
	}
	
	/** Funcion que a partir de un lexema dado, devuelve el token asociado.
	 * 
	 * @param lexema Lexema a partir del cual se obtiene el token.
	 */
	public void token(String lexema){
		if(lexema.compareTo("(")==0){
			this.token = "lparen";
		}else if(lexema.compareTo(")")==0){
			this.token = "rparen";
		}else if(lexema.compareTo(":")==0){
			this.token = "dosp";
		}else if(lexema.compareTo(";")==0){
			this.token = "semip";
		}else if(lexema.compareTo("+")==0){
			this.token = "suma";
		}else if(lexema.compareTo("-")==0){
			this.token = "resta";
		}else if(lexema.compareTo("*")==0){
			this.token = "multiplica";
		}else if(lexema.compareTo("div")==0){
			this.token = "divide";
		}else if(lexema.compareTo("mod")==0){
			this.token = "modulo";
		}else if(lexema.compareTo("/")==0){
			this.token = "divide_real";
		}else if(lexema.compareTo("=")==0){
			this.token = "igual";
		}else if(lexema.compareTo("<>")==0){
			this.token = "distintos";
		}else if(lexema.compareTo(">")==0){
			this.token = "mayor";
		}else if(lexema.compareTo(">=")==0){
			this.token = "mayor_igual";
		}else if(lexema.compareTo("<")==0){
			this.token = "menor";
		}else if(lexema.compareTo("<=")==0){
			this.token = "menor_igual";
		}else if(lexema.compareTo(".")==0){
			this.token = "punto";
		}else if(lexema.compareTo(",")==0){
			this.token = "coma";
		}else if(lexema.compareTo("and")==0){
			this.token = "ylogica";
		}else if(lexema.compareTo("or")==0){
			this.token = "ologica";
		}else if(lexema.compareTo("boolean")==0){
			this.token = lexema;
		}else if(lexema.compareTo("integer")==0){
			this.token = lexema;
		}else if(lexema.compareTo("false")==0){
			this.token = lexema;
		}else if(lexema.compareTo("true")==0){
			this.token = lexema;
		}else if(lexema.compareTo("begin")==0){
			this.token = lexema;
		}else if(lexema.compareTo("end")==0){
			this.token = lexema;
		}else if(lexema.compareTo("program")==0){
			this.token = lexema;
		}else if(lexema.compareTo("read")==0){
			this.token = lexema;
		}else if(lexema.compareTo("write")==0){
			this.token = lexema;
		}else if(lexema.compareTo("var")==0){
			this.token = lexema;
		}else if(lexema.compareTo("not")==0){
			this.token = lexema;
		}else if(lexema.compareTo("real")==0){
			this.token = lexema;
		}else if(lexema.compareTo("char")==0){
			this.token = lexema;
		}else if(lexema.compareTo("const")==0){
			this.token = lexema;
		}else if(lexema.compareTo("if")==0){
			this.token = lexema;
		}else if(lexema.compareTo("then")==0){
			this.token = lexema;
		}else if(lexema.compareTo("else")==0){
			this.token = lexema;
		}else if(lexema.compareTo("while")==0){
			this.token = lexema;
		}else if(lexema.compareTo("do")==0){
			this.token = lexema;
		}else if(lexema.compareTo("new")==0){
			this.token = lexema;
		}else if(lexema.compareTo("dispose")==0){
			this.token = lexema;
		}else if(lexema.compareTo("array")==0){
			this.token = lexema;
		}else if(lexema.compareTo("of")==0){
			this.token = lexema;
		}else if(lexema.compareTo("record")==0){
			this.token = lexema;
		}else if(lexema.compareTo("nil")==0){
			this.token = lexema;
		}else if(lexema.compareTo("function")==0){
			this.token = lexema;
		}else if(lexema.compareTo("result")==0){
			this.token = lexema;
		}else if(lexema.compareTo("procedure")==0){
			this.token = lexema;
		}else if(lexema.compareTo("^")==0){
			this.token = lexema;
		}else if(lexema.compareTo("[")==0){
			this.token = "lcorch";
		}else if(lexema.compareTo("]")==0){
			this.token = "rcorch";
		}
		else this.token = "identificador";
	}
	
	/**
	 * Funcion que analiza un componente lexico del texto dado y devuelve su lexema.
	 */
	public void scanner()throws Exception{
		this.lex ="";
		this.token="";
		this.estado = 0;
		boolean encontrado = false;
		/**
		 * Bucle dentro del cual hay una serie de casos, donde se simula un automata finito
		 * determinista.
		 */
		while(!encontrado){
 			char buf=' ';
			if(Global.getGlobalPos()<this.archivo.length()){
				buf = this.archivo.charAt(Global.getGlobalPos());
			}
			switch(this.estado){
				case 0:
					if(buf=='\n'){
						Global.aumentaNumLinea();
						Global.setColumna(0);
						this.transita(0);
						this.lex = "";
					}else if(buf== '\r' || buf == '\t' || buf == ' '){
						this.transita(0);
						this.lex = "";
					}else if((buf>='a'&&buf<='z')||buf=='ñ'){
						this.transita(1);
					}else if(buf=='\''){
						this.transita(2);
					}else if(buf=='0'){
						this.transita(5);
					}else if(buf>='1'&&buf<='9'){
						this.transita(6);
					}else if(buf==':'){
						this.transita(9);
					}else if(buf==','||buf=='.'||buf=='+' || buf == '-' || buf =='*' || buf == '/'||buf=='('||buf==')'||buf==';'||buf=='='||buf=='^'||buf=='['||buf==']'){
						this.transita(10);
					}else if(buf=='>'){
						this.transita(11);
					}else if(buf=='<'){
						this.transita(12);
					}else if(buf=='\0'){
						this.transita(13);
					}else{
						Global.setErrorMsg("Error léxico");
						throw new Exception("Error lexico" 
								+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
					}
					break;
				case 1:
					if((buf>='a'&&buf<='z')||buf=='ñ'||(buf>='0'&&buf<='9')){
						this.transita(1);
					}else{
						encontrado=true;
						this.token(this.lex);
					}
					break;
				case 2:
					if((buf>='a'&&buf<='z')||buf=='ñ'||(buf>='0'&&buf<='9')){
						this.transita(3);
					}else{
						Global.setErrorMsg("Error léxico");
						throw new Exception("Error lexico" 
								+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
					}
					break;
				case 3:
					if(buf=='\''){
						this.transita(4);
					}else{
						Global.setErrorMsg("Error léxico");
						throw new Exception("Error lexico" 
								+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
					}
					break;
				case 4:
					encontrado=true;
					this.token = "char";
					break;
				case 5:
					if(buf=='.'||buf=='e'){
						this.transita(7);
					}else{
						encontrado = true;
						this.token = "num";
					}
					break;
				case 6:
					if(buf>='0'&&buf<='9'){
						this.transita(6);
					}else if(buf=='.'||buf=='e'){
						this.transita(7);
					}else{
						encontrado = true;
						this.token = "num";
					}
					break;
				case 7:
					if(buf>='0'&&buf<='9'){
						this.transita(8);
					}else{
						Global.setErrorMsg("Error léxico");
						throw new Exception("Error lexico" 
								+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
					}
					break;
				case 8:
					if(buf>='0'&&buf<='9'){
						this.transita(8);
					}else{
						encontrado = true;
						this.token = "numReal";
					}
					break;
				case 9:
					if(buf =='='){
						this.transita(10);
					}else{
						encontrado = true;
						this.token(this.lex);
					}
					break;
				case 10:
					encontrado = true;
					this.token(this.lex);
					break;
				case 11:
					if(buf =='='){
						this.transita(10);
					}else{
						encontrado = true;
						this.token(this.lex);
					}
					break;
				case 12:
					if(buf =='=' || buf=='>'){
						this.transita(10);
					}else{
						encontrado = true;
						this.token(this.lex);
					}
					break;
				case 13:
					encontrado = true;
					this.token = "stop";
			}
		}
	}
	
	/**
	 * Realiza la misma funcion que scanner() pero no avanza, permitiendo predecir el siguiente token sin consumirlo.
	 */
	public void predice() throws Exception{
		this.lex ="";
		this.token="";
		this.estado = 0;
		int posAux = Global.getGlobalPos();
		int colAux = Global.getColumna();
		
		boolean encontrado = false;
		/**
		 * Bucle dentro del cual hay una serie de casos, donde se simula un automata finito
		 * determinista.
		 */
		while(!encontrado){
 			char buf=' ';
			if(Global.getGlobalPos()<this.archivo.length()){
				buf = this.archivo.charAt(Global.getGlobalPos());
			}
			switch(this.estado){
				case 0:
					if(buf=='\n'){
						this.transita(0);
						this.lex = "";
					}else if(buf== '\r' || buf == '\t' || buf == ' '){
						this.transita(0);
						this.lex = "";
					}else if((buf>='a'&&buf<='z')||buf=='ñ'){
						this.transita(1);
					}else if(buf=='\''){
						this.transita(2);
					}else if(buf=='0'){
						this.transita(5);
					}else if(buf>='1'&&buf<='9'){
						this.transita(6);
					}else if(buf==':'){
						this.transita(9);
					}else if(buf==','||buf=='.'||buf=='+' || buf == '-' || buf =='*' || buf == '/'||buf=='('||buf==')'||buf==';'||buf=='='){
						this.transita(10);
					}else if(buf=='>'){
						this.transita(11);
					}else if(buf=='<'){
						this.transita(12);
					}else if(buf=='\0'){
						this.transita(13);
					}else{
						Global.setErrorMsg("Error léxico");
						throw new Exception("Error lexico" 
								+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
					}
					break;
				case 1:
					if((buf>='a'&&buf<='z')||buf=='ñ'||(buf>='0'&&buf<='9')){
						this.transita(1);
					}else{
						encontrado=true;
						Global.setGlobalPos(posAux);
						Global.setColumna(colAux);
						this.token(this.lex);
					}
					break;
				case 2:
					if((buf>='a'&&buf<='z')||buf=='ñ'||(buf>='0'&&buf<='9')){
						this.transita(3);
					}else{
						Global.setErrorMsg("Error léxico");
						throw new Exception("Error lexico" 
								+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
					}
					break;
				case 3:
					if(buf=='\''){
						this.transita(4);
					}else{
						Global.setErrorMsg("Error léxico");
						throw new Exception("Error lexico" 
								+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
					}
					break;
				case 4:
					encontrado=true;
					Global.setGlobalPos(posAux);
					Global.setColumna(colAux);
					this.token = "char";
					break;
				case 5:
					if(buf=='.'||buf=='e'){
						this.transita(7);
					}else{
						encontrado = true;
						Global.setGlobalPos(posAux);
						Global.setColumna(colAux);
						this.token = "num";
					}
					break;
				case 6:
					if(buf>='0'&&buf<='9'){
						this.transita(6);
					}else if(buf=='.'||buf=='e'){
						this.transita(7);
					}else{
						encontrado = true;
						Global.setGlobalPos(posAux);
						Global.setColumna(colAux);
						this.token = "num";
					}
					break;
				case 7:
					if(buf>='0'&&buf<='9'){
						this.transita(8);
					}else{
						Global.setErrorMsg("Error léxico");
						throw new Exception("Error lexico" 
								+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
					}
					break;
				case 8:
					if(buf>='0'&&buf<='9'){
						this.transita(8);
					}else{
						encontrado = true;
						Global.setGlobalPos(posAux);
						Global.setColumna(colAux);
						this.token = "numReal";
					}
					break;
				case 9:
					if(buf =='='){
						this.transita(10);
					}else{
						encontrado = true;
						Global.setGlobalPos(posAux);
						Global.setColumna(colAux);
						this.token(this.lex);
					}
					break;
				case 10:
					encontrado = true;
					Global.setGlobalPos(posAux);
					Global.setColumna(colAux);
					this.token(this.lex);
					break;
				case 11:
					if(buf =='='){
						this.transita(10);
					}else{
						encontrado = true;
						Global.setGlobalPos(posAux);
						Global.setColumna(colAux);
						this.token(this.lex);
					}
					break;
				case 12:
					if(buf =='=' || buf=='>'){
						this.transita(10);
					}else{
						encontrado = true;
						Global.setGlobalPos(posAux);
						Global.setColumna(colAux);
						this.token(this.lex);
					}
					break;
				case 13:
					encontrado = true;
					Global.setGlobalPos(posAux);
					Global.setColumna(colAux);
					this.token = "fin";
			}
		}
	}
	

	/**
	 * @return the lex
	 */
	public String getLex() {
		return lex;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			AnalizadorLexico analizer = new AnalizadorLexico("c:/prueba.txt");
			for(;;){
				//analizer.scanner();
				System.out.println("SCANNER");
				System.out.print("Lexema: ");
				System.out.print(analizer.lex);
				System.out.print("  token: ");
				System.out.println(analizer.token);
				//analizer.predice();
				System.out.println("PREDICE");
				System.out.print("Lexema: ");
				System.out.print(analizer.lex);
				System.out.print("  token: ");
				System.out.println(analizer.token);
			}
	}
	
}
