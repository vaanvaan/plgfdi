/**
 * 
 */
package compilador;

/**
 * @author DaNieLooP
 *
 */
public class Error {
	
	public static void error(String message){
		System.out.println(message);
		System.out.println("Error en la línea: "+ Global.getLinea());
		System.exit(-1);
	}

}
