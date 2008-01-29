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
	boolean errorGen=false;
	
	public AnalizadorSintactico(String path){
		anaLex = new AnalizadorLexico(path);
		tablaSim = new TS();
		boolean errorP = this.programa();
	}
	
	
	public boolean programa(){
		this.compara("program");
		this.id(); //
		this.compara(";");
		boolean errh1 = this.declaraciones(); //	
		boolean err1 = this.proposicion_compuesta(errh1); //
		this.compara(".");
		return err1;
	}
	
	public boolean declaraciones(){
		boolean errh1 = false;
		boolean err1 = false;
		int dir1 = 0;
		int dirh1 = 0;
		Tupla t1 = this.declaracion();
		String tipo = t1.getnTupla(0).toString();
		ListaID lista = ((ListaID) t1.getnTupla(1));
		String tipoDec = t1.getnTupla(2).toString();
		String val = t1.getnTupla(3).toString();
		errh1 = false;
		dirh1 = 0;
		this.compara(";");
		Tupla t2 = this.declaracionesR(dirh1,errh1);
		err1=((Boolean)t2.getnTupla(0)).booleanValue();
		dir1=((Integer)t2.getnTupla(1)).intValue();
		// TODO hay que revisar que dir es la que entra, que creo que da igual, y que creo que tenemos mal
		//porque si declaramos una lista no deberia sumar 1 luego...nose nose
		tablaSim.añadeLista(lista, tipoDec, tipo, val, dirh1);
		return err1;
	}
	
	public Tupla declaracionesR(int dirh0,boolean errh0){
		Tupla t = new Tupla(2);
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0||this.anaLex.getLex().compareTo("const")==0){  
			int dirh1 = 0;
			boolean errh1 = false;
			boolean err1 = false;
			int dir1 = 0;
			Tupla t1 = this.declaracion();
			String tipo = t1.getnTupla(0).toString();
			ListaID listaID = ((ListaID) t1.getnTupla(1));
			String tipoDecl = t1.getnTupla(2).toString();
			String val = t1.getnTupla(3).toString();
			dirh1 = dirh0 + 1;
			errh1 = (errh0 || this.tablaSim.existeID(listaID));
			this.compara(";");
			Tupla t2 = this.declaracionesR(dirh1, errh1);
			err1=((Boolean)t2.getnTupla(0)).booleanValue();
			dir1=((Integer)t2.getnTupla(1)).intValue();
			t.setnTupla(0, err1);
			t.setnTupla(1, dir1);
			this.tablaSim.añadeLista(listaID, tipoDecl, tipo, val, dirh1);
		}else{
			t.setnTupla(0, errh0);
			t.setnTupla(1, dirh0);
		}
		return t;
	}
	
	public Tupla declaracion(){
		Tupla t = new Tupla(4);
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0){
			Tupla t1 = this.variable();
			String tipo1 = t1.getnTupla(0).toString();
			ListaID listaID1 = ((ListaID) t1.getnTupla(1));
			t.setnTupla(0, tipo1);
			t.setnTupla(1, listaID1);
			t.setnTupla(2, "var");
			t.setnTupla(3, "");
		}else if(this.anaLex.getLex().compareTo("const")==0){
			Tupla t2 = this.constante();
			String tipo1 = t2.getnTupla(0).toString();
			ListaID listaID1 = ((ListaID) t2.getnTupla(1));
			String v = t2.getnTupla(0).toString();
			t.setnTupla(0, tipo1);
			t.setnTupla(1, listaID1);
			t.setnTupla(2, "const");
			t.setnTupla(3, v);
		}else{
			Error.error("Programa mal formado, deberia declarar una constante o una variable");
		}
		return t;
	}
	
	public Tupla constante(){
		Tupla t = new Tupla(3);
		this.compara("const");
		this.id();
		String lex0=this.anaLex.getLex();
		this.compara(":");
		String tipo1 = this.tipo();
		this.compara("=");
		// TODO esto no se si es entrada o salida
		String val = this.valor(tipo1); 
		ListaID listaID = new ListaID();
		listaID.añadeID(lex0);
		t.setnTupla(0, tipo1);
		t.setnTupla(1, listaID);
		t.setnTupla(2, val);
		return t;
	}
	
	public Tupla variable(){
		Tupla t = new Tupla(2);
		this.compara("var");
		ListaID l=this.lista_id();
		this.compara(":");
		String tipo1 = this.tipo();
		t.setnTupla(0, tipo1);
		t.setnTupla(1, l);
		return t;
	}
	
	public ListaID lista_id(){
		this.id();
		ListaID l = new ListaID();
		String lex = this.anaLex.getLex();
		l.añadeID(lex);
		return this.lista_idR(l);
	}
	
	public ListaID lista_idR(ListaID listaIDh0){
		this.anaLex.predice();
		String aux = this.anaLex.getLex();
		if(aux.compareTo(",")==0){
			String lex;
			this.compara(",");
			this.id();
			lex = this.anaLex.getLex();
			listaIDh0.añadeID(lex);
			return this.lista_idR(listaIDh0);
		}else{
			return listaIDh0;
		}
	}
	
	public String tipo(){
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
			Error.error("Programa mal construido, el tipo no existe");
		}
		return tipo;
	}
	
	public String valor(String tipo){
		anaLex.scanner();
		String token = anaLex.getToken();
		if(token.compareTo("num")==0 && tipo.compareTo("integer")==0){
			return anaLex.getLex(); 
		}else if((token.compareTo("true")==0 || token.compareTo("false")==0)
					&& tipo.compareTo("boolean")==0){
			return anaLex.getLex();
		}else if(token.compareTo("numReal")==0 && tipo.compareTo("real")==0){
			return anaLex.getLex();
		}else if(token.compareTo("char")==0 && tipo.compareTo("char")==0){
			return anaLex.getLex();
		}else{
			// TODO aqui no se propaga error pero si no es ninguno es error lexico
			Error.error("Programa mal construido, el tipo no existe");
			return "";
		}
	}
	
	public boolean proposicion_compuesta(boolean errh){
		boolean err0;
		this.compara("begin");
		boolean err1 = this.proposiciones_optativas();
		this.compara("end"); 
		err0=err1 || errh;
		return err0;
	}
	
	public boolean proposiciones_optativas(){
		boolean err1=this.lista_proposiciones();
		return err1;
	}
	
	public boolean lista_proposiciones(){
		boolean err0;
		boolean err2 = false;
		boolean err1 = this.proposicion();
		this.compara(";");
		err2 = err1;
		boolean err3 = this.lista_proposicionesR(err2);
		err0=err3;
		return err0;
	}
	
	public boolean lista_proposicionesR(boolean errh){
		this.anaLex.predice();
		boolean err;
		String lexTipo = anaLex.getLex();
		if(lexTipo.compareTo("end")==0){
			err=errh;
		}else{
			boolean err1 = this.lista_proposiciones();
			err = err1 || errh;
		}
		return err;
	}
	
	public boolean proposicion(){
		boolean err0=false;
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		String lexTipo = anaLex.getLex();
		if(lexToken.compareTo("identificador")==0){
			String lex="";
			this.id();
			lex = this.anaLex.getLex();
			this.compara(":=");
			Tupla t1 = this.expresion();
			boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
		    String tipo1 = t1.getnTupla(1).toString();
			err0=err1 || !this.tablaSim.existeID(lex) || !this.compatibles(this.tablaSim.devuelveTipo(lex), tipo1) || this.tablaSim.tipoDecl(lex)!= "var";
			this.emite("asig");
		}else if(lexTipo.compareTo("read")==0){
			this.compara("read");
			this.compara("(");
			String lex="";
			this.id();
			lex = this.anaLex.getLex();
			this.compara(")");
			err0= !this.tablaSim.existeID(lex);
			this.emite("read");
		}else if(lexTipo.compareTo("write")==0){
			this.compara("write");
			this.compara("(");
			Tupla t1 = this.expresion();
			boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
		    String tipo1 = t1.getnTupla(1).toString();
			err0=err1;
			this.compara(")");
			this.emite("write");
		}else if(lexTipo.compareTo("begin")==0){
			boolean errh = false;
			// TODO aqui no se si deberiamos propagar algun error o no
			boolean err1 = this.proposicion_compuesta(errh);
			err0= err1;
		}else{
			Error.error("Error en la proposicion");
			err0=true;
		}
		return err0;
	}
	
	public Tupla expresion(){
		Tupla t = new Tupla(2);
		boolean err0;
		String tipo0="";
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("char")==0){
			err0 = false;
			tipo0 = "char";
			this.anaLex.scanner();
			this.emite(anaLex.getLex());
			t.setnTupla(0, err0);
			t.setnTupla(1, tipo0);
		}else{
			Tupla t1=this.exp_simple();
			boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
		    String tipo1 = t1.getnTupla(1).toString();
			boolean err2 = this.expresionR(tipo1);
			err0 = err1 || err2;
			t.setnTupla(0, err0);
			t.setnTupla(1, tipo1);
		}
		return t;
	}
	
	private boolean expresionR(String tipo0) {
		boolean err0;
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("eq")==0 || lexToken.compareTo("ne")==0 || lexToken.compareTo("gt")==0
			|| lexToken.compareTo("ge")==0 || lexToken.compareTo("lt")==0 || lexToken.compareTo("le")==0){
				String op = this.operador();
				Tupla t1 = this.exp_simple();
				boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
			    String tipo1 = t1.getnTupla(1).toString();
				err0=err1 || !compatibles(tipo0,tipo1);
				this.emite(op);
		} else {
			err0 = false;
		}
	return err0;	
	}
	
	private Tupla exp_simple() {
		Tupla t = new Tupla(2);
		boolean err0;
		String tipo0="";
		this.anaLex.predice();
		String lexToken = anaLex.getToken();
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0){
			String op = this.operador();
			Tupla t1 = this.termino();
			boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
		    String tipo1 = t1.getnTupla(1).toString();
			err0=err1;
			tipo0=tipo1;
			this.emite(op);
		} else {
			Tupla t2 = this.termino();
			boolean err1 = ((Boolean)t2.getnTupla(0)).booleanValue();
		    String tipo1 = t2.getnTupla(1).toString();
			boolean err2 = this.exp_simpleR(tipo1);
			err0=err1 || err2;
		}
		t.setnTupla(0, err0);
		t.setnTupla(1, tipo0);
		return t;
	}

	private Tupla termino() {
		Tupla t = new Tupla(2);
		boolean err0;
		String tipo0;
		Tupla t1=this.factor();
		boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
	    String tipo1 = t1.getnTupla(1).toString();
		boolean err2 = this.terminoR(tipo1);
		err0 = err1 || err2;
		tipo0 = tipo1;	
		t.setnTupla(0, err0);
		t.setnTupla(1, tipo0);
		return t;
	}


	private Tupla factor() {
		Tupla t = new Tupla(2);
		boolean err0=false;
		String tipo0="";
		this.anaLex.predice();
		String token = this.anaLex.getToken();
		String lex = this.anaLex.getLex();
		if (token.compareTo("identificador")==0){
			this.id();
			lex = this.anaLex.getLex(); 
			err0 = this.tablaSim.existeID(lex);
			tipo0 = this.tablaSim.devuelveTipo(lex);
		} else if (token.compareTo("lparen")==0){
			this.compara("(");
			Tupla t1 = this.expresion();
			boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
		    String tipo1 = t1.getnTupla(1).toString();
			err0 = err1; tipo0 = tipo1;
			this.compara(")");
		} else if (lex.compareTo("not")==0){
			this.anaLex.scanner();
			Tupla t1 = this.factor();
			boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
		    String tipo1 = t1.getnTupla(1).toString();
			err0 = err1; tipo0 = tipo1;
			this.emite(this.anaLex.getLex());
		} else if (lex.compareTo("true")==0 || lex.compareTo("false")==0){
			this.anaLex.scanner();
			err0 = false;
			tipo0 = "boolean";
			this.emite(this.anaLex.getLex());
		} else if (token.compareTo("num")==0){
			this.anaLex.scanner();
			err0 = false;
			tipo0 = "integer";
			this.emite(this.anaLex.getLex());
		} else if (token.compareTo("numReal")==0){
			this.anaLex.scanner();
			err0 = false;
			tipo0 = "real";
			this.emite(this.anaLex.getLex());
		}
		t.setnTupla(0, err0);
		t.setnTupla(1, tipo0);
		return t;
	}


	private boolean terminoR(String tipo0) {
		boolean err0;
		this.anaLex.predice();
		String lexToken = this.anaLex.getToken();
		String lexTipo = this.anaLex.getLex();
		if (lexToken.compareTo("prod")==0 || lexToken.compareTo("div")==0
				|| lexToken.compareTo("mod")==0 || lexToken.compareTo("DivReal")==0
				|| lexTipo.compareTo("and")==0){
			String op =this.operador();
			Tupla t1 = this.termino();
			boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
		    String tipo1 = t1.getnTupla(1).toString();
			err0 = err1 || !compatibles(tipo0,tipo1);
			emite(op);
		} else {
			err0 = false;
		}
		return err0;
	}

	private boolean exp_simpleR(String tipo0) {
		boolean err0;
		this.anaLex.predice();
		String lexToken=this.anaLex.getToken();
		String lexTipo= this.anaLex.getLex();
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0
				|| lexTipo.compareTo("or")==0){
			String op = this.operador();
			Tupla t1 = this.exp_simple();
			boolean err1 = ((Boolean)t1.getnTupla(0)).booleanValue();
		    String tipo1 = t1.getnTupla(1).toString();
			err0=err1 || !compatibles(tipo0,tipo1);
			this.emite(op);
		} else {
			err0 = false;
		}
		return err0;
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
	 */
	public void compara(String tok){
		anaLex.scanner();
		String lexema = anaLex.getLex();
		if(lexema.compareTo(tok)!=0){
			Error.error("Programa mal formado");
		}
	}
	
	
	public String valorDe(String lexema){
		return tablaSim.getEntrada(lexema).getValor();
	}
	
	
	public boolean compatibles(String tipo1,String tipo2){
		return (tipo1.compareTo(tipo2)==0);
	}
	
	
	public void id(){
		this.anaLex.scanner();
		if(anaLex.getToken().compareTo("identificador")!=0){
			Error.error("identificador no valido");
		}
	}
	
	public void emite(String a){
		System.out.println(a);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AnalizadorSintactico anaSin = new AnalizadorSintactico("c:/prueba.txt");
	}

}
