/**
 * 
 */
package compilador;

import java.util.Scanner;

/**
 * @author DaNieLooP
 *
 */
public class Compiler {

	public void compile(String path,String path2){
		AnalizadorSintactico anaSin = new AnalizadorSintactico(path,path2);
		try {
			anaSin.programa();
			// TODO Si Global.getError()==true ---> No generar fichero. Borrarlo.
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
	}

}
