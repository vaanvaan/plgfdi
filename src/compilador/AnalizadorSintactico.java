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
	}
	
	private void declaraciones() throws Exception{
		Tupla decs = this.declaracion(); // devuelve listaID, tipoDec, tipo, valor
		this.compara(";");
		tablaSim.añadeLista((ListaID)decs.getnTupla(0), (String)decs.getnTupla(1), 
							(String)decs.getnTupla(2), (String)decs.getnTupla(3));
		this.declaracionesR();
	}
	
	private void declaracionesR() throws Exception{
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0||this.anaLex.getLex().compareTo("const")==0){  
			Tupla t1 = this.declaracion();
			this.compara(";");
			tablaSim.añadeLista((ListaID)t1.getnTupla(0), (String)t1.getnTupla(1), 
					(String)t1.getnTupla(2), (String)t1.getnTupla(3));
			this.declaracionesR();
		}
	}
	
	private Tupla declaracion() throws Exception{
		Tupla dec = new Tupla(4);
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
		}else{
			throw new Exception("Error sintaxis: No hay declaraciones.");
		}
		return dec;
	}
	
	private Tupla constante() throws Exception{
		Tupla t = new Tupla(3);
		this.compara("const");
		this.id();
		String lex0=this.anaLex.getLex();
		if (this.tablaSim.existeID(lex0)) throw new Exception("Error sintaxis: ID ya existente.");
		this.compara(":");
		String tipo1 = this.tipo();
		this.compara("=");
		String valor = this.valor(tipo1); 
		ListaID listaID = new ListaID();
		listaID.añadeID(lex0);
		t.setnTupla(0, listaID);
		t.setnTupla(1, tipo1);
		t.setnTupla(2, valor);
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
		if (this.tablaSim.existeID(lex)) throw new Exception("Error sintaxis: ID ya existente.");
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
			if (this.tablaSim.existeID(lex)) throw new Exception("Error sintaxis: ID ya existente.");
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
			throw new Exception("Error sintaxis: Tipo incorrecto.");
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
			throw new Exception("Error sintaxis: valor no corresponde con tipo.");
		}
	}

// FIN PARTE DECLARACIONES
// HASTA AQUÍ ESTÁ BIEN COMPLETO
	
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
				throw new Exception("Error sintaxis: Asignación incorrecta.");
			}
			this.emite("asig");
		}else if(lexTipo.compareTo("read")==0){
			this.compara("read");
			this.compara("(");
			this.id();
			String lex = this.anaLex.getLex();
			if (!this.tablaSim.existeID(lex)) throw new Exception("Error sintaxis: ID no declarado.");
			this.compara(")");
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
			this.emite(anaLex.getLex());
		}else{
			t=this.exp_simple();
			t=this.expresionR(t);
		}
		return t;
	}
	
	private String expresionR(String tipo) throws Exception {
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("eq")==0 || lexToken.compareTo("ne")==0 || lexToken.compareTo("gt")==0
			|| lexToken.compareTo("ge")==0 || lexToken.compareTo("lt")==0 || lexToken.compareTo("le")==0){
				String op = this.operador();
				String t1 = this.exp_simple();
				if (!compatibles(tipo,t1)) throw new Exception("Error sintaxis: Tipos no compatibles.");
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
			String op = this.operador();
			tipo = this.termino();
			this.emite(op);
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
			if (!tablaSim.existeID(lex)) throw new Exception("Error sintaxis: ID no declarado.");
			tipo = this.tablaSim.devuelveTipo(lex);
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
			this.emite(this.anaLex.getLex());
		} else if (token.compareTo("num")==0){
			this.anaLex.scanner();
			tipo = "integer";
			this.emite(this.anaLex.getLex());
		} else if (token.compareTo("numReal")==0){
			this.anaLex.scanner();
			tipo = "real";
			this.emite(this.anaLex.getLex());
		}
		return tipo;
	}


	private void terminoR(String tipo) throws Exception {
		this.anaLex.predice();
		String lexTipo = this.anaLex.getLex();
		String lexToken = this.anaLex.getToken();
		if (lexToken.compareTo("prod")==0 || lexTipo.compareTo("div")==0
				|| lexTipo.compareTo("mod")==0 || lexToken.compareTo("DivReal")==0
				|| lexTipo.compareTo("and")==0){
			String op =this.operador();
			String t= this.termino();
			if (!compatibles(tipo,t)) throw new Exception("Error sintaxis: Tipos no compatibles.");
			emite(op);
		}
	}

	private void exp_simpleR(String tipo0) throws Exception {
		this.anaLex.predice();
		String lexToken=this.anaLex.getToken();
		String lexTipo= this.anaLex.getLex();
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0
				|| lexTipo.compareTo("or")==0){
			String op = this.operador();
			String t1 = this.exp_simple();
			if (!compatibles(tipo0,t1)) throw new Exception("Error sintaxis: tipos no compatibles.");
			this.emite(op);
		}
	}

	private String operador() {
		this.anaLex.scanner();
		return anaLex.getLex();	
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
			throw new Exception("Error sintaxis: Programa mal formado.");
		}
	}
	
	private boolean compatibles(String tipo1,String tipo2){
		return (tipo1.compareTo(tipo2)==0);
	}
	
	
	private void id() throws Exception{
		this.anaLex.scanner();
		if(anaLex.getToken().compareTo("identificador")!=0){
			throw new Exception("Error sintaxis: identificador no válido.");
		}
	}
	
	private void emite(String a){
		System.out.println(a);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AnalizadorSintactico anaSin = new AnalizadorSintactico("c:/prueba.txt");
		try {
			anaSin.programa();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
