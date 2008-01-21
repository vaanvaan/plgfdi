/**
 * 
 */
package compilador;

/**
 * @author DaNieLooP
 *
 */
public class Error {
	
	public static void error(){
		System.out.println("Error en la línea: "+ Global.getLinea());
		System.exit(-1);
	}

}
