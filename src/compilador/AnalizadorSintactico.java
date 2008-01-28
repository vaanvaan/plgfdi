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
		this.programa(errorGen);
	}
	
	
	public void programa(boolean err0){
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
		if(this.anaLex.getLex().compareTo(";")!=0){  
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
		this.anaLex.predice();
		if(this.anaLex.getLex().compareTo("var")==0){
			String tipo1 = "";
			ListaID listaID1= null;
			this.variable(tipo1,listaID1);
			tipo0=tipo1;
			listaID1.copiar(listaID0);
			//listaID0=listaID1;
			tipoDecl0="var";
		}else if(this.anaLex.getLex().compareTo("const")==0){
			String tipo1 = "";
			ListaID listaID1=null;
			this.constante(tipo1,listaID1);
			tipo0=tipo1;
			listaID1.copiar(listaID0);
			//listaID0=listaID1;
			tipoDecl0="var";
		}else{
			Error.error("Programa mal formado, deberia declarar una constante o una variable");
		}
	}
	
	public void constante(String tipo0,ListaID listaID0){
		String lex0="";
		String tipo1="";
		String valor = "";
		this.compara("const");
		this.id();
		lex0=this.anaLex.getLex();
		this.compara(":");
		this.tipo(tipo1);
		this.compara("=");
		// TODO hay que subir el valor
		this.valor(valor);
		String val = valor; 
		tipo0=tipo1;
		ListaID listaID = new ListaID();
		listaID.añadeID(lex0);
		listaID.copiar(listaID0);
		//listaID0 = listaID;
	}
	
	public void variable(String tipo0,ListaID listaID0){
		ListaID listaID1=null;
		String tipo1 = "";
		this.compara("var");
		this.lista_id(listaID1);
		this.compara(":");
		this.tipo(tipo1);
		tipo0=tipo1;
		listaID1.copiar(listaID0);
		//listaID0=listaID1;
	}
	
	public void lista_id(ListaID listaID0){
		String lex=" ";
		ListaID listaIDh0 = new ListaID();
		ListaID listaID2=null;
		this.id();
		lex = this.anaLex.getLex();
		listaIDh0.añadeID(lex);
		this.lista_idR(listaIDh0,listaID2);
		listaID2.copiar(listaID0);
		//listaID0=listaID2;
	}
	
	public void lista_idR(ListaID listaIDh0,ListaID listaID0){
		this.anaLex.predice();
		String aux = this.anaLex.getLex();
		if(aux.compareTo(",")==0){
			String lex;
			ListaID listaIDh1=null;
			ListaID listaID1=null;
			this.compara(",");
			this.id();
			lex = this.anaLex.getLex();
			listaIDh0.añadeID(lex);
			listaIDh0.copiar(listaIDh1);
			//listaIDh1=listaIDh0;
			this.lista_idR(listaIDh1,listaID1);
			listaID1.copiar(listaID0);
			//listaID0=listaID1;
		}else{
			listaID0=listaIDh0;
		}
	}
	
	public void tipo(String tipo){
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
		this.anaLex.predice();
		String lexTipo = anaLex.getLex();
		if(lexTipo.compareTo("end")==0){
			err=errh;
		}else{
			boolean err1=false;
			this.lista_proposiciones(err1);
			err = err1 || errh;
		}
	}
	
	public void proposicion(boolean err0){
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		String lexTipo = anaLex.getLex();
		if(lexToken.compareTo("identificador")==0){
			String lex="";
			String tipo1="";
			boolean err1 = false;
			this.id();
			lex = this.anaLex.getLex();
			this.compara(":=");
			this.expresion(err1, tipo1);
			err0=err1 || !this.tablaSim.existeID(lex) || !this.compatibles(this.tablaSim.devuelveTipo(lex), tipo1) || this.tablaSim.tipoDecl(lex)!= "var";
			// TODO emitir
			this.emite("asig");
		}else if(lexTipo.compareTo("read")==0){
			this.compara("read");
			this.compara("(");
			String lex="";
			this.id();
			lex = this.anaLex.getLex();
			this.compara(")");
			err0= !this.tablaSim.existeID(lex);
			// TODO emitir
			this.emite("read");
		}else if(lexTipo.compareTo("write")==0){
			this.compara("write");
			this.compara("(");
			boolean err1 = false;
			String tipo1 ="";
			this.expresion(err1, tipo1);
			err0=err1;
			this.compara(")");
			// TODO emitir
			this.emite("write");
		}else if(lexTipo.compareTo("begin")==0){
			boolean errh = false;
			boolean err1 = false;
			this.proposicion_compuesta(errh, err1);
			err0= err1;
		}else{
			Error.error("Error en la proposicion");
		}
	}
	
	public void expresion(boolean err0, String tipo0){
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("char")==0){
			err0 = false;
			tipo0 = "char";
			this.anaLex.scanner();
			this.emite(anaLex.getLex());
		}else{
			boolean err1=false;
			boolean err2=false;
			String tipo1 = "";
			this.exp_simple(err1, tipo1);
			this.expresionR(err2, tipo1);
			err0 = err1 || err2;
		}
	}
	
	private void expresionR(boolean err0, String tipo0) {
		this.anaLex.predice();
		String lexToken= anaLex.getToken();
		if(lexToken.compareTo("eq")==0 || lexToken.compareTo("ne")==0 || lexToken.compareTo("gt")==0
			|| lexToken.compareTo("ge")==0 || lexToken.compareTo("lt")==0 || lexToken.compareTo("le")==0){
				boolean err1= false;
				String tipo1="";
				String op="";
				this.oprel(op);
				this.exp_simple(err1, tipo1);
				err0=err1 || !tipo0.equals(tipo1);
				this.emite(op);
		} else {
			err0 = false;
		}
	}
	
	private void exp_simple(boolean err0, String tipo0) {
		this.anaLex.predice();
		String lexToken = anaLex.getToken();
		boolean err1=false;
		String tipo1="";
		if (lexToken.compareTo("suma")==0 || lexToken.compareTo("resta")==0){
			String op="";
			this.opsuma(op);
			this.termino(err1, tipo1);
			err0=err1;
			tipo0=tipo1;
			this.emite(op);
		} else {
			boolean err2=false;
			this.termino(err1, tipo1);
			this.exp_simpleR(tipo1, err2);
			err0=err1 || err2;
		}
		
	}

	private void opsuma(String op) {
		this.anaLex.scanner();
		op = anaLex.getLex();	
	}


	private void oprel(String op) {
		this.anaLex.scanner();
		op = anaLex.getLex();	
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
