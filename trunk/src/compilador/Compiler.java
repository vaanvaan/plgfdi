/**
 * 
 */
package compilador;

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
		/**
		 * Para que funcione se necesita pasar un argumento por parametro al llamar a compilar.
		 */
		if(args.length==2){
			// TODO pillar 2� path para generar el codigo
			compilador.compile(args[0].toString(),args[1].toString());
		}else{
			System.out.println("Se necesitan dos paths como argumento, 1 con el path del fuente y otro con el destino" +
					"para la creaci�n del bytecode.");
		}
	}

}
