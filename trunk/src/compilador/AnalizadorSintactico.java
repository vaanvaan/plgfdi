/**
 * 
 */
package compilador;

import java.util.Vector;
import java.io.*;

/**
 * Clase que se encarga de ver si un programa es sintacticamente correcto dentro de nuestro subconjunto de Pascal, y 
 * genera las instrucciones necesarias para ejecutarlo despues en la maquina P.
 * 
 * @author DaNieLooP
 *
 */
public class AnalizadorSintactico {
	/**
	 * Instancia del analizador lexico utilizada en el analizador sintactico.
	 */
	private AnalizadorLexico anaLex;
	/**
	 * Tabla de dispersion que hace las veces de tabla de simbolos.
	 */
	private TS tablaSim;
	/**
	 * Vector que almacena el conjunto de instrucciones emitidas hasta el momento.
	 */
	private Vector instrucciones;
	/**
	 * String con el path del archivo que se tiene que escribir.
	 */
	private String pathDestino;
	
	/**Constructor del analizador sintactico, donde se crea el analizador lexico y la tabla de simbolos. 
	 * 
	 * @param path Ruta del archivo que se va a analizar.
	 */
	public AnalizadorSintactico(String path,String path2){
		anaLex = new AnalizadorLexico(path);
		tablaSim = new TS();
		instrucciones = new Vector(0,1);
		pathDestino = path2;
	}
	
	
	/**Método que reconoce un programa completo de Pascal de nuestro subconjunto.
	 * 
	 * @throws Exception Se recoge el error de cualquiera de las funciones que componen el programa.
	 */
	public void programa() throws Exception{
		this.compara("program");
		this.id(); 
		this.compara(";");
		this.declaraciones();
		this.proposicion_compuesta(); 
		this.compara(".");
		this.anaLex.scanner();
		this.emite(this.anaLex.getToken());
		if(Global.getError()){
			// TODO ha habido error
		}else{
			try
	        {
	            FileWriter fichero = new FileWriter(pathDestino);
	            PrintWriter pw = new PrintWriter(fichero);
	            for (int i = 0; i < instrucciones.size(); i++)
	                pw.println(instrucciones.elementAt(i));
	            pw.close();
	        } catch (Exception e)
	        {
	            e.printStackTrace();
	        }
		}
	}
	
	/**
	 * Método que reconoce la parte de las declaraciones de un programa escrito en Pascal.
	 * 
	 * @throws Exception Se recoge cualquier error lanzado dentro de las declaraciones.
	 */
	private void declaraciones() throws Exception{
		Tupla decs = this.declaracion(); // devuelve listaID, tipoDec, tipo, valor
		this.compara(";");
		tablaSim.añadeLista((ListaID)decs.getnTupla(0), (String)decs.getnTupla(1), 
							(String)decs.getnTupla(2), (String)decs.getnTupla(3));
		
		// Ahora añadimos código-P si hemos declarado una constante
		String tipoDec = (String) decs.getnTupla(1); 
		if (tipoDec.compareTo("const")==0){
			this.emite("apila "+ decs.getnTupla(3));
			String id = (String) (decs.getnTupla(4));
			this.emite("desapila_dir "+ this.tablaSim.getDir(id));
		}
		this.declaracionesR();
	}
	
	/**
	 * Método que reconoce una parte de las declaraciones de un programa escrito en Pascal. Es distinto de declaraciones()
	 * para evitar la recursion a izquierdas.
	 * 
	 * @throws Exception Se recoge cualquier error lanzado dentro de esta parte de las declaraciones.
	 */
	private void declaracionesR() throws Exception{
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0||this.anaLex.getLex().compareTo("const")==0){  
			Tupla t1 = this.declaracion();
			this.compara(";");
			tablaSim.añadeLista((ListaID)t1.getnTupla(0), (String)t1.getnTupla(1), 
					(String)t1.getnTupla(2), (String)t1.getnTupla(3));
			
			// Ahora añadimos código-P si hemos declarado una constante
			String tipoDec = (String) t1.getnTupla(1); 
			if (tipoDec.compareTo("const")==0){
				this.emite("apila "+ t1.getnTupla(3));
				String id = (String) (t1.getnTupla(4));
				this.emite("desapila_dir "+ this.tablaSim.getDir(id));
			}
			this.declaracionesR();
		}
	}
	
	/**Método que reconoce una declaracion, ya sea constante o variable.
	 * 
	 * @return Devuelve una tupla con los elementos de la declaracion para que pueda ser añadida a la tabla de simbolos.
	 * 
	 * @throws Exception Recoge cualquier error generado dentro de la declaracion.
	 */
	private Tupla declaracion() throws Exception{
		Tupla dec = new Tupla(5);
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0){
			Tupla var = this.variable(); // Devuelve listaID, tipo
			dec.setnTupla(0, var.getnTupla(0));
			dec.setnTupla(1, "var");
			dec.setnTupla(2, var.getnTupla(1));
			dec.setnTupla(3, "");
		}else if(this.anaLex.getLex().compareTo("const")==0){
			Tupla cons = this.constante(); // Devuelve listaID, tipo, valor
			dec.setnTupla(0, cons.getnTupla(0));
			dec.setnTupla(1, "const");
			dec.setnTupla(2, cons.getnTupla(1));
			dec.setnTupla(3, cons.getnTupla(2));
			dec.setnTupla(4, cons.getnTupla(3));
		}else{
			throw new Exception("Error sintaxis: No hay declaraciones" 
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}
		return dec;
	}
	
	/**Método que se encarga de reconocer una constante.
	 * 
	 * @return Se devuelve una tupla que se ira pasando de un metodo a otro y al final se añadira a la tabla de simbolos.
	 * 
	 * @throws Exception Se encarga de recoger cualquier error ocurrido en el reconocimiento de la constante.
	 */
	private Tupla constante() throws Exception{
		Tupla t = new Tupla(4);
		this.compara("const");
		this.id();
		String lex0=this.anaLex.getLex();
		if (this.tablaSim.existeID(lex0)) 
			throw new Exception("Error sintaxis: ID ya existente"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');		
		this.compara(":");
		String tipo1 = this.tipo();
		this.compara("=");
		String valor = this.valor(tipo1);
		ListaID listaID = new ListaID();
		listaID.añadeID(lex0);
		t.setnTupla(0, listaID);
		t.setnTupla(1, tipo1);
		t.setnTupla(2, valor);
		t.setnTupla(3, lex0);
		return t;
	}
	
	/**Método que se encarga de reconocer una variable.
	 * 
	 * @return Se devuelve una tupla que se ira pasando de un metodo a otro y al final se añadira a la tabla de simbolos.
	 * 
	 * @throws Exception Se encarga de recoger cualquier error ocurrido en el reconocimiento de la variable.
	 */
	private Tupla variable() throws Exception{
		Tupla t = new Tupla(2);
		this.compara("var");
		ListaID listaID=this.lista_id();
		this.compara(":");
		String tipo1 = this.tipo();
		t.setnTupla(0, listaID);
		t.setnTupla(1, tipo1);
		return t;
	}
	
	/**Método que reconoce una lista de identificadores.
	 * 
	 * 
	 * @return se devuelve una lista con todos los lexemas de los identificadores.
	 * 
	 * @throws Exception Se encarga de recoger cualquier error ocurrido en el reconocimiento de una lista de identificadores.
	 */
	private ListaID lista_id() throws Exception{
		this.id();
		String lex = this.anaLex.getLex();
		if (this.tablaSim.existeID(lex)) 
			throw new Exception("Error sintaxis: ID ya existente"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		ListaID listaID = new ListaID();
		listaID.añadeID(lex);
		return this.lista_idR(listaID);
	}
	
	/**Método que reconoce parte de una secuencia de identificadores. Se creo para evitar la recursion a izquierdas en
	 * el metodo lista_id
	 * 
	 * @param listaIDh0 Lista que se pasa por parametro para agregar mas identificadores.
	 * @return Se devuelve una lista con los lexemas de los identificadores.
	 * @throws Exception Se recoge cualquier tipo de error ocurrido dentro de esta parte de la lista de identificadores.
	 */
	private ListaID lista_idR(ListaID listaIDh0) throws Exception{
		this.anaLex.predice();
		String aux = this.anaLex.getToken();
		if(aux.compareTo("coma")==0){
			String lex;
			this.compara(",");
			this.id();
			lex = this.anaLex.getLex();
			if (this.tablaSim.existeID(lex)) 
				throw new Exception("Error sintaxis: ID ya existente"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			listaIDh0.añadeID(lex);
			return this.lista_idR(listaIDh0);
		}else{
			return listaIDh0;
		}
	}
	
	/**
	 * Método que se encarga de reconocer el tipo de una variable o una constante.
	 * 
	 * @return Se devuelve un string con el tipo reconocido
	 * @throws Exception Se genera una excepcion si hay algun tipo incorrecto.
	 */
	private String tipo() throws Exception{
		String tipo="";
		this.anaLex.scanner();
		String lexTipo = anaLex.getLex();
		if(lexTipo.compareTo("integer")==0){
			tipo = "integer";
		}else if(lexTipo.compareTo("boolean")==0){
			tipo = "boolean";
		}else if(lexTipo.compareTo("real")==0){
			tipo = "real";
		}else if(lexTipo.compareTo("char")==0){
			tipo = "char";
		}else{
			//throw new Exception("Error sintaxis: Tipo incorrecto.");
			Global.setErrorMsg("Violación restricciones. Tipo incorrecto");
		}
		return tipo;
	}
	
	/**Método que sirve para reconocer el valor de una constante.
	 * 
	 * @param tipo Se pasa por parametro el tipo para ver si concuerda con el valor.
	 * @return Se devuelve un String con el valor de la constante.
	 * @throws Exception Se genera una excepcion si el tipo y el valor de una constante no concuerdan.
	 */
	private String valor(String tipo) throws Exception{
		anaLex.scanner();
		String token = anaLex.getToken();
		String lex = anaLex.getLex();
		if(token.compareTo("num")==0 && tipo.compareTo("integer")==0){
			return lex; 
		}else if((lex.compareTo("true")==0 || lex.compareTo("false")==0)
					&& tipo.compareTo("boolean")==0){
			return lex;
		}else if(token.compareTo("numReal")==0 && tipo.compareTo("real")==0){
			return lex;
		}else if(token.compareTo("char")==0 && tipo.compareTo("char")==0){
			return lex;
		}else{
			//throw new Exception("Error sintaxis: valor no corresponde con tipo.");
			Global.setErrorMsg("Violación restricciones. Valor y Tipo incompatibles");
			return tipo;
		}
	}

// FIN PARTE DECLARACIONES
	
	/**Método que reconoce la parte de las instrucciones de un programa escrito en Pascal.
	 * 
	 * @throws Exception Se recoge cualquier tipo de error generado dentro de esta parte del programa.
	 */
	private void proposicion_compuesta() throws Exception{
		this.compara("begin");
		this.proposiciones_optativas();
		this.compara("end"); 
	}
	
	/**Método que reconoce si en la parte de las instrucciones hay instrucciones o no.
	 * 
	 * @throws Exception Se recoge cualquier tipo de error que haya ocurrido en la parte de las instrucciones del programa.
	 */
	private void proposiciones_optativas() throws Exception{
		this.anaLex.predice();
		String lex = this.anaLex.getLex();
		if (lex.compareTo("end")!=0){
			// Si no es programa vacío, seguimos.
			this.lista_proposiciones();
		}
	}
	
	/**Método que reconoce todas las proposiciones que conforman las instrucciones del programa.
	 * 
	 * @throws Exception Se recoge cualquier error generado dentro de cualquier proposicion.
	 */
	private void lista_proposiciones() throws Exception{
		this.proposicion();
		this.compara(";");
		this.lista_proposicionesR();
	}
	
	/**Método que reconoce parte de las proposiciones que conforman las instrucciones del programa.
	 * Se creo para evitar la recursion a izquierdas en lista_proposiciones()
	 * 
	 * @throws Exception Se recoge cualquier error generado dentro de cualquier proposicion.
	 */
	private void lista_proposicionesR() throws Exception{
		this.anaLex.predice();
		String lexTipo = anaLex.getLex();
		if(lexTipo.compareTo("end")!=0){
			// Ya no hay más proposiciones.
			this.lista_proposiciones();
		}
	}
	
	/**Método que reconoce cualquier tipo de proposicion (instruccion) del subconjunto de Pascal.
	 * 
	 * @throws Exception Se recogen errores de niveles inferiores de la jerarquia y se lanzan otros si hay algun tipo de asignacion incorrecta.
	 */
	private void proposicion() throws Exception{
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		String lexTipo = anaLex.getLex();
		if(lexToken.compareTo("identificador")==0){
			this.id();
			String lex = this.anaLex.getLex();
			this.compara(":=");
			String tipo = this.expresion(); // Devuelve tipo
			if (!this.tablaSim.existeID(lex) 
					|| !this.compatibles(this.tablaSim.devuelveTipo(lex), tipo) 
					|| this.tablaSim.tipoDecl(lex)!= "var"){
				//throw new Exception("Error sintaxis: Asignación incorrecta.");
				Global.setErrorMsg("Violación restricciones. Asignación incorrecta");
			}
			this.emite("desapila_dir "+this.tablaSim.getDir(lex));
		}else if(lexTipo.compareTo("read")==0){
			this.compara("read");
			this.compara("(");
			this.id();
			String lex = this.anaLex.getLex();
			if (!this.tablaSim.existeID(lex)) 
				throw new Exception("Error sintaxis: ID no declarado"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			this.compara(")");
			this.emite("apila_dir "+ this.tablaSim.getDir(lex));
			this.emite("read");
		}else if(lexTipo.compareTo("write")==0){
			this.compara("write");
			this.compara("(");
			this.expresion();
			this.compara(")");
			this.emite("write");
		}else{
			// Si lo siguiente no empieza por "begin" ya dará error.
			this.proposicion_compuesta();
		}
	}
	
	/**Método que reconoce una expresion.
	 * 
	 * @return Devuelve un string del token analizado.
	 * @throws Exception Se recoge cualquier tipo de error que haya ocurrido en cualquier metodo de los niveles
	 * inferiores de la jerarquia.
	 */
	private String expresion() throws Exception{
		String t="";
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("char")==0){
			t = "char";
			this.anaLex.scanner();
			this.emite("apila " + anaLex.getLex());
		}else{
			t=this.exp_simple();
			t=this.expresionR(t);
		}
		return t;
	}
	
	/**Método que reconoce una parte de una expresion. Se creo para evitar la recursion a izquierdas.
	 * 
	 * @param tipo Se pasa por parametro el tipo para ver si concuerda con el de la expresion.
	 * @return Se devuelve un string de la expresion analizada.
	 * @throws Exception Se genera un error si hay una violacion de tipos.
	 */
	private String expresionR(String tipo) throws Exception {
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("igual")==0 || lexToken.compareTo("distinto")==0 || lexToken.compareTo("mayor")==0
			|| lexToken.compareTo("mayor_igual")==0 || lexToken.compareTo("menor")==0 || lexToken.compareTo("menor_igual")==0){
				String op = this.operador();
				String t1 = this.exp_simple();
				if (!compatibles(tipo,t1)) {
					//throw new Exception("Error sintaxis: tipos no compatibles.");
					Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
				}
				this.emite(op);
				// Si todo va bien, el tipo cambia a boolean por ser relaccional.
				return "boolean";
		} else { 
			return tipo;}
	}
	
	/**Método que reconoce una expresion simple.
	 * 
	 * @return Se devuelve un string del token analizado.
	 * @throws Exception Se recoge cualquier tipo de error generado mas abajo en la jerarquia.
	 */
	private String exp_simple() throws Exception {
		String tipo="";
		this.anaLex.predice();
		String lexToken = anaLex.getToken();
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0){
			this.operador();
			tipo = this.termino();
			if (lexToken.compareTo("resta")==0) this.emite("menosN");
		} else {
			tipo = this.termino();
			this.exp_simpleR(tipo);
		}
		return tipo;
	}

	/**Método que reconoce un termino.
	 * 
	 * @return Se devuelve un string con el lexema del termino.
	 * @throws Exception Se recoge cualquier tipo de error que se produzca dentro del termino.
	 */
	private String termino() throws Exception {
		String tipo=this.factor();
		this.terminoR(tipo);
		return tipo;
	}

	/**Método que reconoce un factor.
	 * 
	 * @return Se devuelve un string con el valor del factor.
	 * @throws Exception Se lanzan errores si el identificador no existe.
	 */
	private String factor() throws Exception {
		String tipo="";
		this.anaLex.predice();
		String token = this.anaLex.getToken();
		String lex = this.anaLex.getLex();
		if (token.compareTo("identificador")==0){
			this.id();
			lex = this.anaLex.getLex(); 
			if (!tablaSim.existeID(lex)) 
				throw new Exception("Error sintaxis: ID no declarado"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			tipo = this.tablaSim.devuelveTipo(lex);
			this.emite("apila_dir "+ this.tablaSim.getDir(lex));
		} else if (token.compareTo("lparen")==0){
			this.compara("(");
			tipo = this.expresion();
			this.compara(")");
		} else if (lex.compareTo("not")==0){
			this.anaLex.scanner();
			String l = this.anaLex.getLex();
			tipo = this.factor();
			this.emite(l);
		} else if (lex.compareTo("true")==0 || lex.compareTo("false")==0){
			this.anaLex.scanner();
			tipo = "boolean";
			this.emite("apila "+ this.anaLex.getLex());
		} else if (token.compareTo("num")==0){
			this.anaLex.scanner();
			tipo = "integer";
			this.emite("apila "+ this.anaLex.getLex());
		} else if (token.compareTo("numReal")==0){
			this.anaLex.scanner();
			tipo = "real";
			this.emite("apila "+ this.anaLex.getLex());
		}
		return tipo;
	}

	/**Método que reconoce una parte de un termino. Se creo para evitar la recursion a izquierdas.
	 * 
	 * @param tipo Se pasa por parametro el tipo de la otra parte del termino reconocida para ver si concuerda.
	 * @throws Exception Se lanza una excepcion si los tipos no son compatibles.
	 */
	private void terminoR(String tipo) throws Exception {
		this.anaLex.predice();
		String lexToken = this.anaLex.getToken();
		if (lexToken.compareTo("multiplica")==0 || lexToken.compareTo("divide")==0
				|| lexToken.compareTo("modulo")==0 || lexToken.compareTo("divide_real")==0
				|| lexToken.compareTo("ylogica")==0){
			String op =this.operador();
			String t= this.termino();
			if (!compatibles(tipo,t)) {
				//throw new Exception("Error sintaxis: tipos no compatibles.");
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
			}
			emite(op);
		}
	}

	/**Método que reconoce una parte de una expresion simple. Creado para evitar la recursion a izquierdas.
	 * 
	 * @param tipo0 Se pasa por parametro el tipo para ver si concuerda con el resto de la expresion.
	 * @throws Exception Se lanza una excepcion si los tipos son incompatibles.
	 */
	private void exp_simpleR(String tipo0) throws Exception {
		this.anaLex.predice();
		String lexToken=this.anaLex.getToken();
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0
				|| lexToken.compareTo("ologica")==0){
			String op = this.operador();
			String t1 = this.exp_simple();
			if (!compatibles(tipo0,t1)){
				//throw new Exception("Error sintaxis: tipos no compatibles.");
				Global.setErrorMsg("Violación restricciones. Tipos incompatibles");
			}
			this.emite(op);
		}
	}

	/**Método que reconoce cualquier tipo de operador.
	 * 
	 * @return Se devuelve un string con el lexema del operador.
	 */
	private String operador() {
		this.anaLex.scanner();
		return anaLex.getToken();	
	}
	
	/** Método que reconoce un identificador.
	 * 
	 * @throws Exception Se genera una excepcion si el identificador no es valido.
	 */
	private void id() throws Exception{
		this.anaLex.scanner();
		if(anaLex.getToken().compareTo("identificador")!=0){
			throw new Exception("Error sintaxis: identificador no válido"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}
	}
//*************************************************************************************************
//                             FUNCIONES AUXILIARES
//*************************************************************************************************




	/** Funcion que compara un string dado con el siguiente elemento lexico a analizar.
	 * 
	 * @param tok String a comparar con el token del programa.
	 * @throws Exception 
	 */
	private void compara(String tok) throws Exception{
		anaLex.scanner();
		String lexema = anaLex.getLex();
		if(lexema.compareTo(tok)!=0){
			throw new Exception("Error sintaxis: Programa mal formado"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}
	}
	
	/** Funcion que dice si dos tipos son iguales
	 * 
	 * @param tipo1 Tipo de la primera expresion
	 * @param tipo2 Tipo de la segunda expresion
	 * @return Cierto si los tipos son iguales, y falso si son tipos distintos.
	 */
	private boolean compatibles(String tipo1,String tipo2){
		return (tipo1.compareTo(tipo2)==0);
	}
	

	/**Funcion que se encarga de emitir los distintos tipos de operaciones.
	 * 
	 * @param a String que se pasa por parametro para diferenciar el tipo de operacion a emitir.
	 */
	private void emite(String a){
		this.instrucciones.add(a);
	}

	
	/**public static void main(String[] args) {
		AnalizadorSintactico anaSin = new AnalizadorSintactico("c:/prueba.txt");
		try {
			anaSin.programa();
			System.out.println(Global.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

}
