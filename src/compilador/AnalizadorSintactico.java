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
		
	}
	
	public void identificador(String lex){
		anaLex.scanner();
		lex = anaLex.getLex();
	}
	public void proposicion_compuesta(boolean err0,boolean err1){}
	public void declaraciones(){}
	
	
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
