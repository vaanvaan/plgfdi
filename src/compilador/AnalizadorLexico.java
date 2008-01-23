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
		this.archivo = this.archivo.toLowerCase()+'\0';
		Global.inicializa();
		this.lex = "";
		this.token = "";
		this.estado = 0;
		this.pos = 0;
	}
	
	/** Funci�n que cambia el estado del analizador lexico, ademas de actualizar el lexema y la posicion
	 * de lectura del fichero.
	 * 
	 * @param state Nuevo estado al que pasa el analizador l�xico.
	 */
	public void transita(int state){
		this.lex = lex + archivo.charAt(pos); 
		this.pos++;
		this.estado = state;
		
	}
	public void esReservada(String lexema){
		if(lexema.compareTo("boolean")==0||lexema.compareTo("integer")==0||
				lexema.compareTo("false")==0||lexema.compareTo("true")==0||
				lexema.compareTo("begin")==0||lexema.compareTo("end")==0||
				lexema.compareTo("program")==0||lexema.compareTo("read")==0||
				lexema.compareTo("write")==0||lexema.compareTo("var")==0||
				lexema.compareTo("and")==0||lexema.compareTo("or")==0||
				lexema.compareTo("not")==0||lexema.compareTo("div")==0||
				lexema.compareTo("mod")==0||lexema.compareTo("real")==0||
				lexema.compareTo("char")==0||lexema.compareTo("const")==0){
			this.token = "reserved";
		}else{
			this.token = "identificador";
		}
	}
	/** Funci�n que a partir de un lexema dado, devuelve el token asociado.
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
			this.token = "prod";
		}else if(lexema.compareTo("div")==0){
			this.token = "div";
		}else if(lexema.compareTo("mod")==0){
			this.token = "mod";
		}else if(lexema.compareTo("/")==0){
			this.token = "DivReal";
		}else if(lexema.compareTo("=")==0){
			this.token = "eq";
		}else if(lexema.compareTo("<>")==0){
			this.token = "ne";
		}else if(lexema.compareTo(">")==0){
			this.token = "gt";
		}else if(lexema.compareTo(">=")==0){
			this.token = "ge";
		}else if(lexema.compareTo("<")==0){
			this.token = "lt";
		}else if(lexema.compareTo("<=")==0){
			this.token = "le";
		}else if(lexema.compareTo(".")==0){
			this.token = "punto";
		}else if(lexema.compareTo(",")==0){
			this.token = "coma";
		}
	}
	
	/**
	 * Funcion que analiza un componente lexico del texto dado y devuelve su lexema.
	 */
	public void scanner(){
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
			if(pos<this.archivo.length()){
				buf = this.archivo.charAt(pos);
			}
			switch(this.estado){
				case 0:
					if(buf=='\n'){
						Global.aumentaNumLinea();
						this.transita(0);
						this.lex = "";
					}else if(buf== '\r' || buf == '\t' || buf == ' '){
						this.transita(0);
						this.lex = "";
					}else if((buf>='a'&&buf<='z')||buf=='�'){
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
						Error.error();
					}
					break;
				case 1:
					if((buf>='a'&&buf<='z')||buf=='�'||(buf>='0'&&buf<='9')){
						this.transita(1);
					}else{
						encontrado=true;
						this.esReservada(this.lex);
					}
					break;
				case 2:
					if((buf>='a'&&buf<='z')||buf=='�'||(buf>='0'&&buf<='9')){
						this.transita(3);
					}else{
						Error.error();
					}
					break;
				case 3:
					if(buf=='\''){
						this.transita(4);
					}else{
						Error.error();
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
						Error.error();
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
					this.token = "fin";
					System.exit(0);
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			AnalizadorLexico analizer = new AnalizadorLexico("/home/danieloop/prueba.txt");
			for(;;){
				analizer.scanner();
				System.out.print("Lexema: ");
				System.out.print(analizer.lex);
				System.out.print("  token: ");
				System.out.println(analizer.token);
			}
	}

}
