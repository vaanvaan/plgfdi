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
	
	public AnalizadorSintactico(String path){
		anaLex = new AnalizadorLexico(path);
		this.programa();
	}
	
	public void programa(){
		this.compara("program");
		this.identificador();
		this.compara(";");
		// TODO crear tabla de simbolos
		this.declaraciones();
		//errh2<-errh1
		this.proposicion_compuesta();
		//err0<-err1
		System.out.print("programa correcto");
	}
	public void identificador(){
		anaLex.scanner();
	}
	public void proposicion_compuesta(){}
	public void declaraciones(){}
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
