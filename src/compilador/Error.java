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
		System.out.println("Error en la l�nea: "+ Global.getLinea());
		System.exit(-1);
	}

}
