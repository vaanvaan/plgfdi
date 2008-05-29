/**
 * 
 */
package compilador;

import java.util.ArrayList;
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
	private PilaTS pilaTablaSim;
	/**
	 * Vector que almacena el conjunto de instrucciones emitidas hasta el momento.
	 */
	private Vector instrucciones;
	/**
	 * String con el path del archivo que se tiene que escribir.
	 */
	private String pathDestino;
	
	private int n;
	
	private int dir;
	
	private int etq;
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
		try{
			this.compara("program");
			this.id(); 
			this.compara(";");
			this.pilaTablaSim.creaTS();
			this.dir = 0;
			this.n = 0;
			this.etq = 0;
			this.bloqueDecls();
			this.proposicion_compuesta(); 
			inicio(n,dir);
			ir-a(etq);
			this.compara(".");
			this.anaLex.scanner();
			this.emite(this.anaLex.getToken());
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		if(!Global.getError()){
			try
	        {
	            FileWriter fichero = new FileWriter(pathDestino);
	            PrintWriter pw = new PrintWriter(fichero);
	            for (int i = 0; i < instrucciones.size(); i++)
	                pw.println(instrucciones.elementAt(i));
	            pw.close();
	            System.out.println("Compilado satisfactoriamente.");
	        } catch (Exception e)
	        {
	        	System.out.println(e.getMessage());
	        }
		}
		
	}
	
	private void bloqueDecls()throws Exception{
		this.declaraciones();
		this.dec_Procs();
	}
	
	/**
	 * Método que reconoce la parte de las declaraciones de un programa escrito en Pascal.
	 * 
	 * @throws Exception Se recoge cualquier error lanzado dentro de las declaraciones.
	 */
	private void declaraciones() throws Exception{
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0||this.anaLex.getLex().compareTo("const")==0){  
			this.declaracion();
			this.declaracionesR();
		}
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
			this.declaracion();
			this.declaracionesR();
		}
	}
	
	/**Método que reconoce una declaracion, ya sea constante o variable.
	 * 
	 * @throws Exception Recoge cualquier error generado dentro de la declaracion.
	 */
	private void declaracion() throws Exception{
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0){
			this.variable(); 
		}else if(this.anaLex.getLex().compareTo("const")==0){
			Tupla t = new Tupla(2);
			t = this.constante();
			this.pilaTablaSim.añadeIDcima((String)t.getnTupla(0), "const",(Propiedades)t.getnTupla(1));
			dir = dir + ((Propiedades)t.getnTupla(1)).getTam();
		}
	}
	
	/**Método que se encarga de reconocer una constante.
	 * 
	 * @return Se devuelve una tupla que se ira pasando de un metodo a otro y al final se añadira a la tabla de simbolos.
	 * 
	 * @throws Exception Se encarga de recoger cualquier error ocurrido en el reconocimiento de la constante.
	 */
	private Tupla constante() throws Exception{
		Tupla t = new Tupla(2);
		this.compara("const");
		String lex0 = this.id();
		if (this.pilaTablaSim.getTSnivel(n).existeID(lex0)) 
			throw new Exception("Error sintaxis: ID ya existente"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');		
		t.setnTupla(0, lex0);
		this.compara("=");
		Tupla rValor = new Tupla(2);
		rValor = this.valor();
		Propiedades p = new Propiedades();
		p.setT((String)rValor.getnTupla(0));
		p.setValor(valorDe((String)rValor.getnTupla(1)));
		p.setTam(1);
		p.setDir(dir);
		p.setNivel(n);
		t.setnTupla(1, p);
		return t;
	}
	
	/**Método que se encarga de reconocer una variable.
	 * 
	 * @return Se devuelve una tupla que se ira pasando de un metodo a otro y al final se añadira a la tabla de simbolos.
	 * 
	 * @throws Exception Se encarga de recoger cualquier error ocurrido en el reconocimiento de la variable.
	 */
	private void variable() throws Exception{
		this.compara("var");
		//TODO CACAO MARAVILLAO
		/*
		 *  1º comprobamos sintaxis correcta (var Lista_ID : Tipo ;)Necesitamos parámetros sintetizados de Tipo para lanzar Lista_ID
			Y ASI COMO LECHES VERIFICAMOS LA SINTAXIS?????? el : y ;
			2º ejecutamos Tipo()
			3º ejecutamos Lista_ID()
			Tipo(out props) 
			Lista_ID(in props)
		 */
	}
	
	/**Método que reconoce una lista de identificadores.
	 * 
	 * 
	 * @return se devuelve una lista con todos los lexemas de los identificadores.
	 * 
	 * @throws Exception Se encarga de recoger cualquier error ocurrido en el reconocimiento de una lista de identificadores.
	 */
	private void lista_id(Propiedades props) throws Exception{
		String lex = this.id();
		if (this.pilaTablaSim.getTSnivel(n).existeID(lex)) 
			throw new Exception("Error sintaxis: ID ya existente"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		props.setDir(dir);
		props.setNivel(n);
		this.pilaTablaSim.añadeIDcima(lex, "var", props);
		this.lista_idR(props);
	}
	
	////////////////////////////////////////////////////////////////
	
	/**Método que reconoce parte de una secuencia de identificadores. Se creo para evitar la recursion a izquierdas en
	 * el metodo lista_id
	 * 
	 * @param listaIDh0 Lista que se pasa por parametro para agregar mas identificadores.
	 * @return Se devuelve una lista con los lexemas de los identificadores.
	 * @throws Exception Se recoge cualquier tipo de error ocurrido dentro de esta parte de la lista de identificadores.
	 */
	private void lista_idR(Propiedades props) throws Exception{
		this.anaLex.predice();
		String aux = this.anaLex.getToken();
		if(aux.compareTo("coma")==0){
			this.compara(",");
			String lex = this.id();
			if (this.pilaTablaSim.getTSnivel(n).existeID(lex)) 
				throw new Exception("Error sintaxis: ID ya existente"
						+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
			props.setDir(dir);
			props.setNivel(n);
			dir = dir + props.getTam();
			this.pilaTablaSim.añadeIDcima(lex, "var", props);
			this.lista_idR(props);
		}
	}
	
		
	/**Método que sirve para reconocer el valor de una constante.
	 * 
	 * @param tipo Se pasa por parametro el tipo para ver si concuerda con el valor.
	 * @return Se devuelve un String con el valor de la constante.
	 * @throws Exception Se genera una excepcion si el tipo y el valor de una constante no concuerdan.
	 */
	private Tupla valor() throws Exception{
		Tupla t = new Tupla(2);
		anaLex.scanner();
		String token = anaLex.getToken();
		String lex = anaLex.getLex();
		if(token.compareTo("num")==0){
			t.setnTupla(0,"integer");
			t.setnTupla(1, lex);
		}else if(lex.compareTo("true")==0 || lex.compareTo("false")==0){
			t.setnTupla(0,"boolean");
			t.setnTupla(1, lex);
		}else if(token.compareTo("numReal")==0){
			t.setnTupla(0,"real");
			t.setnTupla(1, lex);
		}else if(token.compareTo("char")==0){
			t.setnTupla(0,"char");
			t.setnTupla(1, lex);;
		}else{
			//throw new Exception("Error sintaxis: valor no corresponde con tipo.");
			Global.setErrorMsg("Violación restricciones. Tipo no encontrado.");
		}
		return t;
	}
	
	private Propiedades tipo()throws Exception{
		this.anaLex.predice();
		String lex = this.anaLex.getLex();
		if(lex.compareTo("boolean")==0){
			return this.tipo_estandar();
		}else if(lex.compareTo("integer")==0){
			return this.tipo_estandar();
		}else if(lex.compareTo("real")==0){
			return this.tipo_estandar();
		}else if(lex.compareTo("char")==0){
			return this.tipo_estandar();
		}else{
			return this.tipo_construido();
		}
	}
	
	private Propiedades tipo_estandar()throws Exception{
		anaLex.scanner();
		Propiedades p = new Propiedades();
		String token = anaLex.getToken();
		String lex = anaLex.getLex();
		if(lex.compareTo("boolean")==0){
			p.setT("boolean");
			p.setTam(1);
		}else if(lex.compareTo("integer")==0){
			p.setT("integer");
			p.setTam(1);
		}else if(lex.compareTo("real")==0){
			p.setT("real");
			p.setTam(1);
		}else if(lex.compareTo("char")==0){
			p.setT("char");
			p.setTam(1);
		}else{
			//throw new Exception("Error sintaxis: valor no corresponde con tipo.");
			Global.setErrorMsg("Violación restricciones. Tipo no encontrado.");
		}
		return p;
	}
	
	private Propiedades tipo_construido()throws Exception{
		anaLex.predice();
		String lex = this.anaLex.getLex();
		Propiedades p = new Propiedades();
		if(lex.compareTo("array")==0){
			this.compara("array");
			this.compara("[");
			int longt = this.subrango();
			this.compara("]");
			this.compara("of");
			Propiedades p1 = this.tipo();
			p.setT("array");
			p.setN(longt);
			p.setTbase(p1);
			p.setTam(1);
		}else if(lex.compareTo("^")==0){
			this.compara("^");
			Propiedades p2 = this.tipo();
			p.setT("pointer");
			p.setTbase(p2);
			p.setTam(1);
		}else if(lex.compareTo("record")==0){
			this.compara("record");
			Tupla t = this.campos();
			this.compara("end");
			p.setT("reg");
			p.setCampos((ArrayList<CCampos>) t.getnTupla(0));
			p.setTam(Integer.parseInt(t.getnTupla(1).toString()));
		}else{
			//throw new Exception("Error sintaxis: Tipo incorrecto.");
			Global.setErrorMsg("Violación restricciones. Tipo incorrecto");
		}
		return p;
	}
	
	private Tupla campos(){
		
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
				Global.setErrorMsg("Violación restricciones. Asignación incorrecta");
				throw new Exception("Error sintaxis: Asignación incorrecta"+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
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
			//this.emite("apila_dir "+ this.tablaSim.getDir(lex));
			this.emite("read");
			this.emite("desapila_dir "+ this.tablaSim.getDir(lex));
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
		if(lexToken.compareTo("igual")==0 || lexToken.compareTo("distintos")==0 || lexToken.compareTo("mayor")==0
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
		try{
		this.anaLex.scanner();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return anaLex.getToken();
	}
	
	
	/** Método que reconoce un identificador.
	 * 
	 * @throws Exception Se genera una excepcion si el identificador no es valido.
	 */
	private String id() throws Exception{
		this.anaLex.scanner();
		if(anaLex.getToken().compareTo("identificador")!=0){
			throw new Exception("Error sintaxis: identificador no válido"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}else{
			return this.anaLex.getLex();
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
