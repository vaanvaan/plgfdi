/**
 * 
 */
package compilador;

/**
 * @author DaNieLooP
 *
 */
public class AnalizadorSintactico {
	// TODO hacer global el lexema
	String lexema;
	AnalizadorLexico anaLex;
	boolean errorGen=false;
	// TODO crear TS
	// TODO crear listaID
	public AnalizadorSintactico(String path){
		anaLex = new AnalizadorLexico(path);
		this.programa(errorGen);
	}
	
	public void programa(boolean err0){
		String lex="";
		boolean errh1 = false;
		boolean errh2 = false;
		boolean err1 = false;
		
		this.compara("program");
		this.identificador(lex);
		this.compara(";");
		// TODO crear tabla de simbolos
		this.declaraciones(errh1);
		errh2=errh1;
		this.proposicion_compuesta(errh2,err1);
		err0=err1;
		this.compara(".");
		System.out.print("programa correcto");
	}
	
	public void declaraciones(boolean err0){
		boolean errh1 = false;
		boolean err1 = false;
		int dir1 = 0;
		int dirh1 = 0;
		this.declaracion(tipo,listaID,tipoDecl);
		errh1 = false;
		dirh1 = 0;
		this.compara(";");
		this.declaracionesR(dirh1,errh1,err1,dir1);
		err0=err1;
		// TODO añadir a la lista
	}
	
	public void declaracionesR(int dirh0,boolean errh0,boolean err0,int dir0){
		// TODO hay que ver en cual de los 2 casos estas, en el que hay una declaracion o no hay nada
		if(this.compara(" ")){  
			int dirh1 = 0;
			boolean errh1 = false;
			boolean err1 = false;
			int dir1 = 0;
			this.declaracion(tipo,listaID,tipoDecl);
			dirh1 = dirh0 + 1;
			errh1 = (errh0 || existeID(listaID));
			this.compara(";");
			this.declaracionesR(dirh1, errh1, err1, dir1);
			err0 = err1;
			dir0 = dir1;
			// TODO añadir a la lista
		}else{
			err0=errh0;
			dir0=dirh0;
		}
		
	}
	public void declaracion(String tipo0,lista listaID0,String tipoDecl0){
		if(this.compara(" ")){
			String tipo1 = "";
			lista listaID1;
			this.variable(tipo1,listaID1);
			tipo0=tipo1;
			listaID0=listaID1;
			tipoDecl0="var";
		}else{
			String tipo1 = "";
			lista listaID1;
			this.constante(tipo1,listaID1);
			tipo0=tipo1;
			listaID0=listaID1;
			tipoDecl0="var";
		}
	}
	
	public void constante(String tipo0, lista listaID0){
		String lex0="";
		String tipo1="";
		this.compara("const");
		this.id(String lex0);
		this.compara(":");
		this.tipo(tipo1);
		this.compara("=");
		// TODO hay que subir el valor
		this.valor();
		tipo0=tipo1;
		listaID0=añadeID(lex0,listaVacia());
	}
	
	public void variable(String tipo0,lista listaID0){
		lista listaID1;
		String tipo1 = "";
		this.compara("var");
		this.lista_id(listaID1);
		this.compara(":");
		this.tipo(tipo1);
		tipo0=tipo1;
		listaID0=listaID1;
	}
	
	public void lista_id(lista listaID0){
		String lex="";
		lista listaIDh0;
		lista listaID2;
		this.id(lex);
		listaIDh0=añadeID(lex,listaVacia());
		this.lista_idR(listaIDh0,listaID2);
		listaID0=listaID2;
	}
	
	public void lista_idR(lista listaIDh0,lista listaID0){
		// TODO diferenciar aqui entre si falta algo por meter o es vacio
		if(this.compara(" ")){
			String lex;
			lista listaIDh1;
			lista listaID1;
			this.compara(",");
			this.id(lex);
			listaIDh1=añadeID(lex,listaIDh0);
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
			// TODO aqui no se propaga error pero si no es ninguno es error lexico
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
	
	public void proposicion_compuesta(boolean err0){
		boolean err1=false;
		this.compara("begin");
		this.proposiciones_optativas(err1);
		this.compara("end");
		err0=err1;
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
