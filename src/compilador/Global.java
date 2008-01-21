/**
 * 
 */
package compilador;

/**
 * @author DaNieLooP
 *
 */
public class Global {
	
	private static int numLinea=1;
	public static void inicializa(){
		numLinea = 1;
	}
	public static void aumentaNumLinea(){
		numLinea++;
	}
	public static int getLinea(){
		return numLinea;
	}

}
