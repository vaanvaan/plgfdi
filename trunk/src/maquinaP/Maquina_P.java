package maquinaP;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;

import compilador.AuxFun;
/*
 * Intérprete / simulador de la máquina P. 
 * Este intérprete también funcionará en modo línea de comandos,
 * tomando como argumento el archivo donde está el código P a ejecutar,
 * así como un flag que indicará si la ejecución debe ser en modo normal o en modo traza.
 * Como resultado ejecutará dicho código:
 * 	En modo normal el intérprete no mostrará ningún tipo de traza. De esta forma, 
 * 		el único resultado visible será el producido por las instrucciones específicas de lectura / escritura
 * 		incluidas en el lenguaje de la máquina P.
 * 	En modo traza el intérprete visualizará los distintos pasos de ejecución.
 * 		En cada paso el intérprete: (1) mostrará los contenidos de la pila de ejecución y de las celdas relevantes
 * 		en la memoria de datos, (2) esperará a que el usuario decida proseguir la ejecución, (3) mostrará la instrucción a ejecutar,
 * 		y la ejecutará, avanzando, de esta manera, al siguiente paso. 
 *		En cualquier caso, la traza se mostrará siempre por la consola de la línea de comandos. 
 */
/**
 * 
 * @author GRUPO PLG Nº13
 *
 * Clase principal de la máquina P
 * 
 */
public class Maquina_P {
	private Stack pila = new Stack();
	/*
	 * Habbrá que iicializar la memoria
	 */
	private ArrayList memoria = new ArrayList();
	private ArrayList memoriaref = new ArrayList();
	private Long pc = new Long(0);
	private boolean halt = false;
	private boolean error = false;
	private boolean debug = false;
	private String path;
	
	
	/**
	 * Método que se encarga de pintar el estado de la máquina P cuando el modo debug esté habilitado.
	 * 
	 * @param op	opreración que se aplicará sobre la máquina P.
	 * @param oper	operando que acompaña a la operación.
	 */
	public void print_state(String op, String oper){
		
		if(debug){
			Iterator it = memoriaref.iterator();
			ArrayList memref = new ArrayList();
			Map<String,String> mapmem = new HashMap<String,String>();
			
			System.out.println("=============================");
			System.out.println("OPERACION: "+ op + " " + oper);
			System.out.print("STACK: ");
			System.out.println(pila);
			
			while (it.hasNext()){
				String aux = it.next().toString();
				if (esEntero(aux)){
					int pos = Integer.parseInt(aux);
					mapmem.put("M" + aux,memoria.get(pos).toString());
				}
			}
			System.out.println("MEMORY: " + mapmem);	
			System.out.println("PC = "+pc);
		}
	}
	
	/**
	 * Este método se encarga de inicializar las posiciones dememoria.
	 */
	public void inicializa_mem_pru(){
		for(int i=0;i<100;i++){
			int a = 0;
			memoria.add(a);
		}
	}
	
	/**
	 * Método que se encarga de procesar el fichero de las instrucciones generado por el analizador sintáctico.
	 * Su funcionamiento es símple, como cada instrucción se despliega sobre una linea, tomamos esa linea, y
	 * analizamos a qué fución llamar.
	 */
	public void procesa_programa(){
		String programa = AuxFun.getTextoFichero(path);
		StringTokenizer enLineas = new StringTokenizer(programa,"\r\n");
		ArrayList lineas = new ArrayList();
		while (enLineas.hasMoreTokens()){
			String aux = enLineas.nextToken();
			lineas.add(aux);
		}
		//ahora en comandos y operandos
		ArrayList comandos = new ArrayList();
		Iterator itlineas = lineas.iterator();
		while(itlineas.hasNext()){
			String comando = (String) itlineas.next();
			ComandoVO c = new ComandoVO();
			StringTokenizer enSent = new StringTokenizer(comando," ");
			if(enSent.countTokens() > 2){
				System.out.println("Error demasiados parámetros");
			}else{
				if(enSent.countTokens() == 2){
					c.setAccion(enSent.nextToken());
					c.setOperando(enSent.nextToken());
					comandos.add(c);
				}else{
					c.setAccion(enSent.nextToken());
					c.setOperando(null);
					comandos.add(c);
				}
			}
		}
		//	Procesando los comandos
		Iterator itcomandos = comandos.iterator();
		while(itcomandos.hasNext() && !halt && !error){
			ComandoVO com = (ComandoVO) itcomandos.next();
			String accion = com.getAccion();
			if(debug){
				//esperamos a que nos digan que ejecutemos:
				if(com.getOperando() != null){
					System.out.println("Instruccion: "+ com.getAccion() + " "+com.getOperando().toString());
				}else{
					System.out.println("Instruccion: "+ com.getAccion());
				}
				System.out.println("Seguir? [Y/N]");
				Scanner scan = new Scanner(System.in);
				String cad = scan.nextLine();
				if (cad.equalsIgnoreCase("n")){
					halt = true;
				}
			}
			if(error){
				halt = true;
				System.out.println("ERROR EN EJECUCION");
				System.out.println("PARADA DEL SISTEMA");
			}
			
			if(accion.equalsIgnoreCase("apila")){
				apilar(com.getOperando());
				print_state("apila", com.getOperando());
			}else if(accion.equalsIgnoreCase("apila_dir")){
				apila_dir(Integer.parseInt(com.getOperando()));
				if(!memoriaref.contains(com.getOperando())){
					memoriaref.add(com.getOperando());
				}
				print_state("apila_dir", com.getOperando());
			}else if(accion.equalsIgnoreCase("desapila")){
				desapila();
				print_state("desapila","");
			}else if(accion.equalsIgnoreCase("desapila_dir")){
				desapila_dir(Integer.parseInt(com.getOperando()));
				if(!memoriaref.contains(com.getOperando())){
					memoriaref.add(com.getOperando());
				}
				print_state("desapila_dir",com.getOperando());
			}else if(accion.equalsIgnoreCase("suma")){
				suma();
				print_state("suma","");
			}else if(accion.equalsIgnoreCase("resta")){
				resta();
				print_state("resta","");
			}else if(accion.equalsIgnoreCase("multiplica")){
				multiplica();
				print_state("multiplica","");
			}else if(accion.equalsIgnoreCase("divide")){
				divide();
				print_state("divide","");
			}else if(accion.equalsIgnoreCase("divide_real")){
				divide_real();
				print_state("divide_real","");
			}else if(accion.equalsIgnoreCase("modulo")){
				modulo();
				print_state("modulo","");
			}else if(accion.equalsIgnoreCase("negativo")){
				negativo();
				print_state("negativo","");
			}else if(accion.equalsIgnoreCase("mayor")){
				mayor();
				print_state("mayor","");
			}else if(accion.equalsIgnoreCase("mayor_igual")){
				mayor_igual();
				print_state("mayor_igual","");
			}else if(accion.equalsIgnoreCase("menor")){
				menor();
				print_state("menor","");
			}else if(accion.equalsIgnoreCase("menor_igual")){
				menor_igual();
				print_state("menor_igual","");
			}else if(accion.equalsIgnoreCase("igual")){
				igual();
				print_state("igual","");
			}else if(accion.equalsIgnoreCase("distintos")){
				distintos();
				print_state("distintos","");
			}else if(accion.equalsIgnoreCase("ylogica")){
				ylogica();
				print_state("ylogica","");
			}else if(accion.equalsIgnoreCase("ologica")){
				ologica();
				print_state("ologica","");
			}else if(accion.equalsIgnoreCase("not")){
				notlogico();
				print_state("not","");
			}else if(accion.equalsIgnoreCase("read")){
				reed();
				print_state("read","");
			}else if(accion.equalsIgnoreCase("masN")){
				masN();
				print_state("masN","");
			}else if(accion.equalsIgnoreCase("menosN")){
				menosN();
				print_state("menosN","");
			}else if(accion.equalsIgnoreCase("write")){
				escribir();
				print_state("write","");
			}else if(accion.equalsIgnoreCase("stop")){
				stop();
				print_state("stop","");
			}
			
		}
		if(error){
			halt = true;
			System.out.println("ERROR EN EJECUCION");
			System.out.println("PARADA DEL SISTEMA");
		}
	}
	
	
	/**
	 * Método que realiza la operación de apilar sobre la pila de la máquina
	 * 
	 * @param o	 este es el objeto que se colocará en la cima de la pila.
	 */
	public void apilar(Object o){
		pila.push(o);
		pc++;
	}
	/**
	 * Método que realiza la operación de apilar un elemento almacenado en memoria
	 * 
	 * @param dir	dirección de memoria del que tomamos el dato.
	 */
	public void apila_dir(int dir){
		if ((memoria.size() < dir)){
			//Error -> fuera de memoria paramos la máquina
			halt = true;
			error = true;
			System.out.println("Out of memory!");
		}else{
			Object o = memoria.get(dir);
			pila.push(o);
			pc++;
		}
		
	}
	/**
	 * Método que elimina el elemento situado en la cima de la pila, quedando en la cima
	 * el elemnto inmediatamente inferior. 
	 */
	public void  desapila(){
		if (pila.isEmpty()){
			error = true;
			System.out.println("Pila vaciay!");
		}
		pila.pop();
		pc++;
	}
	
	/**
	 * Método que se encarga de realizar la operación de desapilar el elemto en la cima
	 * de la pila y colocarlo en la posición de memoria especificada.
	 * 
	 * @param dir	posición de memoria donde se guardará del dato de la cima. 
	 */
	public void desapila_dir(int dir){
		if ((memoria.size() < dir) || pila.isEmpty() ){
			halt = true;
			error = true;
			System.out.println("Out of memory! OR Pila vacia!");
		}else{
			String o = pila.pop().toString();
			memoria.set(dir, o);
			pc++;
		}
	}
	
	/**
	 * Método que se encarga de realizar la suma, si procede, de los elementos situados
	 * en la cima y subcima de la pila.
	 */
	public void suma(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = a + b;
				pila.push(a);
				pc++;
			}else{
				if (esReal(cima) && esReal(subcima)){
					Float a = Float.valueOf(cima);
					Float b = Float.valueOf(subcima);
					a = a + b;
					pila.push(a);
					pc++;
				}else{
					error = true; 
					System.out.println("Elemento no numerico");
				}
			}
		}else{
			error = true;
			System.out.println("Pila vacia");
		}
	}
	
	/**
	 * Método que se encarga de realizar la resta, si procede, de los elementos situados
	 * en la cima y subcima de la pila. La resta se realiza dela siguiente forma:
	 * 		RESULTADO = SUBCIMA - CIMA
	 * Donde CIMA y SUBCIMA, hacen referencia a los elementos en dichas posiciones de la pila.
	 * RESULTADO sería el resultado de la operación de resta y sería la nueva cima de la pila.
	 */
	public void resta(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = (String) pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = b - a;
				pila.push(a);
				pc++;
			}else{
				if (esReal(cima) && esReal(subcima)){
					Float a = Float.valueOf(cima);
					Float b = Float.valueOf(subcima);
					a = b - a;
					pila.push(a);
					pc++;
				}else{
					error = true; 
					System.out.println("Elemento no numerico");
				}
			}
		}else{
			error = true;
			System.out.println("Pila vacia");
		}
	}
	
	/**
	 * Método que se encarga de realizar la multiplicación, si procede, de los elementos situados
	 * en la cima y subcima de la pila.
	 */
	public void multiplica(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = a * b;
				pila.push(a);
				pc++;
			}else{
				if (esReal(cima) && esReal(subcima)){
					Float a = Float.valueOf(cima);
					Float b = Float.valueOf(subcima);
					a = a * b;
					pila.push(a);
					pc++;
				}else{
					error = true; 
					System.out.println("Elemento no numerico");
				}
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * Método que se encarga de realizar la división entera, si procede, de los elementos situados
	 * en la cima y subcima de la pila. La divsión se realiza dela siguiente forma:
	 * 		RESULTADO = SUBCIMA / CIMA
	 * Donde CIMA y SUBCIMA, hacen referencia a los elementos en dichas posiciones de la pila.
	 * RESULTADO sería el resultado de la operación de división entera y sería la nueva cima de la pila.
	 */
	public void divide(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = b / a;
				pila.push(a);
				pc++;
			}else{
					error = true; 
					System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * Método que se encarga de realizar la división real, si procede, de los elementos situados
	 * en la cima y subcima de la pila. La divisón se realiza dela siguiente forma:
	 * 		RESULTADO = SUBCIMA / CIMA
	 * Donde CIMA y SUBCIMA, hacen referencia a los elementos en dichas posiciones de la pila.
	 * RESULTADO sería el resultado de la operación de división real y sería la nueva cima de la pila.
	 */
	public void divide_real(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;	
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				a = b / a;
				pila.push(a);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * Método que se encarga de realizar el módulo, si procede, de los elementos situados
	 * en la cima y subcima de la pila. La operacion se realiza dela siguiente forma:
	 * 		RESULTADO = SUBCIMA % CIMA    (mod = %)
	 * Donde CIMA y SUBCIMA, hacen referencia a los elementos en dichas posiciones de la pila.
	 * RESULTADO sería el resultado de la operación de división entera y sería la nueva cima de la pila.
	 */
	public void modulo(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esEntero(cima) && esEntero(subcima)){
				Integer a = Integer.parseInt(cima);
				Integer b = Integer.parseInt(subcima);
				a = b % a;
				pila.push(a);
				pc++;
			}else{
					error = true; 
					System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	/**
	 * Método que se encarga de realizar la operación del operador unario "-"
	 * para indicar que un elemnto es negativo.
	 * Esta operación se aplica sobre el elemento situado en la cima de la pila.
	 */
	public void negativo(){
		if (pila.size() > 0){
			String cima = pila.pop().toString();;
			if(esEntero(cima)){
				int a = Integer.parseInt(cima);
				if(a>0){
					a = a*(-1);
				}
				pila.push(a);
			}else if(esReal(cima)){
				float a = Float.parseFloat(cima);
				if(a>0){
					a = a*(-1);
				}
				pila.push(a);
			}
			pc++;
		}else{
			error = true;
		}
	}
	/**
	 * Método que se encarga de realizar la operación de comparación mayor, aplicándose del
	 * siqguiente modo:
	 * 		??? = SUBCIMA > CIMA
	 */
	public void mayor(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b > a;
				pila.push(c);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	/**
	 * Método que se encarga de realizar la operación de comparación mayor-igual, aplicándose del
	 * siqguiente modo:
	 * 		??? = SUBCIMA >= CIMA
	 */
	public void mayor_igual(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b >= a;
				pila.push(c);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	/**
	 * Método que se encarga de realizar la operación de comparación menor, aplicándose del
	 * siqguiente modo:
	 * 		RESULTADO = SUBCIMA < CIMA
	 */
	public void menor(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b < a;
				pila.push(c);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	/**
	 * Método que se encarga de realizar la operación de comparación menor-igual, aplicándose del
	 * siqguiente modo:
	 * 		RESULTADO = SUBCIMA <= CIMA
	 */
	public void menor_igual(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if (esReal(cima) && esReal(subcima)){
				Float a = Float.valueOf(cima);
				Float b = Float.valueOf(subcima);
				boolean c  = b <= a;
				pila.push(c);
				pc++;
			}else{
				error = true; 
				System.out.println("Elemento no numerico");
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * Método que se encarga de realizar la operación de comparación de igualdad, aplicándose del
	 * siqguiente modo:
	 * 		RESULTADO = SUBCIMA = CIMA
	 */
	public void igual(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			Boolean res = subcima.equals(cima);
			pila.push(res);
			pc++;
		}else{
			error = true;
		}
	}
	
	/**
	 * Método que se encarga de realizar la operación de comparación de desigualdad, aplicándose del
	 * siqguiente modo:
	 * 		RESULTADO = SUBCIMA <> CIMA
	 */
	public void distintos(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			Boolean res = !subcima.equals(cima);
			pila.push(res);
			pc++;
		}else{
			error = true;
		}
	}
	/**
	 * Método que se encarga de realizar la AND lógica, si procede, de los elementos situados
	 * en la cima y subcima de la pila.
	 */
	public void ylogica(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if(esBooleano(cima) && esBooleano(subcima)){
				Boolean a = Boolean.valueOf(cima);
				Boolean b = Boolean.valueOf(subcima);
				Boolean c = a && b;
				pila.push(c);
				pc++;
			}else{
				error = true;
				System.out.println("Error no es booleano.");
			}
		}else{
			error = true;
		}
	}
	/**
	 * Método que se encarga de realizar la OR lógica, si procede, de los elementos situados
	 * en la cima y subcima de la pila.
	 */
	public void ologica(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			String subcima = pila.pop().toString();;
			if(esBooleano(cima) && esBooleano(subcima)){
				Boolean a = Boolean.valueOf(cima);
				Boolean b = Boolean.valueOf(subcima);
				Boolean c = a || b;
				pila.push(c);
				pc++;
			}else{
				error = true;
				System.out.println("Error no es booleano.");
			}
		}else{
			error = true;
		}
	}
	/**
	 * Método que se encarga de realizar el NOT lógico, si procede, del elemento situado
	 * en la cima de la pila.
	 */
	public void notlogico(){
		if (pila.size() > 1){
			String cima = pila.pop().toString();;
			if(esBooleano(cima)){
				Boolean a = Boolean.valueOf(cima);
				pila.push(!a);
				pc++;
			}else{
				error = true;
				System.out.println("Error no es booleano.");
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * Este método se encarga de implementar el comando de lectura.
	 * Se encarga de leer toda la linea que se le introduce por entrada y la almacena en
	 * la cima de la pila.
	 */
	public void reed(){
			System.out.println("MAQ_P/>");
			Scanner scan = new Scanner(System.in);
			String linea = scan.nextLine();
			pila.push(linea);
			pc++;
	}
	/**
	 * Método que se encarga de convertir un número en un número de signo positivo.
	 * Esta operación se aplica sobre el elemento situado en la cima de la pila.
	 */
	public void masN(){
		if (pila.size() > 0){
			String cima = pila.pop().toString();;
			if (esEntero(cima)){
				Integer i = Integer.parseInt(cima);
				if (i < 0){
					i = 1 * i;
				}
				pila.push(i);
				pc++;
			}else{
				if(esReal(cima)){
					Float f = Float.valueOf(cima);
					if (f < 0){
						f = 1*f;
					}
					pila.push(f);
					pc++;
				}
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * Método que se encarga de convertir un número en un número de signo negativo.
	 * Esta operación se aplica sobre el elemento situado en la cima de la pila.
	 */
	public void menosN(){
		if (pila.size() > 0){
			String cima = pila.pop().toString();;
			if (esEntero(cima)){
				Integer i = Integer.parseInt(cima);
				if (i > 0){
					i = -1 * i;
				}
				pila.push(i);
				pc++;
			}else{
				if(esReal(cima)){
					Float f = Float.valueOf(cima);
					if (f > 0){
						f = -1*f;
					}
					pila.push(f);
					pc++;
				}
			}
		}else{
			error = true;
		}
	}
	
	/**
	 * Método que se encarga de implementar la escritura, muestreo del elementto situado
	 * en la cima de la pila.
	 */
	public void escribir(){
		if (!pila.empty()){
			String cima = pila.peek().toString();
			System.out.println(cima);
			pc++;
		}else{
			error = true;
		}
	}
	
	/**
	 * Método que se encarga de parar la máquina P.
	 */
	public void stop(){
		halt = true;
	}
	
	/**
	 * Método que chequea si la máquina está parada.
	 * @return  estado del flag Halt (true/false)
	 */
	public boolean isHalt() {
		return halt;
	}
	/**
	 * Método que se encarga de fijar el flag de parada.
	 * @param halt
	 * Valor que le asignamos al flag.
	 */
	public void setHalt(boolean halt) {
		this.halt = halt;
	}
	/**
	 * Método que nos indica si se ha producido un error en
	 * el procesamieto.
	 * @return (true/false) dependiendo si se ha producido error o no.
	 */
	public boolean isError() {
		return error;
	}

	protected void setError(boolean error) {
		this.error = error;
	}
	/**
	 * Método que indica si se encuentra la máquina en modo debug o no.
	 * @return
	 * (true/false) dependiendo del flag.
	 */
	public boolean isDebug() {
		return debug;
	}
	/**
	 * Método que establece el modo debug.
	 * @param debug  Habilitará o deshabilitará el modo debug.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	/**
	 * Método que informa sobre la ruta del fichero a procesar.
	 * @return  la ruta del fichero.
	 */
	public String getPath() {
		return path;
	}
	/**
	 * Método que estblece el la ruta del fichero a procesar
	 * @param path ruta del fichero.
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/*------------------------------------------------------------------------
	 * Funciones auxiliares de chequeo de tipos
	 * -----------------------------------------------------------------------
	 */
	
	private boolean esEntero(String o){
		try{
			Integer a = Integer.valueOf(o.toString());
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	private boolean esReal(String o){
		try{
			Float f = Float.valueOf(o.toString());
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
	
	private boolean esBooleano(String o){
		try{
			Boolean b = Boolean.valueOf(o.toString());
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static void main(String[] args) {
		String orden;
		boolean debug=false;
		Scanner scanEntrada = new Scanner(System.in);
		do{
			System.out.print("Modo debug si/no> ");
			orden = scanEntrada.nextLine();
		}while(orden.toLowerCase().compareTo("si")!=0&&orden.toLowerCase().compareTo("no")!=0);
		if(orden.toLowerCase().compareTo("si")==0){
			debug = true;
		}
		System.out.print("Path del bytecode> ");
		orden = scanEntrada.nextLine();
		Maquina_P maq = new Maquina_P();
		maq.setPath(orden);
		maq.inicializa_mem_pru();
		maq.setDebug(debug); // <--- modo debug
		maq.procesa_programa();
	}
	
}
