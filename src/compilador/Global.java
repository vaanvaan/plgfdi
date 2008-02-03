/**
 * 
 */
package compilador;

/**Clase que contiene variables globales tales como el numero de linea y de columna.
 * 
 * @author DaNieLooP
 *
 */
public class Global {
	
	private static int numLinea = 0;
	private static int numCol = 0;
	private static int globalPos = 0;
	private static String errorMsg = "";
	private static boolean error = false;
	
	public static void inicializa(){
		numLinea = 0;
		numCol = 0;
		globalPos = 0;
		errorMsg = "";
		error = false;
	}
	public static void aumentaNumLinea(){
		numLinea++;
	}
	public static void aumentaCol(){
		numCol++;
	}
	public static int getLinea(){
		return numLinea;
	}
	public static int getColumna(){
		return numCol;
	}
	public static void setColumna(int col){
		numCol = col;
	}
	public static String getErrorMsg(){
		return errorMsg;
	}
	public static void setErrorMsg(String msg){
		errorMsg = errorMsg + msg +": línea "+ (numLinea+1) + ", columna "+ (numCol-1) +'\n';
		error = true;
	}
	public static void aumenteGlobalPos(){
		globalPos++;
	}
	public static int getGlobalPos(){
		return globalPos;
	}
	public static void setGlobalPos(int pos){
		globalPos = pos;
	}
	public static boolean getError() {
		return error;
	}

}
