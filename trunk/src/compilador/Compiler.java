/**
 * 
 */
package compilador;

import java.util.Scanner;

import maquinaP.*;

/**Clase principal desde donde se ejecuta el compilador.
 * 
 * @author DaNieLooP
 *
 */
public class Compiler {

	/**Funcion que compila el archivo especificado.
	 * 
	 * @param path Path del archivo a compilar.
	 * @param path2 Path del archivo intermedio que se crea despues de la compilacion.
	 */
	public void compile(String path,String path2){
		AnalizadorSintactico anaSin = new AnalizadorSintactico(path,path2);
		try {
			anaSin.programa();
			System.out.println(Global.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Compiler compilador = new Compiler();
		String r1;
		String r2;
		Scanner scanEntrada = new Scanner(System.in);
		System.out.print("Ruta de entrada> ");
		r1 = scanEntrada.nextLine();
		System.out.print("Ruta de salida> ");
		r2 = scanEntrada.nextLine();
		compilador.compile(r1, r2);
		String orden;
		do{
			System.out.println("Desea ejecutar la maquina-P ahora?");
			orden = scanEntrada.nextLine();
		}while(orden.toLowerCase().compareTo("si")!=0&&orden.toLowerCase().compareTo("no")!=0);
		if(orden.toLowerCase().compareTo("si")==0){
			String ord;
			boolean debug=false;
			do{
				System.out.print("Modo debug si/no> ");
				ord = scanEntrada.nextLine();
			}while(ord.toLowerCase().compareTo("si")!=0&&ord.toLowerCase().compareTo("no")!=0);
			if(ord.toLowerCase().compareTo("si")==0){
				debug = true;
			}
			Maquina_P maq = new Maquina_P();
			maq.setPath(r2);
			maq.inicializa_mem_pru();
			maq.setDebug(debug); // <--- modo debug
			maq.procesa_programa();
		}
		
	}

}
