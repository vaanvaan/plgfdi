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
	boolean errorGen=false;
	
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
