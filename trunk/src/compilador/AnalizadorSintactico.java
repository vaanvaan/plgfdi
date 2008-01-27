/**
 * 
 */
package compilador;

/**
 * @author DaNieLooP
 *
 */
public class AnalizadorSintactico {
	String lexema;
	AnalizadorLexico anaLex;
	TS tablaSim;
	boolean errorGen=false;
	
	public AnalizadorSintactico(String path){
		anaLex = new AnalizadorLexico(path);
		tablaSim = new TS();
		this.programa(errorGen);
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
	public void programa(boolean err0){
		String lex;
		boolean errh1 = false;
		boolean errh2 = false;
		boolean err1 = false;
		this.compara("program");
		this.id(); //
		this.compara(";");
		this.declaraciones(errh1); //	
		errh2=errh1;	
		this.proposicion_compuesta(errh2,err1); //
		err0=err1;
		this.compara(".");
	}
	
	public void declaraciones(boolean err0){
		boolean errh1 = false;
		boolean err1 = false;
		int dir1 = 0;
		int dirh1 = 0;
		String tipo="";
		ListaID lista=null;
		String tipoDec="";
		this.declaracion(tipo,lista,tipoDec);
		errh1 = false;
		dirh1 = 0;
		this.compara(";");
		this.declaracionesR(dirh1,errh1,err1,dir1);
		err0=err1;
		tablaSim.añadeLista(lista, tipoDec, tipo, dirh1);
	}
	
	public void declaracionesR(int dirh0,boolean errh0,boolean err0,int dir0){
		
		/**
		 * Si el lexema anterior era ; es que no queda nada por cubrir
		 */
		if(this.lexema.compareTo(";")!=0){  
			int dirh1 = 0;
			boolean errh1 = false;
			boolean err1 = false;
			String tipo = "";
			String tipoDecl= "";
			ListaID listaID = null;
			int dir1 = 0;
			this.declaracion(tipo,listaID,tipoDecl);
			dirh1 = dirh0 + 1;
			errh1 = (errh0 || this.tablaSim.existeID(listaID));
			this.compara(";");
			this.declaracionesR(dirh1, errh1, err1, dir1);
			err0 = err1;
			dir0 = dir1;
			this.tablaSim.añadeLista(listaID, tipoDecl, tipo, dirh1);
		}else{
			err0=errh0;
			dir0=dirh0;
		}
		
	}
	public void declaracion(String tipo0,ListaID listaID0,String tipoDecl0){
		this.anaLex.scanner();
		if(this.lexema.compareTo("var")==0){
			String tipo1 = "";
			ListaID listaID1= null;
			this.variable(tipo1,listaID1);
			tipo0=tipo1;
			listaID0=listaID1;
			tipoDecl0="var";
		}else if(this.lexema.compareTo("const")==0){
			String tipo1 = "";
			ListaID listaID1=null;
			this.constante(tipo1,listaID1);
			tipo0=tipo1;
			listaID0=listaID1;
			tipoDecl0="var";
		}else{
			Error.error("Programa mal formado, deberia declarar una constante o una variable");
		}
	}
	
	public void constante(String tipo0,ListaID listaID0){
		String lex0="";
		String tipo1="";
		String valor = "";
		//he subido la comparacion de const a declaracion
		this.id();
		lex0=this.lexema;
		this.compara(":");
		this.tipo(tipo1);
		this.compara("=");
		// TODO hay que subir el valor
		this.valor(valor);
		String val = valor; 
		tipo0=tipo1;
		ListaID listaID = new ListaID();
		listaID.añadeID(lex0);
		listaID0 = listaID;
	}
	
	public void variable(String tipo0,ListaID listaID0){
		ListaID listaID1=null;
		String tipo1 = "";
		//He subido la comparacion de var a declaracion
		this.lista_id(listaID1);
		this.compara(":");
		this.tipo(tipo1);
		tipo0=tipo1;
		listaID0=listaID1;
	}
	
	public void lista_id(ListaID listaID0){
		String lex="";
		ListaID listaIDh0 = new ListaID();
		ListaID listaID2=null;
		this.id();
		lex = this.lexema;
		listaIDh0.añadeID(lex);
		this.lista_idR(listaIDh0,listaID2);
		listaID0=listaID2;
	}
	
	public void lista_idR(ListaID listaIDh0,ListaID listaID0){
		this.anaLex.scanner();
		String aux = this.lexema;
		if(aux.compareTo(",")==0){
			String lex;
			ListaID listaIDh1=null;
			ListaID listaID1=null;
			this.id();
			lex = this.lexema;
			listaIDh0.añadeID(lex);
			listaIDh1=listaIDh0;
			this.lista_idR(listaIDh1,listaID1);
			listaID0=listaID1;
		}else{
			listaID0=listaIDh0;
		}
	}
	
	public void tipo(String tipo){
		anaLex.scanner();
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
			// FIXME aqui no se propaga error pero si no es ninguno es error lexico
			Error.error("Programa mal construido, el tipo no existe");
		}
	}
	
	public void valor(String tipo){
		anaLex.scanner();
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
			// TODO aqui no se propaga error pero si no es ninguno es error lexico
			Error.error("Programa mal construido, el tipo no existe");
		}
	}
	
	public void proposicion_compuesta(boolean errh, boolean err0){
		boolean err1=false;
		this.compara("begin");
		this.proposiciones_optativas(err1);
		this.compara("end");
		err0=err1 || errh;
	}
	
	public void proposiciones_optativas(boolean err0){
		boolean err1 = false;
		this.lista_proposiciones(err1);
		err0=err1;
	}
	
	public void lista_proposiciones(boolean err0){
		boolean err1 = false;
		boolean err2 = false;
		boolean err3 = false;
		this.proposicion(err1);
		this.compara(";");
		err2 = err1;
		this.lista_proposicionesR(err2,err3);
		err0=err3;
	}
	
	public void lista_proposicionesR(boolean errh, boolean err){
		// TODO tracatra, aqui tambien hay que ver por que rama entra
		if(this.compara(" ")){
			err=errh;
		}else{
			boolean err1=false;
			this.lista_proposiciones(err1);
			err = err1 || errh;
		}
	}
	
	public void proposicion(boolean err0){
		if(){	
			String lex;
			boolean err1= false;
			this.id(lex);
			this.compara(":=");
			this.expresion(err1);
			err0=err1 || !existeID(lex) || !compatibles(ts[lex].tipo,expresion.tipo)||tipoDecl(lex)!=var;
			emite(asig);
			desapila-dir(ts[lex].dir);
		}else if(){
			boolean err1= false;
			proposicion_compuesta(err1);
			err0 = err1;
		}else if(){
			String lex;
			this.compara("read");
			this.compara("(");
			this.id(lex);
			this.compara(")");
			err0=existeID(lex);
			emite("Read");
		}else if(){
			String lex;
			boolean err1 = false;
			this.compara("write");
			this.compara("(");
			this.expresion(err1);
			err0=err1;
			this.compara(")");
			emite("Write");
		}
	}
	

	
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
	
	
	
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AnalizadorSintactico anaSin = new AnalizadorSintactico("c:/prueba.txt");

	}

}
