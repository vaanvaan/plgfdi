/**
 * 
 */
package compilador;

/**
 * @author DaNieLooP
 *
 */
public class AnalizadorSintactico {
	AnalizadorLexico anaLex;
	TS tablaSim;
	
	public AnalizadorSintactico(String path){
		anaLex = new AnalizadorLexico(path);
		tablaSim = new TS();
	}
	
	
	public void programa() throws Exception{
		this.compara("program");
		this.id(); 
		this.compara(";");
		this.declaraciones();
		this.proposicion_compuesta(); 
		this.compara(".");
		this.anaLex.scanner();
		this.emite(this.anaLex.getToken());
	}
	
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
	
	private void proposicion_compuesta() throws Exception{
		this.compara("begin");
		this.proposiciones_optativas();
		this.compara("end"); 
	}
	
	private void proposiciones_optativas() throws Exception{
		this.anaLex.predice();
		String lex = this.anaLex.getLex();
		if (lex.compareTo("end")!=0){
			// Si no es programa vacío, seguimos.
			this.lista_proposiciones();
		}
	}
	
	private void lista_proposiciones() throws Exception{
		this.proposicion();
		this.compara(";");
		this.lista_proposicionesR();
	}
	
	private void lista_proposicionesR() throws Exception{
		this.anaLex.predice();
		String lexTipo = anaLex.getLex();
		if(lexTipo.compareTo("end")!=0){
			// Ya no hay más proposiciones.
			this.lista_proposiciones();
		}
	}
	
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
	
	private String exp_simple() throws Exception {
		String tipo="";
		this.anaLex.predice();
		String lexToken = anaLex.getToken();
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0){
			this.operador();
			tipo = this.termino();
			if (lexToken.compareTo("resta")==0) this.emite("negativo");
		} else {
			tipo = this.termino();
			this.exp_simpleR(tipo);
		}
		return tipo;
	}

	private String termino() throws Exception {
		String tipo=this.factor();
		this.terminoR(tipo);
		return tipo;
	}


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

	private String operador() {
		this.anaLex.scanner();
		return anaLex.getToken();	
	}
	
//*************************************************************************************************
//                             FUNCIONES AUXILIARES
//*************************************************************************************************




	/** Funcion que compara un string dado con el siguiente elemento lexico a analizar.
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
	
	private boolean compatibles(String tipo1,String tipo2){
		return (tipo1.compareTo(tipo2)==0);
	}
	
	
	private void id() throws Exception{
		this.anaLex.scanner();
		if(anaLex.getToken().compareTo("identificador")!=0){
			throw new Exception("Error sintaxis: identificador no válido"
					+ ": línea "+ (Global.getLinea()+1) + ", columna "+ (Global.getColumna()-1) +'\n');
		}
	}
	
	private void emite(String a){
		// TODO Escribir en archivo
		System.out.println(a);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Pedir fichero fuente y destino
		AnalizadorSintactico anaSin = new AnalizadorSintactico("c:/prueba.txt");
		try {
			anaSin.programa();
			// TODO Si Global.getError()==true ---> No generar fichero. Borrarlo.
			System.out.println(Global.getErrorMsg());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
